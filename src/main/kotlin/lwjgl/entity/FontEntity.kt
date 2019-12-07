package lwjgl.entity

import lwjgl.util.glfw.opengl.glColorOf
import lwjgl.util.glfw.opengl.glTexImage2D
import lwjgl.util.glfw.opengl.glTransaction
import lwjgl.util.glfw.opengl.glVertexOf
import lwjgl.util.io.use
import lwjgl.util.lwjgl.ioResourceToByteBuffer
import lwjgl.util.lwjgl.stb.bakeFontBitmap
import lwjgl.util.lwjgl.stb.getBakedQuad
import lwjgl.util.lwjgl.stb.getPackedQuad
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.*
import org.lwjgl.system.MemoryUtil

interface FontRender {
    fun drawText(color: Color, pointTopLeft: Point, text: CharSequence)
}

class STBAdvancedFontRender(
    fullPathFont: String,
    private val fontHeight: Float
): FontRender {
    private val textureId: Int = GL11.glGenTextures()
    private val alignedQuad = STBTTAlignedQuad.malloc()
    private val xBuffer = BufferUtils.createFloatBuffer(1)
    private val yBuffer = BufferUtils.createFloatBuffer(1)
    private val charBuffer = STBTTPackedchar.malloc(6 * 128)
    private val size = Size(512, 512)
    private val lineHeight: Float

    init {
        val source = Any::class.java.getResourceAsStream(fullPathFont)!!
        val fontByteBuffer = ioResourceToByteBuffer(source, 1024)
        val pixels = BufferUtils.createByteBuffer(size.width * size.height)
        STBTTPackContext.malloc().use { packContext ->
            STBTruetype.stbtt_PackBegin(
                packContext, pixels, size.width, size.height, 0, 1, MemoryUtil.NULL
            )
//            for(i in 0..1) {
//                for(j in 0..2) {
//                    val p = (i * 3 + j) * 128 + 32
//                    charBuffer.limit(p + 95)
//                    charBuffer.position(p)
//                    STBTruetype.stbtt_PackSetOversampling(packContext, j + 1, if(j%2 == 0) 1 else 2)
//                    STBTruetype.stbtt_PackFontRange(
//                        packContext, fontByteBuffer, 0,
////                        scale[i],
//                        48f,
//                        32, charBuffer
//                    )
//                }
//            }
            charBuffer.limit(32 + 95)
            charBuffer.position(32)
            STBTruetype.stbtt_PackSetOversampling(packContext, 1, 1)
            STBTruetype.stbtt_PackFontRange(
                packContext, fontByteBuffer, 0,
                fontHeight,
                32, charBuffer
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
        lineHeight = getTextHeight("M")
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
            } else if(c < 32.toChar() || c > 128.toChar()) {
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
            val height = alignedQuad.y1() - alignedQuad.y0()
            if(height > result) {
                result = height
            }
        }
        return result
    }

    override fun drawText(color: Color, pointTopLeft: Point, text: CharSequence) {
        val x = pointTopLeft.x
        val y = pointTopLeft.y + lineHeight
        xBuffer.put(0, x)
        yBuffer.put(0, y)

//        val font = 3//todo
        val font = 0//todo
//        val font = (3 + 1) % 3 + (3 / 3) * 3
        charBuffer.position(font * 128)
//        charBuffer.position(128)
//        charBuffer.position(0)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

        glColorOf(color)
//        GL11.glTranslatef(x, y + fontHeight, 0f)
//        xBuffer.put(0, 0f)
//        yBuffer.put(0, 0f)

        glTransaction(GL11.GL_QUADS) {
            for(c in text.toString()) {
                if(c == '\n') {
                    yBuffer.put(0, yBuffer.get(0) + fontHeight)
//                    yBuffer.put(0, yBuffer.get(0) + lineHeight)
                    xBuffer.put(0, 0f)
                    continue
                } else if(c < 32.toChar() || c > 128.toChar()) {
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

class STBFontRender(
    fullPathFont: String,
    private val fontHeight: Float
): FontRender {
    private val textureId: Int = GL11.glGenTextures()
    private val buffer = STBTTBakedChar.malloc(96)
    private val size = Size(512, 512)
    private val xBuffer = BufferUtils.createFloatBuffer(1)
    private val yBuffer = BufferUtils.createFloatBuffer(1)
    private val alignedQuad = STBTTAlignedQuad.malloc()

    init {
        val source = Any::class.java.getResourceAsStream(fullPathFont)!!
        val pixels = BufferUtils.createByteBuffer(size.width * size.height)
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
        val y = pointTopLeft.y

//        GL11.glEnable(GL11.GL_BLEND)
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        glColorOf(color)

        GL11.glPushMatrix()
        GL11.glTranslatef(x, y + fontHeight, 0f)

        xBuffer.put(0, 0.0f)
        yBuffer.put(0, 0.0f)
        glTransaction(GL11.GL_QUADS) {
            for(c in text.toString()) {
                if(c == '\n') {
                    yBuffer.put(0, yBuffer.get(0) + fontHeight)
                    xBuffer.put(0, 0f)
                    continue
                } else if(c < 32.toChar() || c > 128.toChar()) {
                    continue
                }

                getBakedQuad(
                    buffer = buffer,
                    bufferSize = size,
                    charIndex = (c - 32).toInt(),
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
        GL11.glPopMatrix()
//        GL11.glDisable(GL11.GL_BLEND)
    }
}
