package dev.kentrino.jsonassert

internal class MatchResult<T>(
    val position: JsonPosition,
    val expected: T,
    val actual: Any?
)
