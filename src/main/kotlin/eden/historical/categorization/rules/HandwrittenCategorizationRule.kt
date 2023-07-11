package eden.historical.categorization.rules

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eden.historical.categorization.Categorization
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata
import eden.historical.models.Period
import eden.historical.models.Place
import java.io.File

class HandwrittenCategorizationRule : Rule {
    private val extraCategorizations = loadHandwrittenCategorizations()

    private fun loadHandwrittenCategorizations(): Map<String, Categorization> {
        val jsonPath: String? = Thread.currentThread().contextClassLoader?.getResource("human-input.json")?.path
        return if (jsonPath != null) {
            val data = File(jsonPath).reader().use {
                Gson().fromJson(it, object : TypeToken<List<ManualBookData>>() {})
            }
            data.associate {
                it.id to Categorization(it.period, it.place)
            }
        } else {
            emptyMap()
        }
    }

    override fun apply(book: BookMetadata) = extraCategorizations[book.book.id]?.withReasoning(this, "")
}

private data class ManualBookData(val id: String, val period: Period.Range?, val place: Place.Area?)