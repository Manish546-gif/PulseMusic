package com.pulse.music.manish.utils

import timber.log.Timber

var exceptionReporter: ((Throwable) -> Unit)? = null

fun reportException(throwable: Throwable) {
  Timber.e(throwable)
  exceptionReporter?.invoke(throwable)
}
