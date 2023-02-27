package eden.historical.storage

import eden.historical.models.CategorizedBook

class DummyStore : Store {
    override fun put(book: CategorizedBook) {
        println(""""${book.title}" by ${book.author}""")
        println("\t${book.period}")
        println("\t${book.location.name}")
    }

    override fun save() {
        println("finished")
    }
}