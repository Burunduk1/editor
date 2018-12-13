package controller

import model.Selection
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class SelectionKeyAdapter(val selection: Selection): KeyAdapter() {
    companion object {
        private val navigationKeys: Array<Int> by lazy { arrayOf(
            KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT,
            KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP, KeyEvent.VK_HOME, KeyEvent.VK_END,
            KeyEvent.VK_SHIFT
        )}
    }
    override fun keyPressed(e: KeyEvent) {
        with (selection) {
            val isNavigationKey = navigationKeys.indexOf(e.keyCode) != -1
            if (!e.isControlDown && !e.isAltDown && (!e.isShiftDown || !isNavigationKey)) {
                on = false
            } else if (e.isControlDown && !e.isAltDown && !e.isShiftDown && isNavigationKey) {
                on = false
            } else if (!on) {
                startSelection()
            }
        }
    }
}