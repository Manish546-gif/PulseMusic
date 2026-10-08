package com.pulse.music.manish.playback

import com.pulse.music.manish.db.entities.SongEntity

interface ISyncUtils {
  fun likeSong(song: SongEntity)
}
