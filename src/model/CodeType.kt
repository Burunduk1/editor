package model

enum class CodeType {
    BASE, WHITESPACE, EMPTY, KEYWORD, OPERATOR, NUMBER, COMMENT, STRING
}

data class CodeChar(var char: Char, var type: CodeType = CodeType.BASE)

val emptyChar = CodeChar(' ', CodeType.EMPTY)
