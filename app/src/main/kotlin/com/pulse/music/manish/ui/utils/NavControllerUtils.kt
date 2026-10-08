package com.pulse.music.manish.ui.utils

import androidx.navigation.NavController
import com.pulse.music.manish.ui.screens.Screens

fun NavController.backToMain() {
  val mainRoutes = Screens.MainScreens.map { it.route }

  while (
    previousBackStackEntry != null && currentBackStackEntry?.destination?.route !in mainRoutes
  ) {
    popBackStack()
  }
}
