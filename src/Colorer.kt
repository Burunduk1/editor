package parser

import ds.DataArray

enum class CodeType {
    BASE, WHITESPACE, EMPTY, KEYWORD, OPERATOR
}

data class CodeChar(var char: Char, var type: CodeType)

private val parserLock = java.lang.Object()

data class Parser(val data: DataArray<DataArray<CodeChar>>) {
    object companion {
        const val whitespaces = "\t\n\r "
        fun isWhitespace(char: Char): Boolean {
            return whitespaces.indexOf(char) != -1
        }
        fun canBeInName(char: Char): Boolean {
            return char.isLetterOrDigit() || char == '_'
        }
        init {
            println("init colorer!")
        }
    }

    fun apply() = synchronized (parserLock) {
        println("wait...")
        parserLock.wait()
        println("ok")
        for (i in 0 until data.size) {
            val row = data.get(i)
            for (j in 0 until row.size) {
                val c = row.get(j)
                c.type = when {
                    Parser.companion.isWhitespace(c.char) -> CodeType.WHITESPACE
                    Parser.companion.canBeInName(c.char) -> CodeType.BASE
                    else -> CodeType.OPERATOR
                }
            }
            // TODO: highlight keywords
//            val str = row.toList().map {it.char}.toString()
        }
    }
}
