package eden.historical

import java.io.File
import java.lang.Thread.currentThread
import java.util.Properties

object Settings {
    private val props by lazy {
        val propertiesPath: String? = currentThread().contextClassLoader?.getResource("app.properties")?.path
        val p = Properties()
        if (propertiesPath != null) {
            p.load(File(propertiesPath).reader())
        }
        p
    }

    private fun getProperty(key: String, throwOnMissingProperty: Boolean = false): String? {
        val raw = props[key]
        return if (raw != null) {
            if (raw is String) {
                raw
            } else {
                throw Exception("Unable to parse property '$key'. Value was $raw")
            }
        } else if (throwOnMissingProperty) {
            throw Exception("Missing required property '$key'.")
        } else {
            null
        }
    }

    private fun getProperty(key: String, default: String): String {
        return getProperty(key) ?: default
    }
    private fun <T> getProperty(key: String, default: T, converter: (String) -> T): T {
        val raw = getProperty(key)
        return if (raw != null) {
            converter(raw)
        } else {
            default
        }
    }

    val outputPath: String = getProperty("outputPath", default = "output")
    val maximumCacheMisses: Int = getProperty("maximumCacheMisses", 20) { it.toInt() }

    val atMain = getProperty("atMain")
    val ubidMain = getProperty("ubidMain")
}
