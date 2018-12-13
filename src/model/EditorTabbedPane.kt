package model

import component.saveFileChooser
import utility.checkFileForReading
import utility.selectFileForReading
import java.awt.Component
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.JFrame
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities

class EditorTabbedPane : JTabbedPane() {
    val currentTab: EditorTab?
        get() = if (tabCount > 0) (selectedComponent as EditorTab) else null
    private val empty: Boolean
        get() = tabCount == 0

    fun forwardKeyEvent(e: KeyEvent) {
        currentTab?.keyListeners?.forEach { utility.handleKeyEvent(it, e) }
    }

    fun updateTitles() {
        for (i in 0 until tabCount)
            setTitleAt(i, (getComponentAt(i) as EditorTab).title)
    }

    fun saveTab() {
        currentTab?.save()
        updateTitles()
    }
    fun saveTabAs() {
        currentTab?.saveAs()
        updateTitles()
    }

    fun saveAllTabs(): Boolean {
        for (i in 0 until tabCount) {
            val tab = getComponentAt(i) as EditorTab
            if (!tab.saved && !tab.save()) {
                return false
            }
        }
        return true
    }

    fun closeTab() {
        if (!empty) {
            saveFileChooser(!currentTab!!.saved, SwingUtilities.getWindowAncestor(this) as JFrame,
                { currentTab!!.save() },
                { removeTabAt(selectedIndex) }
            )
        }
    }

    fun newTab(_f: File? = null) {
        val f = checkFileForReading(_f)
        println("newTab: ${f?.name}")
        val tab = EditorTab(f)
        tab.canvas.editor.registerEditListener { updateTitles() }
        addTab(tab.title, tab)
        selectedIndex = tabCount - 1
    }

    fun openTab() = newTab(selectFileForReading(this))

    fun handleCutEvent() { currentTab?.cutEvent() }
    fun handleCopyEvent() { currentTab?.copyEvent() }
    fun handlePasteEvent() { currentTab?.pasteEvent() }
}
