package com.pulse.music.manish.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import com.pulse.music.manish.R

fun UriHandler.safeOpenUri(context: Context, uri: String) {
  if (uri.isBlank()) return

  runCatching { openUri(uri) }
    .onFailure {
      Toast.makeText(
          context,
          context.getString(R.string.error_no_stream).replace("stream", "app"),
          Toast.LENGTH_SHORT
        )
        .show()
    }
}
