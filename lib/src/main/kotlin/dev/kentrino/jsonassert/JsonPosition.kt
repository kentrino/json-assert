package dev.kentrino.jsonassert


data class JsonPosition(private val keyChain: MutableList<JsonKey>) {
    fun extend(key: JsonKey): JsonPosition = JsonPosition(keyChain.toMutableList().apply {
        add(key)
    })

    fun <R>fold(initial: R, operation: (R, JsonKey) -> R): R = keyChain.fold(initial, operation)

    fun print(): String = keyChain.joinToString(".") { it.string }

    companion object {
        val root = JsonPosition(mutableListOf())
    }
}
