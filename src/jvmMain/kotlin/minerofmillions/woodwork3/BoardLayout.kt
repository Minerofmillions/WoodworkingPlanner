package minerofmillions.woodwork3

import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.ImageWriteParam
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataFormatImpl
import javax.imageio.metadata.IIOMetadataNode
import kotlin.math.pow
import kotlin.math.roundToInt

data class BoardLayout(
    val strip: Strip,
    val rule: Int,
    val period: Int,
    val stripName: String
) {
    private val outputName = buildString {
        append(period)
        append(".")
        append(rule)
        append(".png")
    }

    private val stripImageLength = strip.imageLength
    private val stripImageLengthRounded = stripImageLength.roundToInt()

    private val minimizedStrip = strip.joinToString("%") { it.minimized }
    private val stripImage by lazy { strip.render()!! }

    val previewRender by lazy {
        renderImage(PREVIEW_COUNT)
    }
    private val finalRender by lazy {
        renderImage(FINAL_COUNT)
    }

    private fun renderImage(count: Int) =
        BufferedImage(stripImageLengthRounded, count * STRIP_HEIGHT, BufferedImage.TYPE_INT_RGB).apply {
            (0 until count).forEach { i ->
                val y0 = i * STRIP_HEIGHT
                (0 until stripImageLengthRounded).forEach { sX ->
                    (0 until STRIP_HEIGHT).forEach { sY ->
                        val (bX, bY) = transform(sX, sY, i)
                        setRGB(bX, bY + y0, stripImage.getRGB(sX, sY))
                    }
                }
            }
        }

    fun save() {
        val stripDirectory = boardLayoutDirectory.resolve(stripName)
        val boardFile = stripDirectory.resolve(outputName)

        stripDirectory.mkdirs()

        val writer = ImageIO.getImageWritersByFormatName("png").next()
        ImageIO.createImageOutputStream(boardFile).use { imageOutputStream ->
            writer.output = imageOutputStream

            val metadata = writer.getDefaultImageMetadata(
                ImageTypeSpecifier.createFromRenderedImage(finalRender),
                ImageWriteParam(Locale.getDefault())
            )
            updateMetadata(metadata)

            val iioImage = IIOImage(finalRender, null, metadata)

            writer.write(iioImage)
        }
        writer.dispose()
    }

    private fun transform(x: Int, y: Int, stripNumber: Int): Pair<Int, Int> {
        val stripTransformIndex = stripNumber % period
        if (stripTransformIndex == 0) return x to y
        val transformIndex = (rule / 4.0.pow(stripTransformIndex - 1)).toInt() % 4
        return (if (transformIndex and 1 == 1) stripImageLengthRounded - 1 - x else x) to
                (if (transformIndex and 2 == 2) STRIP_HEIGHT - 1 - y else y)
    }

    private fun updateMetadata(metadata: IIOMetadata) {
        addEntry(metadata, "rule", rule.toString(4))
        addEntry(metadata, "period", period.toString())
        addEntry(metadata, "strip", minimizedStrip)
        addEntry(metadata, "name", stripName)
        addEntry(metadata, "height", StripPiece.height.toString())
    }

    private fun addEntry(metadata: IIOMetadata, key: String, value: String) {
        val textEntry = IIOMetadataNode("TextEntry").apply {
            setAttribute("keyword", key)
            setAttribute("value", value)
        }

        val text = IIOMetadataNode("Text").apply {
            appendChild(textEntry)
        }

        val root = IIOMetadataNode(IIOMetadataFormatImpl.standardMetadataFormatName).apply {
            appendChild(text)
        }

        metadata.mergeTree(IIOMetadataFormatImpl.standardMetadataFormatName, root)
    }

    companion object {
        const val STRIP_HEIGHT = 40

        const val PREVIEW_COUNT = 15

        const val FINAL_COUNT = 20

        private val homeDirectory: String = System.getProperty("user.home")
        val boardLayoutDirectory = File(homeDirectory, "BoardLayouts")
    }
}
