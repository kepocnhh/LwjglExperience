package lwjgl.util.resource

import java.io.File
import java.io.InputStream
import java.net.URL

class ResourceProvider {
    companion object {
        fun getResourceAsStream(filePath: String): InputStream {
            return ResourceProvider::class.java.getResourceAsStream(filePath) ?: throw IllegalStateException(
                "Resource by path $filePath does not exist!"
            )
        }

        fun getResourceAsURL(filePath: String): URL {
            return ResourceProvider::class.java.getResource(filePath)  ?: throw IllegalStateException(
                "Resource by path $filePath does not exist!"
            )
        }

        fun getResourceAsFile(filePath: String): File {
            val url = getResourceAsURL(filePath)
            return File(url.toURI())
        }
    }
}
