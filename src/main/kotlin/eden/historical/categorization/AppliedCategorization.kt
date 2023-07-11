package eden.historical.categorization

import eden.historical.categorization.rules.Rule

data class AppliedCategorization(
    val categorization: Categorization,
    val reasoning: Reasoning
) {
    fun weightedBy(confidenceMultiplier: Float) = copy(categorization = categorization.weightedBy(confidenceMultiplier))
}

fun Categorization.withReasoning(rule: Rule, relevantData: Any) = AppliedCategorization(
    this,
    Reasoning(rule::class.simpleName ?: "Unknown", relevantData)
)