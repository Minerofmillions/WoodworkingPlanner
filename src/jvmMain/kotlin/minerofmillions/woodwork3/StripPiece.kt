package minerofmillions.woodwork3

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import minerofmillions.utils.approximatelyEquals
import minerofmillions.utils.toJavaColor
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.*
import java.awt.Color as JavaColor

data class StripPiece(val topLength: Double, val bottomLength: Double, val leadingAngle: Double, val color: Color?) {
    // Consider piece labelled as follows:
    // B--x------C
    //  \ |   |ß/
    // |α\|    /
    // y  A---D
    // with the angle between line segment AB and the vertical labelled α
    // and A = (0, 0)

    // => D = (bottomLength, 0)

    // tan(α) = x / y => x = tan(α) * y
    // => B = (tan(α) * y, y)
    // => C = (tan(α) * y + topLength, y)

    // => tan(ß) = (tan(α) * y + bottomLength - topLength) / y
    // => ß = atan2(y, tan(α) * y + bottomLength - topLength)

    private val leadingRadians = Math.toRadians(leadingAngle)

    val javaColor = color?.toJavaColor()
    val minimized = "%x$%.3f$%.3f$%.3f".format(color?.toArgb() ?: 0xFF00FF00, topLength, bottomLength, leadingAngle)

    val trailingAngle by derivedStateOf {
        Math.toDegrees(
            atan2(
                tan(leadingRadians) * _height + topLength - bottomLength,
                _height
            )
        )
    }

    val flippedVertically by lazy { StripPiece(bottomLength, topLength, -leadingAngle, color) }
    val flippedHorizontally by lazy { StripPiece(topLength, bottomLength, -trailingAngle, color) }

    val topLengthFormatted = "%.2f".format(topLength)
    val bottomLengthFormatted = "%.2f".format(bottomLength)
    val leadingAngleFormatted = buildString {
        append("%.2f° ".format(leadingAngle.absoluteValue))
        if (leadingAngle > 0) append("counter-")
        append("clockwise from vertical")
    }
    val trailingAngleFormatted by derivedStateOf {
        buildString {
            append("%.2f° ".format(trailingAngle.absoluteValue))
            if (trailingAngle > 0) append("counter-")
            append("clockwise from vertical")
        }
    }

    private val xOffset by derivedStateOf { -min(tan(leadingRadians) * _height, 0.0) }
    val corners by derivedStateOf {
        arrayOf(
            xOffset to 0.0,
            tan(leadingRadians) * _height + xOffset to _height,
            tan(leadingRadians) * _height + topLength + xOffset to _height,
            bottomLength + xOffset to 0.0
        )
    }
    val maxX by derivedStateOf {
        corners.maxOf(Pair<Double, Double>::first)
    }

    val render: BufferedImage by derivedStateOf {
        stripRender(maxX) {
            it.renderPiece(this)
        }
    }

    fun offsetBefore(other: StripPiece): Double {
        val topOffset = corners[2].first - other.corners[1].first
        val bottomOffset = corners[3].first - other.corners[0].first

        return max(topOffset, bottomOffset)
    }

    companion object {
        const val STRIP_HEIGHT_PX = 20

        private var _height: Double by mutableStateOf(1.0)
        var height
            get() = _height
            set(value) {
                if (value > 0) _height = value
            }

        val RENDER_HEIGHT_PX by derivedStateOf { (_height * STRIP_HEIGHT_PX).roundToInt() }

        fun getConnector(prevTrailing: Double, nextLeading: Double, smallestLength: Double, color: Color?): StripPiece {
            val leadingRadians = Math.toRadians(prevTrailing)
            val trailingRadians = Math.toRadians(nextLeading)
            // trailingAngle = atan2(tan(leadingRadians) * height + topLength - bottomLength, height)
            // => tan(trailingAngle) = (tan(leadingAngle) * height + topLength - bottomLength) / height
            // => bottomLength - topLength = height * tan(leadingAngle) - height * tan(trailingAngle)
            val lengthDelta = _height * tan(leadingRadians) - _height * tan(trailingRadians)
            return if (lengthDelta > 0) StripPiece(smallestLength, smallestLength + lengthDelta, prevTrailing, color)
            else StripPiece(smallestLength - lengthDelta, smallestLength, prevTrailing, color)
        }
    }
}

typealias Strip = List<StripPiece>

val Strip.length
    get() = when(size) {
        0 -> 0.0
        1 -> first().maxX
        else -> zipWithNext(StripPiece::offsetBefore).fold(0.0, Double::plus) + last().maxX
    }

val Strip.imageLength
    get() = length * BoardLayout.STRIP_HEIGHT

fun Strip.render(): BufferedImage? {
    if (isEmpty()) return null
    if (size == 1) return first().render
    val xOffsets = zipWithNext(StripPiece::offsetBefore).runningFold(0.0, Double::plus)

    return stripRender(xOffsets.last() + last().maxX) {
        zip(xOffsets).forEach(it::renderPiece)
    }
}

fun Strip.mergeConnectedPieces(): Strip = toMutableList().apply { mergeSamePieces() }

fun MutableList<StripPiece>.mergeSamePieces() {
    for (index in (size - 2 downTo 0)) {
        val current = get(index)
        val next = get(index + 1)

        if (current.trailingAngle.approximatelyEquals(next.leadingAngle) && current.color == next.color) {
            remove(current)
            remove(next)
            add(
                index,
                StripPiece(
                    current.topLength + next.topLength,
                    current.bottomLength + next.bottomLength,
                    current.leadingAngle,
                    current.color
                )
            )
        }
    }
}

private fun stripRender(stripWidth: Double, renderBody: (Graphics2D) -> Unit) =
    BufferedImage(
        (stripWidth * StripPiece.STRIP_HEIGHT_PX).roundToInt(),
        StripPiece.RENDER_HEIGHT_PX,
        BufferedImage.TYPE_INT_RGB
    ).also {
        val graphics = it.createGraphics()
        graphics.let(renderBody)
        graphics.dispose()
    }

private fun Graphics2D.renderPiece(pair: Pair<StripPiece, Double>) = renderPiece(pair.first, pair.second)
private fun Graphics2D.renderPiece(piece: StripPiece, offset: Double = 0.0) {
    color = piece.javaColor ?: JavaColor(0, 255, 0)
    fillPolygon(
        piece.corners.map { (x, _) ->
            ((x + offset) * StripPiece.STRIP_HEIGHT_PX).roundToInt()
        }.toIntArray(),
        piece.corners.map { (_, y) ->
            ((StripPiece.height - y) * StripPiece.STRIP_HEIGHT_PX).roundToInt()
        }.toIntArray(),
        4
    )
}
