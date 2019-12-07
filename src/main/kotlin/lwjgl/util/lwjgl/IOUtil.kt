package lwjgl.util.lwjgl

import org.lwjgl.BufferUtils.createByteBuffer
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels

private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
    val newBuffer = createByteBuffer(newCapacity)
    buffer.flip()
    newBuffer.put(buffer)
    return newBuffer
}

fun ioResourceToByteBuffer(inputStream: InputStream, bufferSize: Int): ByteBuffer {
    return inputStream.use {
        Channels.newChannel(it).use { channel ->
            var buffer = createByteBuffer(bufferSize)
            while(true) {
                val byte = channel.read(buffer)
                if(byte == -1) break
                if(buffer.remaining() == 0) buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2)
            }
            buffer.flip()
            buffer
        }
    }
}