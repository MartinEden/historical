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

    private fun getProperty(key: String, default: String): String {
        val raw = props[key]
        return if (raw != null) {
            if (raw is String) {
                raw
            } else {
                throw Exception("Unable to parse property $key. Value was $raw")
            }
        } else {
            default
        }
    }

    val outputPath: String = getProperty("outputPath", default = "output")
}