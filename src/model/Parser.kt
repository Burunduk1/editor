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
            Regex("""(//.*[\n])|([/][*](.|\n)*?[*][/])""")
        }
    }

    fun apply() {
        markComments()

        for (i in 0 until data.size) {
            val row = data.get(i)
            for (j in 0 until row.size) {
                val c = row.get(j)
                if (c.type == CodeType.COMMENT) continue
                c.type = when {
                    Parser.isWhitespace(c.char) -> CodeType.WHITESPACE
                    Parser.canBeInName(c.char) -> CodeType.BASE
                    else -> CodeType.OPERATOR
                }
            }
            val str = data.getRow(i)
            for (match in Parser.keywordsRegex.findAll(str)) {
                for (j in match.groups[2]!!.range) {
                    if (row.get(j).type == CodeType.COMMENT) continue
                    row.get(j).type = CodeType.KEYWORD
                }
            }
            for (match in Parser.numbersRegex.findAll(str)) {
                for (j in match.groups[2]!!.range) {
                    if (row.get(j).type == CodeType.COMMENT) continue
                    row.get(j).type = CodeType.NUMBER
                }
            }
        }
    }

    private fun markComments() {
        val text = data.getText()
        val mark = Array(text.length) {false}
        for (match in Parser.commentRegex.findAll(text))
            for (i in match.range)
                mark[i] = true
        var row = 0
        var column = 0
        for (i in 0 until text.length) {
            if (text[i] != '\n')
                data.get(row).get(column).type = if (mark[i]) CodeType.COMMENT else CodeType.BASE
            column++
            if (text[i] == '\n') {
                row++
                column = 0
            }
        }
    }
}
