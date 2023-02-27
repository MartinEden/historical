package eden.historical.categorization

import eden.historical.models.Period
import eden.historical.models.Place

data class Categorization(val period: Period? = null, val place: Place? = null)