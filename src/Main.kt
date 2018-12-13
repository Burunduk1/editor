import model.EditorWindow
import java.awt.EventQueue
import java.io.File

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        val frame = EditorWindow()
        frame.revalidate()
        frame.isVisible = true

        val sourceDir = "tests/"
        frame.tabbedPane.newTab(File("${sourceDir}Main.test"))
        frame.tabbedPane.newTab(File("${sourceDir}WrongName"))
        frame.tabbedPane.newTab(File("${sourceDir}DataArray.test"))
    }
}
