package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Reports function invocations which have more parameters than a certain threshold and are all not named.
 *
 * <noncompliant>
 * fun sum(a: Int, b: Int, c: Int, d: Int) {
 * }
 * sum(1, 2, 3, 4)
 * </noncompliant>
 *
 * <compliant>
 * fun sum(a: Int, b: Int, c: Int, d: Int) {
 * }
 * sum(a = 1, b = 2, c = 3, d = 4)
 * </compliant>
 *
 * @configuration threshold - number of parameters that triggers this inspection (default: `3`)
 */
class NamedArguments(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_FUNCTION_THRESHOLD
) : ThresholdRule(config, threshold) {

    override val issue = Issue(
        "NamedArguments", Severity.Maintainability,
        "Function invocation with more than $threshold parameters must all be named",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        val valueArguments = expression.valueArguments
        if (valueArguments.size > threshold && valueArguments.any { !it.isNamed() }) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        } else {
            super.visitCallExpression(expression)
        }
    }

    companion object {
        const val DEFAULT_FUNCTION_THRESHOLD = 3
    }
}
