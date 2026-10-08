package com.pulse.music.manish.models

import com.music.innertube.models.YTItem
import com.pulse.music.manish.db.entities.LocalItem

data class SimilarRecommendation(
  val title: LocalItem,
  val items: List<YTItem>,
)
