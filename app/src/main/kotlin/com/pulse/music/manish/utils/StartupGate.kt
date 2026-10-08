package com.pulse.music.manish.utils

import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Coordinates the AndroidX splash screen with the Home startup pipeline.
 *
 * The splash must stay visible until the app has something *meaningful* to
 * show (cached Home data or a populated local shell), instead of popping away
 * on the first frame and exposing an empty screen that later flickers into a
 * shimmer. [markComposed] is set once Compose draws its first real frame and
 * [markHomeReady] once the Home pipeline has renderable content.
 *
 * [forceRelease] is the safety net that guarantees the user can never be
 * trapped on the splash screen by a slow or failed network.
 */
object StartupGate {
  private const val TAG = "StartupGate"

  /** Hard upper bound on how long the splash may stay visible. */
  const val SPLASH_MAX_MS = 3000L

  private val _composed = MutableStateFlow(false)
  val composed: StateFlow<Boolean> = _composed.asStateFlow()

  private val _homeContentReady = MutableStateFlow(false)
  val homeContentReady: StateFlow<Boolean> = _homeContentReady.asStateFlow()

  private val startedAtRealtime: Long = SystemClock.elapsedRealtime()

  /**
   * The splash should remain visible while either the app has not composed
   * yet or we are deliberately waiting for the first meaningful Home content.
   */
  fun shouldKeepSplash(): Boolean = !_composed.value || !_homeContentReady.value

  fun markComposed() {
    if (!_composed.value) {
      _composed.value = true
      mark("composed")
    }
  }

  fun markHomeReady() {
    if (!_homeContentReady.value) {
      _homeContentReady.value = true
      mark("home-content-ready")
    }
  }

  /** Never expected in normal operation; only the timeout/safety paths call it. */
  fun forceRelease() {
    markComposed()
    markHomeReady()
  }

  /**
   * Records a startup milestone for cold-start profiling. Kept intentionally
   * cheap (no allocation on the release path) so it can be called from any
   * phase without affecting startup.
   */
  fun mark(milestone: String) {
    Log.d(TAG, "startup: $milestone at ${elapsedMs()}ms")
  }

  private fun elapsedMs(): Long = SystemClock.elapsedRealtime() - startedAtRealtime
}