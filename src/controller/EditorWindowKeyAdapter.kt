package controller

import model.EditorWindow
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class EditorWindowKeyAdapter(private val frame: EditorWindow) : KeyAdapter() {
    private val tabbedPane = frame.tabbedPane
    private val closeByEscape = false
    override fun keyTyped(e: KeyEvent) {
        tabbedPane.forwardKeyEvent(e)
    }
    override fun keyPressed(e: KeyEvent) {
        if (e.isControlDown) {
            when (e.keyCode) {
                KeyEvent.VK_N -> tabbedPane.newTab()
                KeyEvent.VK_O -> tabbedPane.openTab()
                KeyEvent.VK_W -> tabbedPane.closeTab()
                KeyEvent.VK_F4 -> tabbedPane.closeTab()
                KeyEvent.VK_S ->
                    if (e.isShiftDown)
                        tabbedPane.saveTabAs()
                    else
                        tabbedPane.saveTab()
            }
        } else {
            when (e.keyCode) {
                KeyEvent.VK_ESCAPE ->
                    if (closeByEscape)
                        frame.closeApp()
            }
        }
        with (tabbedPane) {
            if (e.isAltDown && tabCount != 0) {
                when (e.keyCode) {
                    KeyEvent.VK_RIGHT -> selectedIndex = (selectedIndex + 1) % tabCount
                    KeyEvent.VK_LEFT -> selectedIndex = (selectedIndex + tabCount - 1) % tabCount
                }
            }
        }
        tabbedPane.forwardKeyEvent(e)
    }
    override fun keyReleased(e: KeyEvent) {
        tabbedPane.forwardKeyEvent(e)
    }
}
