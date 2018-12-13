package component

import javax.swing.JOptionPane
import javax.swing.JFrame

private val options = arrayOf<Any>("Save & Exit", "Just exit", "Cancel")
fun saveFileChooser(parent: JFrame) =
    JOptionPane.showOptionDialog(parent, "There are unsaved changes. What to do?",
        "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2])

fun saveFileChooser(needChoose: Boolean, parent: JFrame, saveAction: () -> Boolean, closeAction: () -> Unit) {
    if (!needChoose)
        closeAction()
    else {
        val choice = saveFileChooser(parent)
        when (choice) {
            0 -> {
                if (saveAction())
                    closeAction()
            }
            1 -> {
                closeAction()
            }
        }
    }
}