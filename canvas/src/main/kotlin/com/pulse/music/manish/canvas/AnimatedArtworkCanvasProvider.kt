package com.pulse.music.manish.canvas

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Animated album artwork (Apple Music motion covers) served as HLS playlists by
 * artwork.m8tec.top. `artist` and `album` are required; `title` is optional.
 *
 * Status codes: 200 = hit, 404 = no artwork for that album, 400 = missing required params.
 */
object AnimatedArtworkCanvasProvider {
  private const val BASE_URL = "https://artwork.m8tec.top/api/v1/artwork/"

  @Serializable
  private data class ArtworkResponse(
    val url: String? = null,
    @SerialName("url_tall") val urlTall: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val isCached: Boolean? = null,
  )

  private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
  }

  private val client by lazy {
    HttpClient(OkHttp) {
      install(ContentNegotiation) { json(json) }
      install(HttpTimeout) {
        connectTimeoutMillis = 10_000
        requestTimeoutMillis = 20_000
        socketTimeoutMillis = 20_000
      }
      expectSuccess = false
    }
  }

  private val cache = ConcurrentHashMap<String, CacheEntry>()

  private data class CacheEntry(val value: CanvasArtwork?, val expiresAtMs: Long)

  private const val CACHE_TTL_MS = 1000L * 60 * 60 * 24

  suspend fun getBySongArtist(
    song: String,
    artist: String,
    album: String? = null
  ): CanvasArtwork? = fetch(song = song, artist = artist, album = album)

  suspend fun getByAlbumArtist(album: String, artist: String): CanvasArtwork? =
    fetch(song = null, artist = artist, album = album)

  private suspend fun fetch(song: String?, artist: String, album: String?): CanvasArtwork? {
    if (artist.isBlank() || album.isNullOrBlank()) return null

    val key =
      listOf("artwork", song.orEmpty(), artist, album).joinToString("|") {
        it.trim().lowercase()
      }
    cache[key]
      ?.takeIf { it.expiresAtMs > System.currentTimeMillis() }
      ?.let {
        return it.value
      }

    val result = request(artist = artist, album = album, title = song?.takeIf { it.isNotBlank() })
    if (result != null) {
      cache[key] = CacheEntry(result, System.currentTimeMillis() + CACHE_TTL_MS)
    }
    return result
  }

  private suspend fun request(artist: String, album: String, title: String?): CanvasArtwork? {
    try {
      val response =
        client.get("${BASE_URL}search") {
          parameter("artist", artist)
          parameter("album", album)
          if (title != null) parameter("title", title)
        }

      when (response.status) {
        HttpStatusCode.NotFound -> {
          println("AnimatedArtwork: 404 no artwork artist='$artist' album='$album'")
          return null
        }
        HttpStatusCode.OK -> Unit
        else -> {
          println(
            "AnimatedArtwork: HTTP ${response.status.value} artist='$artist' album='$album'"
          )
          return null
        }
      }

      val data = response.body<ArtworkResponse>()
      val url = data.url?.takeIf { it.isNotBlank() }
      if (url == null) {
        println("AnimatedArtwork: 200 without url artist='$artist' album='$album'")
        return null
      }

      println("AnimatedArtwork: hit album='${data.album ?: album}' url=$url")
      return CanvasArtwork(
        name = title ?: data.album,
        artist = data.artist ?: artist,
        albumName = data.album ?: album,
        animated = url,
        videoUrl = url,
      )
    } catch (e: Exception) {
      println("AnimatedArtwork: ERROR ${e::class.simpleName}: ${e.message}")
      return null
    }
  }
}
