// ui/responsive/Responsive.kt
package com.diegodiaz.techwizards.ui.responsive

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
import androidx.compose.ui.unit.sp

/**
 * Contenedor que calcula tamaños proporcionalmente al espacio disponible.
 * Úsalo para que una screen escale en phone/tablet/landscape sin if-else.
 */
@Composable
fun Responsive(
    modifier: Modifier = Modifier,
    content: @Composable (dims: UiDims) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars) // evita solaparse con notch/barras
    ) {
        val minSide = listOf(maxWidth, maxHeight).minBy { it.value }

        val dims = remember(minSide) { UiDims.from(minSide) }
        content(dims)
    }
}

/**
 * Dimensiones derivadas del lado corto (minSide). Ajusta factores si quieres
 * que todo “crezca” o “encoga” más.
 */
data class UiDims(
    val minSide: Dp,
    // espaciados
    val spaceXs: Dp,
    val spaceSm: Dp,
    val spaceMd: Dp,
    val spaceLg: Dp,
    // tamaños de componentes comunes
    val buttonHeight: Dp,
    val buttonHeightSm: Dp,
    val cardCorner: Dp,
    val imageLg: Dp,
    val imageMd: Dp,
    val blockLg: Dp,
    // tipografías sugeridas (puedes ignorarlas si usas Typography del tema)
    val titleSp: androidx.compose.ui.unit.TextUnit,
    val bodySp: androidx.compose.ui.unit.TextUnit
) {
    companion object {
        fun from(minSide: Dp): UiDims {
            // factores “razonables”
            val f = minSide.value
            return UiDims(
                minSide = minSide,
                spaceXs = (minSide * 0.02f),
                spaceSm = (minSide * 0.03f),
                spaceMd = (minSide * 0.05f),
                spaceLg = (minSide * 0.08f),
                buttonHeight   = (minSide * 0.14f).coerceAtLeast(48.dp),
                buttonHeightSm = (minSide * 0.10f).coerceAtLeast(40.dp),
                cardCorner = 24.dp,
                imageLg = (minSide * 0.38f),
                imageMd = (minSide * 0.28f),
                blockLg = (minSide * 0.55f),
                titleSp = (f * 0.06f).sp,
                bodySp  = (f * 0.045f).sp
            )
        }
    }
}
