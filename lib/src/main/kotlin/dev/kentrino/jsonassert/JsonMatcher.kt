package dev.kentrino.jsonassert

import java.lang.NullPointerException
import io.github.portfoligno.json.ast.Json
import io.github.portfoligno.json.ast.JsonArray
import io.github.portfoligno.json.ast.JsonObject


@Suppress("UNUSED_PARAMETER")
sealed class JsonMatcher {
    abstract val position: JsonPosition
    internal abstract fun test(json: Json): List<MatchResult<*>>

    sealed class JsonPrimitiveMatcher(): JsonMatcher() {
        abstract val value: Any?

        class ShouldBeInt(override val position: JsonPosition, override val value: Int): JsonPrimitiveMatcher()
        class ShouldBeString(override val position: JsonPosition, override val value: String): JsonPrimitiveMatcher()
        class ShouldBeNull(override val position: JsonPosition, override val value: Nothing?): JsonPrimitiveMatcher()
        class ShouldBeBoolean(override val position: JsonPosition, override val value: Boolean): JsonPrimitiveMatcher()
        class ShouldBeUndefined(override val position: JsonPosition, override val value: Undefined): JsonPrimitiveMatcher()

        override fun test(json: Json): List<MatchResult<*>> {
            val actual = handleUndefined { position.fold(json, ::access) }
            return listOf(MatchResult(position, value, actual))
        }

        private fun handleUndefined(block: () -> Json): Any? = try {
            block().value
        } catch (e: NullPointerException) {
            Undefined
        } catch (e: ArrayIndexOutOfBoundsException) {
            Undefined
        }

        private fun access(json: Json, key: JsonKey): Json = when (key) {
            is JsonKey.IntKey -> {
                json[key.value]!!
            }
            is JsonKey.StringKey -> {
                json[key.value]!!
            }
        }

        private operator fun Json?.get(key: String): Json? = (this as? JsonObject)?.let { it.value[key] }
        private operator fun Json?.get(index: Int): Json? = (this as? JsonArray)?.let { it.value[index] }
    }

    class JsonObjectMatcher(
            override val position: JsonPosition
    ): JsonMatcher() {
        private val internalMap: MutableMap<JsonKey, JsonMatcher> = mutableMapOf()

        operator fun String.invoke(matcher: JsonObjectMatcher.() -> Unit) {
            val key = JsonKey.StringKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonObjectMatcher(position.extend(key)).apply(matcher)
        }

        operator fun Int.invoke(matcher: JsonObjectMatcher.() -> Unit) {
            val key = JsonKey.IntKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonObjectMatcher(position.extend(key)).apply(matcher)
        }

        infix fun String.shouldBe(value: String) {
            val key = JsonKey.StringKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeString(position.extend(key), value)
        }

        infix fun String.shouldBe(value: Int) {
            val key = JsonKey.StringKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeInt(position.extend(key), value)
        }

        infix fun String.shouldBe(value: Boolean) {
            val key = JsonKey.StringKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeBoolean(position.extend(key), value)
        }

        // for null
        infix fun String.shouldBe(value: Nothing?) {
            val key = JsonKey.StringKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeNull(position.extend(key), null)
        }

        infix fun String.shouldBe(value: Any?) {
            val key = JsonKey.StringKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeUndefined(position.extend(key), Undefined)
        }

        infix fun Int.shouldBe(value: String) {
            val key = JsonKey.IntKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeString(position.extend(key), value)
        }

        infix fun Int.shouldBe(value: Int) {
            val key = JsonKey.IntKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeInt(position.extend(key), value)
        }

        infix fun Int.shouldBe(value: Boolean) {
            val key = JsonKey.IntKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeBoolean(position.extend(key), value)
        }

        infix fun Int.shouldBe(value: Nothing?) {
            val key = JsonKey.IntKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeNull(position.extend(key), null)
        }

        infix fun Int.shouldBe(value: Undefined) {
            val key = JsonKey.IntKey(this)
            this@JsonObjectMatcher.internalMap[key] = JsonPrimitiveMatcher.ShouldBeUndefined(position.extend(key), Undefined)
        }

        override fun test(json: Json): List<MatchResult<*>> {
            return internalMap.values.fold(listOf<MatchResult<*>>()) { acc, matcher ->
                acc + matcher.test(json)
            }
        }
    }
}
