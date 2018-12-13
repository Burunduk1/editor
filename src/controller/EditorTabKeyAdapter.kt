package controller

import model.EditorTab
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class EditorTabKeyAdapter(private val editorTab: EditorTab): KeyAdapter() {
    companion object {
        val skippedKeys = arrayOf(8, 9, 10, 13, 27, 127)
    }
    private val editor = editorTab.editor
    private val selection = editorTab.canvas.selection
    private val selectionKeyAdapter = SelectionKeyAdapter(selection)

    override fun keyTyped(e: KeyEvent) {
        val code = e.keyChar.toInt()
        if (skippedKeys.indexOf(code) != -1 || e.isAltDown || e.isControlDown || e.isActionKey || e.isMetaDown) {
            return
        }
        editor.editTypeChar(e.keyChar)
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.isAltDown)
            return
        val selectModeBeforeKeyEvent = selection.on
        val rowsOnPage = maxOf(1, editorTab.canvas.rowsOnScreen - 1)
        selectionKeyAdapter.keyPressed(e)
        when (e.keyCode) {
            KeyEvent.VK_UP -> {
                editor.navigateUp(1)
            }
            KeyEvent.VK_DOWN -> {
                editor.navigateDown(1)
            }
            KeyEvent.VK_PAGE_UP -> {
                editor.navigateUp(rowsOnPage)
            }
            KeyEvent.VK_PAGE_DOWN -> {
                editor.navigateDown(rowsOnPage)
            }
            KeyEvent.VK_LEFT -> {
                if (e.isControlDown)
                    editor.navigateTermLeft()
                else
                    editor.navigateLeft()
            }
            KeyEvent.VK_RIGHT -> {
                if (e.isControlDown)
                    editor.navigateTermRight()
                else
                    editor.navigateRight()
            }
            KeyEvent.VK_DELETE -> {
                if (selectModeBeforeKeyEvent) {
                    if (e.isShiftDown)
                        editorTab.cutEvent()
                    else
                        editorTab.deleteEvent()
                } else {
                    editor.editDelete()
                }
            }
            KeyEvent.VK_BACK_SPACE -> {
                editor.editBackspace()
            }
            KeyEvent.VK_ENTER -> {
                editor.editNewline()
            }
            KeyEvent.VK_HOME -> {
                if (e.isControlDown)
                    editor.navigateToBegin()
                else
                    editor.navigateHome()
            }
            KeyEvent.VK_END -> {
                if (e.isControlDown)
                    editor.navigateToEnd()
                else
                    editor.navigateEnd()
            }
            KeyEvent.VK_TAB -> {
                editorTab.tabKeyEvent()
            }
            KeyEvent.VK_Y -> {
                if (e.isControlDown)
                    editor.editDeleteLine()
            }
            KeyEvent.VK_D -> {
                if (e.isControlDown)
                    editor.editDuplicateLine()
            }
            KeyEvent.VK_C -> {
                if (e.isControlDown)
                    editorTab.copyEvent()
            }
            KeyEvent.VK_V -> {
                if (e.isControlDown) {
                    selection.on = false
                    editorTab.pasteEvent()
                }
            }
            KeyEvent.VK_X -> {
                if (e.isControlDown) {
                    editorTab.cutEvent()
                    selection.on = false
                }
            }
        }
        if (selectModeBeforeKeyEvent != selection.on)
            editorTab.repaint()
    }
}