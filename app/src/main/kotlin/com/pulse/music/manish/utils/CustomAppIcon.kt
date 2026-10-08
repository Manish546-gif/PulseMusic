package com.pulse.music.manish.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Scale
import coil3.toBitmap
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * User supplied launcher artwork.
 *
 * A normal app cannot replace its real launcher icon with arbitrary content:
 * `PackageManager.setApplicationIcon` requires the signature level
 * `android.permission.INSTANCE_PACKAGE`, and an `activity-alias` icon has to
 * resolve to a resource compiled into the APK at install time. The supported
 * route is a pinned launcher shortcut built from a bitmap, which is what this
 * stores and pins.
 */
object CustomAppIcon {
  const val SHORTCUT_ID = "pulse_custom_app_icon"

  private const val DIR_NAME = "custom_app_icon"
  private const val FILE_NAME = "custom_icon.png"
  private const val EXPORT_SIZE = 512
  private const val PREVIEW_SIZE = 192

  private fun iconFile(context: Context): File = File(File(context.filesDir, DIR_NAME), FILE_NAME)

  fun exists(context: Context): Boolean = iconFile(context).let { it.isFile && it.length() > 0L }

  fun isPinSupported(context: Context): Boolean =
    ShortcutManagerCompat.isRequestPinShortcutSupported(context)

  fun defaultLabel(context: Context): String =
    runCatching {
      val pm = context.packageManager
      pm.getApplicationLabel(pm.getApplicationInfo(context.packageName, 0)).toString()
    }.getOrDefault("Pulse")

  /**
   * Copies [uri] into app storage as a square 512px PNG. Returns false when the
   * image cannot be read or encoded, so callers can surface a failure.
   */
  suspend fun save(context: Context, uri: Uri): Boolean =
    withContext(Dispatchers.IO) {
      runCatching {
        val request =
          ImageRequest.Builder(context)
            .data(uri)
            .size(EXPORT_SIZE, EXPORT_SIZE)
            .scale(Scale.FILL)
            .allowHardware(false)
            .build()

        val source = context.imageLoader.execute(request).image?.toBitmap()
        if (source == null) return@runCatching false

        val square = centerCrop(source)
        val scaled =
          if (square.width == EXPORT_SIZE && square.height == EXPORT_SIZE) square
          else Bitmap.createScaledBitmap(square, EXPORT_SIZE, EXPORT_SIZE, true)

        val dir = File(context.filesDir, DIR_NAME)
        if (!dir.isDirectory && !dir.mkdirs()) return@runCatching false

        FileOutputStream(File(dir, FILE_NAME)).use { out ->
          if (!scaled.compress(Bitmap.CompressFormat.PNG, 100, out)) return@runCatching false
        }
        true
      }.getOrDefault(false)
    }

  suspend fun loadPreview(context: Context): Bitmap? =
    withContext(Dispatchers.IO) { decode(context, PREVIEW_SIZE) }

  fun delete(context: Context) {
    runCatching { iconFile(context).delete() }
    runCatching { ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(SHORTCUT_ID)) }
  }

  /**
   * Asks the launcher to place the saved icon on the home screen. Must be called
   * from the main thread; the pinning dialog is system UI.
   *
   * Returns true when the shortcut was pinned or refreshed.
   */
  suspend fun requestPin(context: Context, label: String): Boolean =
    withContext(Dispatchers.IO) {
      val bitmap = decode(context, EXPORT_SIZE) ?: return@withContext false
      val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
      if (launchIntent == null) return@withContext false

      val info =
        ShortcutInfoCompat.Builder(context, SHORTCUT_ID)
          .setShortLabel(label)
          .setLongLabel(label)
          .setIcon(IconCompat.createWithAdaptiveBitmap(bitmap))
          .setIntent(
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
          )
          .build()

      withContext(Dispatchers.Main) {
        runCatching {
          if (ShortcutManagerCompat.requestPinShortcut(context, info, null)) {
            true
          } else {
            ShortcutManagerCompat.updateShortcuts(context, listOf(info))
          }
        }.getOrDefault(false)
      }
    }

  private fun decode(context: Context, maxSize: Int): Bitmap? {
    val file = iconFile(context)
    if (!file.isFile || file.length() == 0L) return null

    return runCatching {
      val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      BitmapFactory.decodeFile(file.absolutePath, bounds)
      if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

      var sample = 1
      while (bounds.outWidth / sample > maxSize * 2 || bounds.outHeight / sample > maxSize * 2) {
        sample *= 2
      }

      BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = sample })
    }.getOrNull()
  }

  private fun centerCrop(source: Bitmap): Bitmap {
    if (source.width == source.height) return source
    val edge = min(source.width, source.height)
    val left = (source.width - edge) / 2
    val top = (source.height - edge) / 2
    return Bitmap.createBitmap(source, left, top, edge, edge)
  }
}