package dev.kentrino.jsonassert

import dev.kentrino.jsonassert.util.Failures
import java.lang.IllegalArgumentException

typealias Json = io.github.portfoligno.json.ast.Json

fun json(block: JsonMatcher.JsonObjectMatcher.() -> Unit) = JsonMatcher.JsonObjectMatcher(JsonPosition.root).apply {
    block()
}

fun json(value: Any?): JsonMatcher.JsonPrimitiveMatcher {
    return when (value) {
        is Int -> JsonMatcher.JsonPrimitiveMatcher.ShouldBeInt(JsonPosition.root, value)
        is String -> JsonMatcher.JsonPrimitiveMatcher.ShouldBeString(JsonPosition.root, value)
        is Boolean -> JsonMatcher.JsonPrimitiveMatcher.ShouldBeBoolean(JsonPosition.root, value)
        is Undefined -> JsonMatcher.JsonPrimitiveMatcher.ShouldBeUndefined(JsonPosition.root, value)
        null -> JsonMatcher.JsonPrimitiveMatcher.ShouldBeNull(JsonPosition.root, value)
        else -> throw IllegalArgumentException("not supported")
    }
}

infix fun Json.shouldBe(matcher: JsonMatcher) {
    matcher.test(this).doAssert()
}

private fun List<MatchResult<*>>.doAssert() {
    forEach {
        if (it.expected != it.actual) {
            throw Failures.failure(
                    message = "json mismatched fail (position: ${it.position.print()})",
                    expectedRepr = "${it.expected}",
                    actualRepr = "${it.actual}"
            )
        }
    }
}
