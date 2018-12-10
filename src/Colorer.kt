package parser

import ds.DataArray

enum class CodeType {
    BASE, WHITESPACE, EMPTY, KEYWORD, OPERATOR
}

data class CodeChar(var char: Char, var type: CodeType)

data class Parser(val data: DataArray<DataArray<CodeChar>>) {
    companion object {
        const val whitespaces = "\t\n\r "
        fun isWhitespace(char: Char): Boolean {
            return whitespaces.indexOf(char) != -1
        }
        fun canBeInName(char: Char): Boolean {
            return char.isLetterOrDigit() || char == '_'
        }
        private val keywords : Array<String> by lazy {
            arrayOf(
                "abstract",
                "assert",
                "boolean",
                "break",
                "byte",
                "case",
                "catch",
                "char",
                "class",
                "const",
                "continue",
                "default",
                "do",
                "double",
                "else",
                "enum",
                "extends",
                "final",
                "finally",
                "float",
                "for",
                "goto",
                "if",
                "implements",
                "import",
                "instanceof",
                "int",
                "interface",
                "long",
                "native",
                "new",
                "package",
                "private",
                "protected",
                "public",
                "return",
                "short",
                "static",
                "strictfp",
                "super",
                "switch",
                "synchronized",
                "this",
                "throw",
                "throws",
                "transient",
                "try",
                "void",
                "volatile",
                "while",
                "true",
                "false",
                "null"
            )
        }
        val keywordsRegex : Regex by lazy {
            Regex("""(^|[^\w\d_])(""" + keywords.joinToString("|") + """)($|[^\w\d_])""")
        }
    }

    fun apply() {
        for (i in 0 until data.size) {
            val row = data.get(i)
            for (j in 0 until row.size) {
                val c = row.get(j)
                c.type = when {
                    Parser.isWhitespace(c.char) -> CodeType.WHITESPACE
                    Parser.canBeInName(c.char) -> CodeType.BASE
                    else -> CodeType.OPERATOR
                }
            }
            val str = row.toList().map {it.char}.joinToString("")
            for (match in Parser.keywordsRegex.findAll(str)) {
                for (j in match.groups[2]!!.range) {
                    row.get(j).type = CodeType.KEYWORD
                }
                //println(match.groups[2])
            }
        }
    }
}
