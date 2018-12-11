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
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
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
                for (listener in currentTab.keyListeners)
                    listener.keyTyped(e)
            }
            override fun keyPressed(e: KeyEvent) {
                for (listener in currentTab.keyListeners)
                    listener.keyPressed(e)
            }
            override fun keyReleased(e: KeyEvent) {
                for (listener in currentTab.keyListeners)
                    listener.keyReleased(e)
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
        addItem("Exit", KeyEvent.VK_E, { System.exit(0) }, images.loadIcon("exit.png"))

        menubar.add(file)
        this.jMenuBar = menubar
    }

    private fun saveTab() {
        currentTab.save()
        updateTitles()
    }

    private fun closeTab() {
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
