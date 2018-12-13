package model

import component.EditorCanvas
import controller.EditorTabKeyAdapter
import utility.openFileForWriting
import utility.selectFileForWriting
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

class EditorTab(var file: File?) : JPanel(BorderLayout()) {
    companion object {
        val tabSize: Int by lazy { 4 }
    }
    val editor = Editor()
    val canvas = EditorCanvas(editor, ColorTheme())

    val saved: Boolean
        get() = editor.saved
    val title: String
        get() = (if (saved) "" else "*") + if (file == null) "<untitled>" else file!!.name // unsafe concurrency

    init {
        this.add(canvas)
        file?.let {
            println("editor: load ${file!!.name}")
            editor.load(utility.openFileForReading(file!!))
        }
        addKeyListener(EditorTabKeyAdapter(this))
        fun handleCursorAction() {
            canvas.scrollToCursor()
            canvas.repaint()
        }
        editor.registerNavigateListener { handleCursorAction() }
        editor.registerEditListener { handleCursorAction() }
    }

    fun tabKeyEvent() {
        for (i in 0 until tabSize)
            editor.editTypeChar(' ')
    }
    fun copyEvent() {
        canvas.selection.areaHandle(editor::copyToClipboard)
    }
    fun pasteEvent() {
        editor.pasteFromClipboard()
    }
    fun deleteEvent() {
        canvas.selection.areaHandle(editor::deleteBlock)
    }
    fun cutEvent() {
        copyEvent()
        deleteEvent()
    }

    private fun saveRoutine(): Boolean {
        if (file != null) {
            openFileForWriting(file!!).use {
                editor.save(it)
                return true
            } // unsafe concurrency
        }
        return false
    }
    fun save(): Boolean {
        file = file ?: selectFileForWriting(this)
        return saveRoutine()
    }
    fun saveAs(): Boolean {
        val newFile = selectFileForWriting(this)
        if (newFile != null) {
            file = newFile
            return saveRoutine()
        }
        return false
    }
}
