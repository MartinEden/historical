package eden.historical.storage

import com.google.gson.GsonBuilder
import eden.historical.Settings
import eden.historical.models.CategorizedBook
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString

// TODO: Output reasoning
class JsonStore : Store {
    private val books = mutableListOf<CategorizedBook>()
    //private val nowAsString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss"))
    private val outputFile = File(Paths.get(Settings.outputPath,"output.json").absolutePathString())
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()


    override fun put(book: CategorizedBook) {
        books.add(book)
    }

    override fun save() {
        outputFile.parentFile.mkdirs()
        val booksInOrder = books.sortedBy { it.book }
        outputFile.writer().use {
            gson.toJson(booksInOrder, it)
        }
    }
}