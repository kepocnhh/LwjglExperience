package lwjgl.entity

import lwjgl.util.glfw.opengl.glColorOf
import lwjgl.util.glfw.opengl.glTexImage2D
import lwjgl.util.glfw.opengl.glTransaction
import lwjgl.util.glfw.opengl.glVertexOf
import lwjgl.util.io.use
import lwjgl.util.lwjgl.ioResourceToByteBuffer
import lwjgl.util.lwjgl.stb.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.*
import org.lwjgl.system.MemoryUtil
import java.io.PrintStream
import kotlin.math.absoluteValue
import kotlin.math.sqrt

interface FontRender {
    fun drawText(color: Color, pointTopLeft: Point, text: CharSequence)
}

class STBAdvancedFontRender(
    fullPathFont: String,
    private val fontHeight: Float
): FontRender {
    companion object {
        private const val firstUnicodeCharInRange = 32
    }

    private val textureId: Int = GL11.glGenTextures()
    private val alignedQuad = STBTTAlignedQuad.malloc()
    private val xBuffer = BufferUtils.createFloatBuffer(1)
    private val yBuffer = BufferUtils.createFloatBuffer(1)
    private val limit = Char.MAX_VALUE.toInt()
    private val charBuffer = STBTTPackedchar.malloc(limit)
//    private val size = Size(512, 512)
//    private val size = Size(1024, 1024)
    private val size = Size(2048, 2048)
//    private val size: Size
    private val lineHeight: Float

    init {
        val source = STBAdvancedFontRender::class.java.getResourceAsStream(fullPathFont)!!
//        val length = (sqrt(limit.toDouble())*fontHeight/6).toInt()
//        val length = 512
//        println("length: $length")
//        size = Size(length, length)
        val fontByteBuffer = ioResourceToByteBuffer(source, 1024)
        val pixels = BufferUtils.createByteBuffer(size.width * size.height)
        val fontInfo = STBTTFontinfo.malloc()
        STBTruetype.stbtt_InitFont(fontInfo, fontByteBuffer)
        STBTTPackContext.malloc().use { packContext ->
            STBTruetype.stbtt_PackBegin(
                packContext, pixels, size.width, size.height, 0, 1, MemoryUtil.NULL
            )
            charBuffer.limit(limit)
            charBuffer.position(firstUnicodeCharInRange)
            val oversample = 2
            STBTruetype.stbtt_PackSetOversampling(packContext, oversample, oversample)
//            STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2)
//            STBTruetype.stbtt_PackSetOversampling(packContext, 3, 1)
            //
            //
//            val fontSize = STBTruetype.stbtt_ScaleForMappingEmToPixels(fontInfo, fontHeight)
//            println("fontSize: $fontSize")
//            val scaleFactor = STBTruetype.stbtt_ScaleForMappingEmToPixels(fontInfo, fontHeight)
            val scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontHeight)
            println("scaleFactor: $scaleFactor")

            val charValue = 'j'
            val codepointBox = getCodepointBox(fontInfo, charValue)
            println("codepoint box of '$charValue': $codepointBox")
            val glyphBox = getGlyphBox(fontInfo, charValue)
            println("glyph box of '$charValue': $glyphBox")

            val factor = 23f/24f
//            val factor = 10f/12f
            val resultFontSize = fontHeight * factor
            println("resultFontSize: $resultFontSize")
            packFontRange(
                packContext,
                fontByteBuffer = fontByteBuffer,
                fontIndex = 0,
//                fontSize = resultFontSize,
//                fontSize = STBTruetype.STBTT_POINT_SIZE(23).toFloat(),
//                fontSize = fontSize,
                fontSize = fontHeight,
//                fontSize = fontHeight * (1f-scaleFactor),
                firstUnicodeCharInRange = firstUnicodeCharInRange,
                charBufferForRange = charBuffer
            )
            charBuffer.clear()
            STBTruetype.stbtt_PackEnd(packContext)
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        glTexImage2D(
            textureTarget = GL11.GL_TEXTURE_2D,
            textureInternalFormat = GL11.GL_ALPHA,
            textureSize = size,
            texelDataFormat = GL11.GL_ALPHA,
            texelDataType = GL11.GL_UNSIGNED_BYTE,
            pixels = pixels
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        val englishAlphabet = "abcdefghijklmnopqrstuvwxyz"
        val russianAlphabet = "абвгдеёжзиклмнопрстуфхцчшщЪыьэюя"
//        val russianAlphabet = ""
        val specialCharacters = "!@#$%^&*()[];'\\,./{}:\"|<>?`~"
//        lineHeight = getTextHeight(
//            englishAlphabet +
//            englishAlphabet.toUpperCase() +
//            russianAlphabet +
//            russianAlphabet.toUpperCase() +
//            specialCharacters
//        )
        val fontVMetrics = getFontVMetrics(fontInfo)
        println("fontVMetrics: $fontVMetrics")
        val scaleAscent = fontHeight / fontVMetrics.ascent
        println("scaleAscent: $scaleAscent")
//        lineHeight = fontHeight/(fontVMetrics.ascent.toFloat() - fontVMetrics.descent.toFloat())*fontVMetrics.ascent.toFloat()
        lineHeight = fontHeight/(fontVMetrics.ascent.toFloat() - fontVMetrics.descent.toFloat() - fontVMetrics.lineGap.toFloat()/2)*fontVMetrics.ascent.toFloat()
        println("lineHeight: $lineHeight")

        val testString = "jqQбВГдДЁуУфФцЦщЩъЪ"
//        val printStream = PrintStream(System.out, true, "UTF-8")
        val printStream = PrintStream(System.out, true, "Windows-1251")
//        printStream.println(testString)
        testString.forEach {
            val alignedQuad = getCharAlignedQuad(it)
//            printStream.println("${it.toInt()})\t$it: y0 - ${alignedQuad.y0()} y1 - ${alignedQuad.y1()}")
        }
    }

    private fun getCharAlignedQuad(value: Char): STBTTAlignedQuad {
        getPackedQuad(
            buffer = charBuffer,
            bufferSize = size,
            charIndex = value.toInt(),
            xBuffer = xBuffer,
            yBuffer = yBuffer,
            alignedQuad = alignedQuad
        )
        return alignedQuad
    }
    private fun getCharHeight(value: Char): Float {
        getPackedQuad(
            buffer = charBuffer,
            bufferSize = size,
            charIndex = value.toInt(),
            xBuffer = xBuffer,
            yBuffer = yBuffer,
            alignedQuad = alignedQuad
        )
//        return alignedQuad.y1() - alignedQuad.y0()
        return alignedQuad.y0().absoluteValue
//        return 24f
//        return 0f
    }
    private fun getTextHeight(text: CharSequence): Float {
        var result = 0f
        xBuffer.put(0, 0f)
        yBuffer.put(0, 0f)
        for(c in text.toString()) {
            if(c == '\n') {
                yBuffer.put(0, yBuffer.get(0))
                xBuffer.put(0, 0f)
                continue
            } else if(c < firstUnicodeCharInRange.toChar()) {
                continue
            }
            val height = getCharHeight(c)
            if(height > result) {
                result = height
            }
        }
        return result
    }

    override fun drawText(color: Color, pointTopLeft: Point, text: CharSequence) {
        val x = pointTopLeft.x
        val y = pointTopLeft.y + lineHeight
//        val y = pointTopLeft.y
        xBuffer.put(0, x)
        yBuffer.put(0, y)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

        glColorOf(color)

        var lineNumber = 0
        glTransaction(GL11.GL_QUADS) {
            for(c in text.toString()) {
                if(c == '\n') {
                    yBuffer.put(0, yBuffer.get(0) + fontHeight)
                    xBuffer.put(0, 0f)
                    lineNumber++
                    continue
                } else if(c < firstUnicodeCharInRange.toChar()) {
                    continue
                }
                getPackedQuad(
                    buffer = charBuffer,
                    bufferSize = size,
                    charIndex = c.toInt(),
                    xBuffer = xBuffer,
                    yBuffer = yBuffer,
                    alignedQuad = alignedQuad
                )
                GL11.glTexCoord2f(
                    alignedQuad.s0(),
                    alignedQuad.t0()
                )
                glVertexOf(
                    alignedQuad.x0(),
                    alignedQuad.y0()
//                    y+lineNumber*fontHeight
                )
                GL11.glTexCoord2f(
                    alignedQuad.s1(),
                    alignedQuad.t0()
                )
                glVertexOf(
                    alignedQuad.x1(),
                    alignedQuad.y0()
//                    y+lineNumber*fontHeight
                )
                GL11.glTexCoord2f(
                    alignedQuad.s1(),
                    alignedQuad.t1()
                )
                glVertexOf(
                    alignedQuad.x1(),
                    alignedQuad.y1()
//                    alignedQuad.y1() - alignedQuad.y0()
                )
                GL11.glTexCoord2f(
                    alignedQuad.s0(),
                    alignedQuad.t1()
                )
                glVertexOf(
                    alignedQuad.x0(),
                    alignedQuad.y1()
//                    alignedQuad.y1() - alignedQuad.y0()
                )
            }
        }

        GL11.glDisable(GL11.GL_BLEND)
    }
}

class STBFontRender(
    fullPathFont: String,
    private val fontHeight: Float
): FontRender {
    private val textureId: Int = GL11.glGenTextures()
    private val buffer = STBTTBakedChar.malloc(Char.MAX_VALUE.toInt())
    private val size = Size(512, 512)
    private val xBuffer = BufferUtils.createFloatBuffer(1)
    private val yBuffer = BufferUtils.createFloatBuffer(1)
    private val alignedQuad = STBTTAlignedQuad.malloc()

    init {
        val source = Any::class.java.getResourceAsStream(fullPathFont)!!
        val pixels = BufferUtils.createByteBuffer(size.width * size.height)
        buffer.limit(Char.MAX_VALUE.toInt())
        buffer.position(32)
        bakeFontBitmap(
            fontData = ioResourceToByteBuffer(source, 1024),
            fontHeightInPixels = fontHeight,
            pixels = pixels,
            bitmapSize = size,
            firstCharacterToBake = 32,
            charData = buffer
        )

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        glTexImage2D(
            textureTarget = GL11.GL_TEXTURE_2D,
            textureInternalFormat = GL11.GL_ALPHA,
            textureSize = size,
            texelDataFormat = GL11.GL_ALPHA,
            texelDataType = GL11.GL_UNSIGNED_BYTE,
            pixels = pixels
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
    }

    override fun drawText(color: Color, pointTopLeft: Point, text: CharSequence) {
        val x = pointTopLeft.x
        val y = pointTopLeft.y + fontHeight
        xBuffer.put(0, x)
        yBuffer.put(0, y)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

        glColorOf(color)

        glTransaction(GL11.GL_QUADS) {
            for(c in text.toString()) {
                if(c == '\n') {
                    yBuffer.put(0, yBuffer.get(0) + fontHeight)
                    xBuffer.put(0, 0f)
                    continue
                } else if(c < 32.toChar()) {
                    continue
                }

                getBakedQuad(
                    buffer = buffer,
                    bufferSize = size,
                    charIndex = c.toInt() - 32,
                    xBuffer = xBuffer,
                    yBuffer = yBuffer,
                    alignedQuad = alignedQuad
                )

                GL11.glTexCoord2f(
                    alignedQuad.s0(),
                    alignedQuad.t0()
                )
                glVertexOf(
                    alignedQuad.x0(),
                    alignedQuad.y0()
                )
                GL11.glTexCoord2f(
                    alignedQuad.s1(),
                    alignedQuad.t0()
                )
                glVertexOf(
                    alignedQuad.x1(),
                    alignedQuad.y0()
                )
                GL11.glTexCoord2f(
                    alignedQuad.s1(),
                    alignedQuad.t1()
                )
                glVertexOf(
                    alignedQuad.x1(),
                    alignedQuad.y1()
                )
                GL11.glTexCoord2f(
                    alignedQuad.s0(),
                    alignedQuad.t1()
                )
                glVertexOf(
                    alignedQuad.x0(),
                    alignedQuad.y1()
                )
            }
        }

        GL11.glDisable(GL11.GL_BLEND)
    }
}
