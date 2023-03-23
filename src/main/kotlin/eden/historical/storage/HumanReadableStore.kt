package eden.historical.storage

import eden.historical.Settings
import eden.historical.models.CategorizedBook
import eden.historical.models.Period
import eden.historical.models.Place
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class HumanReadableStore : Store {
    private val books = mutableListOf<CategorizedBook>()
    private val outputFile = File(Paths.get(Settings.outputPath,"output.txt").absolutePathString())

    override fun put(book: CategorizedBook) {
        books.add(book)
    }

    override fun save() {
        outputFile.parentFile.mkdirs()
        outputFile.writer().use { f ->
            for (book in books.sortedBy { it.book }) {
                f.write(serialize(book))
                f.write("\n")
            }
            f.write("\n")
            f.write("Number of books: ${books.size}\n")
            val missingDataPoints = books.count { it.period == Period.Unknown } + books.count { it.place == Place.Unknown }
            f.write("Missing pieces of data: $missingDataPoints\n")
        }
    }

    private fun serialize(book: CategorizedBook) = """
        |${book.title} by ${book.authors.joinToString("; ")}
        |${book.period}
        |${book.place}
    """.trimMargin()
}