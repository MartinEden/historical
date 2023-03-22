package eden.historical.storage

import eden.historical.Settings
import eden.historical.models.CategorizedBook
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
        }
    }

    private fun serialize(book: CategorizedBook) = """
        |${book.title} by ${book.authors.joinToString("; ")}
        |${book.period}
        |${book.location}
    """.trimMargin()
}