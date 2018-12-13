package utility

import java.awt.Component
import java.io.File
import javax.swing.JFileChooser

fun checkFileForReading(f: File?): File? = if (f?.canRead() == false) null else f
fun checkFileForWriting(f: File?): File? {
    if (f == null)
        return null
    if (f.exists())
        return if (f.canWrite()) f else null
    try {
        f.printWriter().close()
        f.delete()
    } catch (_: Exception) {
        println("Exception!")
        return null
    }
    return f
}

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
