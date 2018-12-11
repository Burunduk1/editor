package parser

import ds.DataArray

enum class CodeType {
    BASE, WHITESPACE, EMPTY, KEYWORD, OPERATOR, NUMBER, COMMENT
}

class CodeChar(var char: Char, var type: CodeType = CodeType.BASE)

class Parser(private val data: DataArray<DataArray<CodeChar>>) {
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
            Regex("""(^|[^\w\d_])(""" + keywords.joinToString("|") + """)($|[^\w\d_])""")
        }
        val numbersRegex : Regex by lazy {
            Regex("""(^|[^\w\d_])(\d+)($|[^\w\d_])""")
        }
        val commentLine : Regex by lazy {
            Regex("""//.*[\n]""")
        }
        val commentBlock : Regex by lazy {
            Regex("""[/][*](.|\n)*?[*][/]""")
        }
    }

    private fun getRow(i: Int) = data.get(i).toList().map {it.char}.joinToString("")
    private fun getText() = (0 until data.size).joinToString("\n") { getRow(it) }

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
            val str = getRow(i)
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
        val text = getText()
        var rightBorder = 0
        val mark = Array(text.length) {false}
        fun commentRange(range: IntRange) {
            for (i in range)
                mark[i] = true
            rightBorder = range.last + 1
        }
        while (true) {
            val line = Parser.commentLine.find(text, rightBorder)
            val block = Parser.commentBlock.find(text, rightBorder)
            if (line == null && block == null)
                break
            if (line != null && (block == null || line.range.first < block.range.first))
                commentRange(line.range)
            else
                commentRange(block!!.range)

        }
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
