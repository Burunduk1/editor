package files

import java.io.File

fun checkFile(f: File?): File? {
    if (f?.exists() == null)
        return null
    try {
        f.bufferedReader().close()
    } catch (_: Exception) {
        println("Exception!")
        return null
    }
    return f
}
fun getFile(s: String?) = s?.let {checkFile(File(s))}

fun openFile(f: File) =
    f.bufferedReader().also {
        println("openFile: filename = ${f.name}")
    }

