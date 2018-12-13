package utility

import java.awt.Component
import java.io.File
import javax.swing.JFileChooser

private fun checkFileCan(f: File?, action: () -> Unit): File? {
    if (f?.exists() == false)
        return null
    try {
        action()
    } catch (e: Exception) {
        println("Exception!")
        return null
    }
    return f
}

fun checkFileForReading(f: File?): File? = checkFileCan(f) { f?.bufferedReader() }
fun checkFileForWriting(f: File?): File? = checkFileCan(f) { f?.printWriter() }

//fun getFile(s: String?) = s?.let { checkFileForReading(File(s)) }

fun openFileForReading(f: File) = f.bufferedReader().also { println("openFileForReading: filename = ${f.name}") }
fun openFileForWriting(f: File) = f.printWriter().also { println("openFileForWriting: filename = ${f.name}") }

fun selectFile(parent: Component, dialogType: Int): File? {
    val fc = JFileChooser()
    fc.isMultiSelectionEnabled = false
    fc.dialogType = dialogType
    return when (fc.showOpenDialog(parent)) {
        JFileChooser.APPROVE_OPTION -> fc.selectedFile
        else -> null
    }
}

fun selectFileForWriting(parent: Component) = checkFileForWriting(selectFile(parent, JFileChooser.SAVE_DIALOG))
fun selectFileForReading(parent: Component) = checkFileForReading(selectFile(parent, JFileChooser.OPEN_DIALOG))
