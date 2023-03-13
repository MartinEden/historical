package eden.historical.storage

import eden.historical.models.CategorizedBook

class MultiStore(private val stores: List<Store>): Store {
    override fun put(book: CategorizedBook) {
        stores.forEach { it.put(book) }
    }

    override fun save() {
        stores.forEach { it.save() }
    }
}