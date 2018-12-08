package files

import java.io.File

fun openOrCreate(f: File?) = when {
    f == null -> null
    f.exists() -> f.bufferedReader()
    else -> f.bufferedReader()
}.also { println("openOrCreate: filename = ${f?.name}")}

fun openOrCreate(s: String?) = s?.let {openOrCreate(File(s))}
