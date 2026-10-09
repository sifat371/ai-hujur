package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.IslamicGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun IslamicGeometricBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.05f,
    lineColor: Color = IslamicGold
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val step = 90f

        var x = 0f
        while (x < w + step) {
            var y = 0f
            while (y < h + step) {
                // Draw 8-pointed star at grid intersections
                drawIslamicStar(
                    center = Offset(x, y),
                    radius = 28f,
                    color = lineColor.copy(alpha = alpha)
                )

                // Draw subtle diagonal connecting lines
                drawLine(
                    color = lineColor.copy(alpha = alpha * 0.6f),
                    start = Offset(x - 20f, y - 20f),
                    end = Offset(x + 20f, y + 20f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = lineColor.copy(alpha = alpha * 0.6f),
                    start = Offset(x + 20f, y - 20f),
                    end = Offset(x - 20f, y + 20f),
                    strokeWidth = 1f
                )

                y += step
            }
            x += step
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawIslamicStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    // 8-point star composed of two overlapping rotated squares
    val path = Path()
    val points = 8
    val innerRadius = radius * 0.55f

    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = (i * Math.PI / points) - (Math.PI / 2)
        val px = center.x + (r * cos(angle)).toFloat()
        val py = center.y + (r * sin(angle)).toFloat()

        if (i == 0) {
            path.moveTo(px, py)
        } else {
            path.lineTo(px, py)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 1.2f)
    )
}
