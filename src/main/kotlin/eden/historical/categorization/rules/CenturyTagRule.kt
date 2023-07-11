package eden.historical.categorization.rules

import eden.historical.categorization.Categorization
import eden.historical.models.BookMetadata
import eden.historical.models.Century

object CenturyTagRule : Rule {
    private val lookup = Century.ofInterest.associateBy { "${it.ordinalAsNumber} Century" to it }
    private val terms = lookup.keys.toSet()

    override fun apply(book: BookMetadata): Categorization? {
        val intersection = book.tags.intersect(terms)
        // TODO: Return all matches that result in unique countries?
        return intersection.firstOrNull()?.let {
            val century = lookup[it]
                ?: throw Exception("Unable to find century '$it' in lookup")
            Categorization(period = century.period)
        }
    }
}

// TODO: Reintroduce ability to detect phase "the Xth and X+1th centuries"?

//    private val nextCenturyRegex = makeNextCenturyRegex()
//
//        } else if (nextCenturyRegex != null && nextCenturyRegex in book.synopsis) {
//            Categorization(
//                period = Period.Range(
//                    nextCenturyRegex.pattern,
//                    century.period.start,
//                    century.next!!.period.end
//                ) withConfidence 0.5f
//            )
//        } else null
//    }
//
//    private fun makeNextCenturyRegex(): Regex? {
//        val nextCentury = century.next
//        return if (nextCentury != null) {
//            Regex("""${century.ordinalAsNumber} and ${nextCentury.ordinalAsNumber} centuries""")
//        } else {
//            null
//        }
//    }