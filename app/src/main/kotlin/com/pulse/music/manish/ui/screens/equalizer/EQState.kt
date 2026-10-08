package com.pulse.music.manish.ui.screens.equalizer

import com.pulse.music.manish.eq.data.SavedEQProfile

data class EQState(
  val profiles: List<SavedEQProfile> = emptyList(),
  val activeProfileId: String? = null,
  val importStatus: String? = null,
  val error: String? = null
)
