package model

import component.saveFileChooser
import controller.EditorWindowKeyAdapter
import java.awt.EventQueue
import java.awt.event.*
import javax.swing.*

class EditorWindow : JFrame() {
    val tabbedPane = EditorTabbedPane()

    init {
        this.jMenuBar = createMenuBar()
        this.iconImage = utility.loadImage("icon.png")
        this.title = "Java Editor"
        this.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        this.setSize(600, 400)
        this.setLocationRelativeTo(null)
        this.add(tabbedPane)
        isFocusable = true
        focusTraversalKeysEnabled = false
        addKeyListener(EditorWindowKeyAdapter(this))
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                closeApp()
            }
        })
    }

    private fun createMenuBar() = {
        val menubar = JMenuBar()

        var subMenu = JMenu("")
        fun addItem(name: String, key: Int, action: (ActionEvent) -> Unit, icon: ImageIcon? = null) {
            val item = JMenuItem(name, icon)
            item.mnemonic = key
            item.addActionListener(action)
            subMenu.add(item)
        }

        subMenu = JMenu("File")
        subMenu.mnemonic = KeyEvent.VK_F
        addItem("New", KeyEvent.VK_N, { EventQueue.invokeLater { tabbedPane.newTab() }})
        addItem("Open", KeyEvent.VK_O, { EventQueue.invokeLater { tabbedPane.openTab() }})
        addItem("Save", KeyEvent.VK_S, { EventQueue.invokeLater { tabbedPane.saveTab() }})
        addItem("Save As", KeyEvent.VK_A, { EventQueue.invokeLater { tabbedPane.saveTabAs() }})
        addItem("Close file", KeyEvent.VK_C, { EventQueue.invokeLater { tabbedPane.closeTab() }})
        addItem("Exit", KeyEvent.VK_X, { closeApp() }, utility.loadIcon("exit.png"))
        menubar.add(subMenu)

        subMenu = JMenu("Edit")
        subMenu.mnemonic = KeyEvent.VK_E
        addItem("Cut", KeyEvent.VK_T, { EventQueue.invokeLater { tabbedPane.handleCutEvent() }})
        addItem("Copy", KeyEvent.VK_C, { EventQueue.invokeLater { tabbedPane.handleCopyEvent() }})
        addItem("Paste", KeyEvent.VK_P, { EventQueue.invokeLater { tabbedPane.handlePasteEvent() }})
        menubar.add(subMenu)

        menubar
    }()

    fun closeApp() {
        var needSave = false
        with (tabbedPane) {
            for (i in 0 until tabCount)
                needSave = needSave or !(getComponentAt(i) as EditorTab).saved
        }
        saveFileChooser(needSave, this, { tabbedPane.saveAllTabs() }, { System.exit(0) })
        tabbedPane.updateTitles() // case: some files were saved, some not
    }
}