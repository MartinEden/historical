package eden.historical.fetching

import org.jsoup.nodes.Document

abstract class RequiresLoginFetcher<T: Fetcher>(protected val nestedFetcher: T): Fetcher {
    protected var loggedIn: Boolean = false

    override fun get(url: String): Document {
        if (!loggedIn) {
            doLogin()
        }
        return nestedFetcher.get(url)
    }

    override val exhausted
        get() = nestedFetcher.exhausted

    protected abstract fun doLogin(): Unit
}