package uk.co.akm.util.manager.password.io

/**
 * Created by Thanos Mavroidis on 07/10/2018.
 */

private val console = System.console()
private val lineEndChars = arrayListOf('\n', '\r')

fun readInputLine(hide: Boolean = false): String {
    if (console == null) {
        return readInputLineFromSystemIn()
    }

    return (if (hide) String(console.readPassword()) else console.readLine()).trim()
}

private fun readInputLineFromSystemIn(): String {
    val sb = StringBuilder()

    var c: Char? = null
    while ( !lineEndChars.contains({ c = System.`in`.read().toChar(); c}()) ) {
        sb.append(c)
    }


    return sb.toString().trim()
}
