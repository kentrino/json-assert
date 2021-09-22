package jsonassert.util


internal object UserStackTraceConverter {
    fun getUserStacktrace(kotestStacktraces: Array<StackTraceElement>): Array<StackTraceElement> {
        return kotestStacktraces.dropUntilUserClass()
    }

    private fun Array<StackTraceElement>.dropUntilUserClass(): Array<StackTraceElement> {
        return toList().dropUntilFirstKotestClass().dropUntilFirstNonKotestClass().toTypedArray()
    }

    private fun List<StackTraceElement>.dropUntilFirstKotestClass(): List<StackTraceElement> {
        return dropWhile {
            it.isNotKotestClass()
        }
    }

    private fun List<StackTraceElement>.dropUntilFirstNonKotestClass(): List<StackTraceElement> {
        return dropWhile {
            it.isKotestClass()
        }
    }

    private fun StackTraceElement.isKotestClass(): Boolean {
        return className.startsWith("io.kotest")
    }

    private fun StackTraceElement.isNotKotestClass(): Boolean {
        return !isKotestClass()
    }
}
