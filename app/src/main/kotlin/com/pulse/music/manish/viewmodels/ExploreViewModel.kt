package com.pulse.music.manish.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.filterExplicit
import com.music.innertube.pages.ExplorePage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.pulse.music.manish.constants.HideExplicitKey
import com.pulse.music.manish.db.MusicDatabase
import com.pulse.music.manish.utils.dataStore
import com.pulse.music.manish.utils.get
import com.pulse.music.manish.utils.reportException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ExploreViewModel
@Inject
constructor(
  @ApplicationContext val context: Context,
  val database: MusicDatabase,
) : ViewModel() {
  val explorePage = MutableStateFlow<ExplorePage?>(null)

  private suspend fun load() {
    YouTube.explore()
      .onSuccess { page ->
        val artists: MutableMap<Int, String> = mutableMapOf()
        val favouriteArtists: MutableMap<Int, String> = mutableMapOf()
        database.allArtistsByPlayTime().first().let { list ->
          var favIndex = 0
          for ((artistsIndex, artist) in list.withIndex()) {
            artists[artistsIndex] = artist.id
            if (artist.artist.bookmarkedAt != null) {
              favouriteArtists[favIndex] = artist.id
              favIndex++
            }
          }
        }
        explorePage.value =
          page.copy(
            newReleaseAlbums =
              page.newReleaseAlbums
                .sortedBy { album ->
                  val artistIds = album.artists.orEmpty().mapNotNull { it.id }
                  val firstArtistKey =
                    artistIds.firstNotNullOfOrNull { artistId ->
                      if (artistId in favouriteArtists.values) {
                        favouriteArtists.entries.firstOrNull { it.value == artistId }?.key
                      } else {
                        artists.entries.firstOrNull { it.value == artistId }?.key
                      }
                    } ?: Int.MAX_VALUE
                  firstArtistKey
                }
                .filterExplicit(context.dataStore.get(HideExplicitKey, false)),
          )
      }
      .onFailure { reportException(it) }
  }

  init {
    viewModelScope.launch(Dispatchers.IO) { load() }
  }
}
