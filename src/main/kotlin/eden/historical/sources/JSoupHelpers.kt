package eden.historical.sources

import org.jsoup.nodes.Element

fun Element.trimmedText(cssQuery: String) =
    this.select(cssQuery).first()?.text()?.trim() ?: throw Exception("Unable to find any elements matching: $cssQuery")
