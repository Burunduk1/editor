package model

enum class CodeType {
    BASE, WHITESPACE, EMPTY, KEYWORD, OPERATOR, NUMBER, COMMENT
}

class CodeChar(var char: Char, var type: CodeType = CodeType.BASE)

val emptyChar = CodeChar(' ', CodeType.EMPTY)