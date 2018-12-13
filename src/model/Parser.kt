package model

class Parser(private val data: EditorData) {
    companion object {
        private const val whitespaces = "\t\n\r "
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
            Regex("""(^|[^\w\d_])(""" + keywords.joinToString("|") + """)(?![\w\d_])""")
        }
        val numbersRegex : Regex by lazy {
            Regex("""(^|[^\w\d_])(\d+)($|[^\w\d_])""")
        }
        val commentRegex : Regex by lazy {
            Regex("""(//.*($|[\n]))|([/][*](.|\n)*?[*][/])""")
        }
    }

    fun apply() {
        markCommentsAndStrings()

        for (i in 0 until data.size) {
            val row = data.get(i)
            for (j in 0 until row.size) {
                val c = row.get(j)
                if (c.type == CodeType.COMMENT || c.type == CodeType.STRING) continue
                c.type = when {
                    Parser.isWhitespace(c.char) -> CodeType.WHITESPACE
                    Parser.canBeInName(c.char) -> CodeType.BASE
                    else -> CodeType.OPERATOR
                }
            }
            val str = data.getRow(i)
            for (pair in mapOf(CodeType.KEYWORD to Parser.keywordsRegex, CodeType.NUMBER to Parser.numbersRegex)) {
                val type = pair.key
                for (match in pair.value.findAll(str)) {
                    for (j in match.groups[2]!!.range) {
                        val c = row.get(j)
                        if (c.type == CodeType.COMMENT || c.type == CodeType.STRING) continue
                        c.type = type
                    }
                }
            }
        }
    }

    private fun markCommentsAndStrings() {
        val text = data.getText()
        val mark = Array(text.length) {false}
        for (match in Parser.commentRegex.findAll(text))
            for (i in match.range)
                mark[i] = true
        var rowI = 0
        var row = data.get(rowI)
        var column = 0
        val prev = arrayOf(-1, -1)
        val quote = arrayOf('"', '\'')

        for (i in 0 until text.length) {
            if (text[i] != '\n') {
                val c = row.get(column)
                if (mark[i]) {
                    c.type = CodeType.COMMENT
                } else {
                    c.type = CodeType.BASE
                    for (k in 0..1) {
                        if (c.char == quote[k]) {
                            println("k=$k, i=$i prev=${prev[k]}, row=$rowI column=$column")
                            if (prev[k] != -1) {
                                for (j in 0 .. i - prev[k]) {
                                    val cell = row.get(column - j)
                                    if (cell.type != CodeType.COMMENT) {
                                        cell.type = CodeType.STRING
                                    }
                                }
                                prev[k] = -1
                            } else {
                                prev[k] = i
                            }
                        }
                    }
                }
            } else {
                prev[0] = -1
                prev[1] = -1
            }
            column++
            if (text[i] == '\n') {
                if (++rowI < data.size)
                    row = data.get(rowI)
                column = 0
            }
        }
    }
}
