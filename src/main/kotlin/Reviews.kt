import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.jsoup.Jsoup
import java.net.URI

fun main(args: Array<String>) {
    (1..50).map { scrape("http://www.blabbermouth.net/cdreviews/page/$it") }
      .flatMap { it }
      .sortedByDescending { it.rating }
      .map(::toJson)
      .map(::println)
}

fun scrape(url: String): List<Review> = Jsoup.connect(url).get().run {
    select("article .entry-content").map {
        Review(
          thumb = URI.create(it.selectFirst(".post-thumbnail img").attr("src")),
          artist = it.selectFirst(".review-meta h2 > a").text(),
          album = it.selectFirst(".review-meta h2 > a span").text(),
          rating = it.selectFirst(".review-meta h5").selectFirst("a").text().toDouble()
        )
    }
}.toList()

data class Review(val thumb: URI, val artist: String, val album: String, val rating: Double)

fun toJson(obj: Any): String = ObjectMapper().registerKotlinModule().writeValueAsString(obj)
