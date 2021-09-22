package jsonassert


sealed class JsonKey {
    class IntKey(val value: Int): JsonKey() {
        override val string: String
            get() = value.toString()
    }
    class StringKey(val value: String): JsonKey() {
        override val string: String
            get() = value
    }

    abstract val string: String
}
