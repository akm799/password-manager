package uk.co.akm.util.manager.password.io

import java.io.File

/**
 * Created by thanos Mavroidis on 27/09/2018.
 */

private val fileName = ".password-manager-store.dat"

fun findOrCreateStoreFile(): File {
    val file = File(buildFilePath())
    if (!file.exists()) {
        file.createNewFile()
    }

    return file
}

private fun buildFilePath(): String {
    val homeDir = System.getProperty("user.home")

    return if (homeDir.endsWith(File.separator)) "$homeDir$fileName" else "$homeDir${File.separator}$fileName"
}