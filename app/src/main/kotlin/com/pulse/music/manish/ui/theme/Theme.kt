package com.pulse.music.manish.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

import com.pulse.music.manish.constants.SelectedFontKey
import com.pulse.music.manish.constants.AppFont
import com.pulse.music.manish.utils.rememberPreference
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.getValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

/**
 * Pulse brand palette.
 *
 * Mirrors the `pulse-mobile` reference app (`tailwind.config.js`) so the native
 * Android build renders the same design system.
 */
object PulsePalette {
  val Background = Color(0xFF09090F)
  val Surface = Color(0xFF111217)
  val SurfaceVariant = Color(0xFF1A1B21)
  val Primary = Color(0xFFFF6B35)
  val PrimaryHover = Color(0xFFFF8555)
  val Text = Color(0xFFF5F5F5)
  val TextSecondary = Color(0xFFA0A0A0)
  val TextMuted = Color(0xFF666666)
  val Border = Color(0xFF2A2B30)
  val BorderLight = Color(0xFF3A3B40)
}

/**
 * Iconography tokens for settings rows.
 *
 * Per AGENT.md's monochrome/minimal rule, settings rows deliberately carry **no
 * per-section colour coding**: every row uses one neutral tone so the list reads
 * as a single system rather than a set of competing badges. [Accent] is the only
 * sparing exception, reserved for a genuinely active/selected row.
 */
object SettingsIconColors {
  /** Neutral tone for the resting state of every settings row icon. */
  val Neutral = PulsePalette.TextSecondary

  /**
   * Sparing Pulse-brand accent, used only for a selected or highlighted row.
   * Note: the row *background* highlight intentionally uses
   * `MaterialTheme.colorScheme.primary` instead of this value, so it still
   * follows a user-selected theme color rather than forcing Pulse orange.
   */
  val Accent = PulsePalette.Primary
}

val DefaultThemeColor = PulsePalette.Primary

private val PulseDarkColorScheme = darkColorScheme(
  primary = PulsePalette.Primary,
  onPrimary = Color(0xFF2A0E02),
  primaryContainer = Color(0xFF7A2E12),
  onPrimaryContainer = Color(0xFFFFDBCF),
  secondary = Color(0xFFFFB59B),
  onSecondary = Color(0xFF54210C),
  secondaryContainer = Color(0xFF71361F),
  onSecondaryContainer = Color(0xFFFFDBCF),
  background = PulsePalette.Background,
  onBackground = PulsePalette.Text,
  surface = PulsePalette.Surface,
  onSurface = PulsePalette.Text,
  surfaceVariant = PulsePalette.SurfaceVariant,
  onSurfaceVariant = PulsePalette.TextSecondary,
  outline = PulsePalette.Border,
  outlineVariant = PulsePalette.BorderLight,
)

private val PulseLightColorScheme = lightColorScheme(
  primary = Color(0xFFD9480F),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFFFE1D5),
  onPrimaryContainer = Color(0xFF3B1200),
  secondary = Color(0xFF9A4522),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFFFDBCF),
  onSecondaryContainer = Color(0xFF3B1200),
  background = Color(0xFFFAFAFC),
  onBackground = Color(0xFF15161A),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF15161A),
  surfaceVariant = Color(0xFFF1E1DA),
  onSurfaceVariant = Color(0xFF53433E),
  outline = Color(0xFF85736D),
  outlineVariant = Color(0xFFD8C2BB),
)

@Composable
fun pulseTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  pureBlack: Boolean = false,
  themeColor: Color = DefaultThemeColor,
  content: @Composable () -> Unit,
) {
  val selectedFontValue by rememberPreference(SelectedFontKey, AppFont.SYSTEM.value)

  val brandFont = remember(selectedFontValue) {
      when (AppFont.fromValue(selectedFontValue)) {
          AppFont.SYSTEM -> FontFamily.Default
          AppFont.GOOGLE_SANS -> GoogleSansFontFamily
          AppFont.SANS_FLEX -> SansFlexFontFamily
          AppFont.OUTFIT -> OutfitFontFamily
          AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
          else -> FontFamily.Default
      }
  }


  val isPulseDefaultTheme = themeColor == DefaultThemeColor

  val baseColorScheme =
    if (isPulseDefaultTheme) {
      // Default: fixed Pulse brand palette (dark is the primary experience).
      if (darkTheme) PulseDarkColorScheme else PulseLightColorScheme
    } else {

      rememberDynamicColorScheme(
        seedColor = themeColor,
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.TonalSpot
      )
    }

  val colorScheme =
    remember(baseColorScheme, pureBlack, darkTheme) {
      if (darkTheme && pureBlack) {
        baseColorScheme.pureBlack(true)
      } else {
        baseColorScheme
      }
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = getTypography(brandFont),
    shapes =
      androidx.compose.material3.MaterialTheme.shapes.copy(
        extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
      ),
    content = content
  )
}

fun Bitmap.extractThemeColor(): Color {
  val colorsToPopulation =
    Palette.from(this).maximumColorCount(8).generate().swatches.associate {
      it.rgb to it.population
    }
  val rankedColors = Score.score(colorsToPopulation)
  return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
  val extractedColors =
    Palette.from(this).maximumColorCount(64).generate().swatches.associate {
      it.rgb to it.population
    }

  val orderedColors =
    Score.score(extractedColors, 2, 0xff4285f4.toInt(), true).sortedByDescending {
      Color(it).luminance()
    }

  return if (orderedColors.size >= 2) listOf(Color(orderedColors[0]), Color(orderedColors[1]))
  else listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.pureBlack(apply: Boolean) =
  if (apply) copy(surface = Color.Black, background = Color.Black) else this

val ColorSaver =
  object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)

    override fun SaverScope.save(value: Color): Int = value.toArgb()
  }
