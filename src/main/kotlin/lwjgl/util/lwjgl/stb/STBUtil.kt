package lwjgl.util.lwjgl.stb

import lwjgl.entity.Size
import org.lwjgl.stb.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer

fun getBakedQuad(
    buffer: STBTTBakedChar.Buffer,
    bufferSize: Size,
    charIndex: Int,
    xBuffer: FloatBuffer,
    yBuffer: FloatBuffer,
    alignedQuad: STBTTAlignedQuad,
    isOpenGLFillRule: Boolean = true
) {
    STBTruetype.stbtt_GetBakedQuad(
        buffer,
        bufferSize.width,
        bufferSize.height,
        charIndex,
        xBuffer,
        yBuffer,
        alignedQuad,
        isOpenGLFillRule
    )
}

fun getPackedQuad(
    buffer: STBTTPackedchar.Buffer,
    bufferSize: Size,
    charIndex: Int,
    xBuffer: FloatBuffer,
    yBuffer: FloatBuffer,
    alignedQuad: STBTTAlignedQuad,
    isAlignToInteger: Boolean = false
) {
    STBTruetype.stbtt_GetPackedQuad(
        buffer,
        bufferSize.width, bufferSize.height,
        charIndex, xBuffer, yBuffer, alignedQuad,
        isAlignToInteger
    )
}

fun initFont(
    info: STBTTFontinfo,
    buffer: ByteBuffer,
    onFailedAction: () -> Unit = {
        throw IllegalStateException("Failed to initialize font information.")
    }
) {
    val isSuccess = STBTruetype.stbtt_InitFont(info, buffer)
    if(!isSuccess) onFailedAction()
}

fun bakeFontBitmap(
    fontData: ByteBuffer,
    fontHeightInPixels: Float,
    pixels: ByteBuffer,
    bitmapSize: Size,
    firstCharacterToBake: Int,
    charData: STBTTBakedChar.Buffer
) {
    STBTruetype.stbtt_BakeFontBitmap(
        fontData,
        fontHeightInPixels,
        pixels,
        bitmapSize.width,
        bitmapSize.height,
        firstCharacterToBake,
        charData
    )
}