import files.selectFile
import java.awt.Component
import java.awt.EventQueue
import java.awt.event.*
import java.io.File
import javax.swing.*

class EditorWindow : JFrame() {
    private val tabbedPane = JTabbedPane()
    private val sourceDir = "tests/"
    private val currentTab: EditorTab
        get() = tabbedPane.selectedComponent as EditorTab

    init {
        createMenubar()
        this.iconImage = images.loadImage("icon.png")
        this.title = "Java Editor"
        this.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        this.setSize(600, 400)
        this.setLocationRelativeTo(null)
        this.add(tabbedPane)
        tabbedPane.addChangeListener {
            println("tabbed paned change listener: ${tabbedPane.tabCount} tabs")
            if (tabbedPane.tabCount > 0) {
                print("selected tab: ")
                currentTab.canvas.log()
            }
        }
        newTab(File("${sourceDir}Main.test"))
        newTab(File("${sourceDir}WrongName"))
        newTab(File("${sourceDir}DataArray.test"))
        isFocusable = true
        focusTraversalKeysEnabled = false
        addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                if (tabbedPane.tabCount > 0)
                    for (listener in currentTab.keyListeners)
                        listener.keyTyped(e)
            }
            override fun keyPressed(e: KeyEvent) {
                if (e.isControlDown) {
                    when (e.keyCode) {
                        KeyEvent.VK_N -> newTab()
                        KeyEvent.VK_O -> openTab()
                        KeyEvent.VK_S -> saveTab()
                        KeyEvent.VK_W -> closeTab()
                        KeyEvent.VK_F4 -> closeTab()
                    }
                } else {
                    when (e.keyCode) {
                        KeyEvent.VK_ESCAPE -> closeApp()
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
                if (tabbedPane.tabCount > 0)
                    for (listener in currentTab.keyListeners)
                        listener.keyPressed(e)
            }
            override fun keyReleased(e: KeyEvent) {
                if (tabbedPane.tabCount > 0)
                    for (listener in currentTab.keyListeners)
                        listener.keyReleased(e)
            }
        })
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                println("event: window closing")
                closeApp()
            }
        })
    }

    private fun createMenubar() {
        val menubar = JMenuBar()
        val file = JMenu("File")
        file.mnemonic = KeyEvent.VK_F

        fun addItem(name: String, key: Int, action: (ActionEvent) -> Unit, icon: ImageIcon? = null) {
            val item = JMenuItem(name, icon)
            item.mnemonic = key
            item.addActionListener(action)
            file.add(item)
        }

        addItem("New", KeyEvent.VK_N, { EventQueue.invokeLater { newTab() }})
        addItem("Open", KeyEvent.VK_O, { EventQueue.invokeLater { openTab() }})
        addItem("Save", KeyEvent.VK_S, { EventQueue.invokeLater { saveTab() }})
        addItem("Close file", KeyEvent.VK_C, { EventQueue.invokeLater { closeTab() }})
        addItem("Exit", KeyEvent.VK_X, { closeApp() }, images.loadIcon("exit.png"))

        menubar.add(file)
        this.jMenuBar = menubar
    }

    private fun saveTab() {
        currentTab.save()
        updateTitles()
    }

    private fun closeApp() {
        println("call closeApp")
        var needSave = false
        with (tabbedPane) {
            for (i in 0 until tabCount)
                needSave = needSave or (getComponentAt(i) as EditorTab).needSave
        }
        if (needSave) {
            val options = arrayOf<Any>("Save & Exit", "Just exit", "Cancel")
            val choice = JOptionPane.showOptionDialog(this, "There are unsaved changes. What to do?",
                "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])
            when (choice) {
                0 -> {
                    with (tabbedPane) {
                        for (i in 0 until tabCount) {
                            val tab = getComponentAt(i) as EditorTab
                            if (tab.needSave)
                                tab.save()
                        }
                    }
                    System.exit(0)
                }
                1 -> {
                    System.exit(0)
                }
            }
        } else {
            System.exit(0)
        }
    }

    private fun closeTab() {
        if (tabbedPane.tabCount > 0 && currentTab.close())
            tabbedPane.removeTabAt(tabbedPane.selectedIndex)
    }

    private fun newTab(f: File? = null) {
        @Suppress("NAME_SHADOWING") val f = files.checkFileForReading(f)
        println("newTab: ${f?.name}")
        val tab = EditorTab(f)
        tab.canvas.editor.registerEditListener { updateTitles() }
        tabbedPane.addTab(tab.title, tab)
        tabbedPane.selectedIndex = tabbedPane.tabCount - 1
    }

    private fun updateTitles() {
        with (tabbedPane) {
            for (i in 0 until tabCount)
                setTitleAt(i, (getComponentAt(i) as EditorTab).title)
        }
    }

    private fun openTab() {
        println("open new tab...")
        newTab(selectFile(this as Component, JFileChooser.OPEN_DIALOG))
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        val frame = EditorWindow()
        frame.revalidate()
        frame.isVisible = true
    }
}
