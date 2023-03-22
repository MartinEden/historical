package eden.historical.sources

import com.google.gson.Gson
import org.jsoup.nodes.Element

@JvmInline
value class Json(private val value: Map<*,*>) {
    operator fun get(key: String): Json = getSomething(key).asDynamicJson()
    fun value(key: String): String = getSomething(key).toString()
    fun list(key: String): List<Json> {
        val raw = getSomething(key)
        if (raw is List<*>) {
            return raw.map { it.asDynamicJson() }
        }
        throw Exception("Tried to treat $raw as list in JSON lookup")
    }

    private fun getSomething(key: String): Any? = value[key]

    fun getLookupWithKeyMatching(regex: Regex): Json {
        for (key in keys) {
            if (regex.containsMatchIn(key)) {
                return this[key]
            }
        }
        throw Exception("Couldn't find key matching $regex in $this")
    }

    private val keys: Iterable<String> get() = value.keys.map { it.toString() }

    companion object {
        private fun Any?.asDynamicJson() = if (this != null && this is Map<*, *>) {
            Json(this)
        } else {
            throw Exception("Tried to treat $this as JSON lookup")
        }

        fun Element.getJsonData(gson: Gson) = this
            .data()
            .let { gson.fromJson(it, Map::class.java) }
            .asDynamicJson()
    }
}

