package uk.co.akm.util.manager.password.io

import java.io.File
import java.nio.charset.Charset

/**
 * Reads the pass-phrase from the file in the first input array argument or from the command line,
 * if the array is empty. Please not that this input array cannot have multiple arguments.
 */
fun readPassword(args: Array<String>): String {
    if (args.isEmpty()) {
        return readPasswordFromConsole()
    } else if (args.size == 1) {
        return readPasswordFromFile(args[0])
    } else {
        exit("There are ${args.size} command line arguments. There can only be one or none.")
        return ""
    }
}

private fun readPasswordFromConsole(): String {
    println("Please enter the pass-phrase:")
    val password = readInputLine(true).trim()
    if (password.isEmpty() || password.isBlank()) {
        exit("The pass-phrase cannot be blank or empty.")
    }

    return password
}

private fun readPasswordFromFile(fileName: String): String {
    val file = File(fileName)
    if (!file.exists()) {
        exit("Pass-phrase file '$fileName' does not exist.")
    }

    val password = file.readText(Charset.defaultCharset())
    if (password.isBlank()) {
        exit("Pass-phrase file '$fileName' is empty or contains a blank pass-phrase.")
    }

    println("Pass-phrase read from '$fileName'.")
    return password;
}

private fun exit(msg: String) {
    System.err.println(msg)
    System.exit(1)
}
