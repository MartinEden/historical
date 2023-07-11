package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata
import eden.historical.models.Century

object CenturyTagRule : Rule {
    private val lookup = Century.ofInterest.associateBy { "${it.ordinalAsNumber} Century" to it }
    private val terms = lookup.keys.toSet()

    override fun apply(book: BookMetadata): Iterable<AppliedCategorization> {
        val intersection = book.tags.intersect(terms)
        return intersection.map {
            val century = lookup[it]
                ?: throw Exception("Unable to find century '$it' in lookup")
            Categorization(period = century.period).withReasoning(this, intersection)
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