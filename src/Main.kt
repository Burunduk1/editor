import java.awt.EventQueue
import java.awt.event.*
import java.io.File
import javax.swing.*

class EditorWindow : JFrame() {
    private val tabbedPane = JTabbedPane()
    private val sourceDir = "C:\\Users\\SK\\IdeaProjects\\editor\\src\\"

    init {
        createMenubar()
        this.iconImage = images.loadImage("icon.png")
        this.title = "Java Editor"
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setSize(600, 400)
        this.setLocationRelativeTo(null)
        this.add(tabbedPane)
        tabbedPane.addChangeListener {
            println("tabbed paned: ${tabbedPane.tabCount} tabs")
            if (tabbedPane.tabCount > 0) {
                print("selected tab: ")
                (tabbedPane.selectedComponent as EditorTab).canvas.log()
            }
        }
        newTab(File("${sourceDir}Main.kt"))
        newTab(File("${sourceDir}WrongName"))
        newTab(File("${sourceDir}DataArray.kt"))
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
        addItem("Exit", KeyEvent.VK_E, { System.exit(0) }, images.loadIcon("exit.png"))

        menubar.add(file)
        this.jMenuBar = menubar
    }

    private fun newTab(f: File? = null) {
        @Suppress("NAME_SHADOWING") val f = files.checkFile(f)
        println("newTab: ${f?.name}")
        val tab = EditorTab(f)
        tabbedPane.addTab(tab.title, tab)
        tabbedPane.selectedIndex = tabbedPane.tabCount - 1
    }

    private fun openTab() {
        println("open new tab...")
        val fc = JFileChooser()

        when (fc.showOpenDialog(this)) {
            JFileChooser.APPROVE_OPTION -> newTab(fc.selectedFile)
            else -> println("opening was canceled by user")
        }
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        val frame = EditorWindow()
        frame.revalidate()
        frame.isVisible = true
    }
}
