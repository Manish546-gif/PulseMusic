package com.pulse.music.manish.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pulse.music.manish.ui.component.backdrop.backdrops.emptyBackdrop
import com.pulse.music.manish.ui.theme.PulsePalette

/**
 * Supplies the CompositionLocals that `Modifier.liquidGlass` depends on, for use
 * inside `@Preview` functions.
 *
 * At runtime these are provided once by `MainActivity` with a real
 * `rememberLayerBackdrop`. `LocalAppBackdrop`'s default is
 * `error("No AppBackdrop provided")`, so *any* preview that renders a glass
 * surface would otherwise crash. This provider supplies an inert
 * [emptyBackdrop] instead, which draws nothing and lets the blur pass through
 * harmlessly.
 *
 * Glass is also force-enabled here regardless of the user's stored preference,
 * so previews show the intended treatment rather than an accidental
 * disabled/transparent state.
 */
@Composable
fun PreviewBackdropProvider(
  /** Match the runtime dense-blur recipe from AGENT.md: high blur, moderate opacity. */
  blurRadius: Float = 64f,
  surfaceOpacity: Float = 0.5f,
  content: @Composable () -> Unit
) {
  val surfaceTint = PulsePalette.Background
  CompositionLocalProvider(
    LocalGlassEffectConfig provides
      GlassEffectConfig(
        // `glassEffectEnabled` is the user preference; previews ignore it on purpose.
        globalEnabled = true,
        blurRadius = blurRadius,
        surfaceOpacity = surfaceOpacity,
        surfaceTintColor = surfaceTint
      ),
    LocalAppBackdrop provides emptyBackdrop(),
    content = content
  )
}
