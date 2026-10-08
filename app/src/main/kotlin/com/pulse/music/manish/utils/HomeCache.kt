package com.pulse.music.manish.utils

import android.content.Context
import com.music.innertube.pages.ExplorePage
import com.music.innertube.pages.HomePage
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Lightweight cold-start cache for the Home/Explore content.
 *
 * The snapshot is written after every successful fresh load and read back
 * before the first network request on subsequent launches, so the app can
 * render cached content immediately and refresh it in the background. No
 * authentication or account secrets are ever written here - only the public
 * Home/Explore render data.
 */
object HomeCache {
  private const val FILE_NAME = "home_explore_snapshot.json"

  private val json =
    Json {
      ignoreUnknownKeys = true
      encodeDefaults = true
    }

  @Serializable
  data class Snapshot(
    val home: HomePage? = null,
    val explore: ExplorePage? = null,
    val timestamp: Long = 0L,
  )

  private fun file(context: Context): File = File(context.cacheDir, FILE_NAME)

  suspend fun read(context: Context): Snapshot? =
    withContext(Dispatchers.IO) {
      try {
        val file = file(context)
        if (!file.exists()) {
          null
        } else {
          json.decodeFromString(Snapshot.serializer(), file.readText())
        }
      } catch (e: Exception) {
        null
      }
    }

  suspend fun write(context: Context, snapshot: Snapshot) {
    withContext(Dispatchers.IO) {
      try {
        file(context).writeText(json.encodeToString(Snapshot.serializer(), snapshot))
      } catch (e: Exception) {
        // Best effort: a failed cache write must never break playback or Home.
      }
    }
  }
}