package eden.historical.storage

import eden.historical.models.CategorizedBook

interface Store {
    fun put(book: CategorizedBook)
    fun save()
}