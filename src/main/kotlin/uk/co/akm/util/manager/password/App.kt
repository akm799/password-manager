package uk.co.akm.util.manager.password

import uk.co.akm.util.manager.password.console.impl.CredentialsPresenter
import uk.co.akm.util.manager.password.crypto.CryptoService
import uk.co.akm.util.manager.password.exceptions.CredentialsParseException
import uk.co.akm.util.manager.password.io.CredentialsStore
import uk.co.akm.util.manager.password.io.findOrCreateStoreFile
import uk.co.akm.util.manager.password.io.readInputLine
import uk.co.akm.util.manager.password.model.Credentials
import java.io.File

fun main(args: Array<String>) {
    val password = readPassword()
    val file = findOrCreateStoreFile()

    val cryptoService = CryptoService.aesGcmInstance(password)
    val credentialsStore = CredentialsStore.encryptedInstance(cryptoService)

    try {
        val credentials = credentialsStore.read(file)
        println("Credentials read from ${file.absolutePath}")

        val presenter = CredentialsPresenter(credentials)
        presenter.launch()

        if (presenter.haveChanges) {
            credentialsStore.write(presenter.credentials, file)
            println("Credentials saved in ${file.absolutePath}")
        }
    } catch (cpe: CredentialsParseException) {
        System.err.println("Incorrect pass-phrase.")
    }
}

private fun readPassword(): String {
    println("Please enter the pass-phrase:")
    val password = readInputLine(true).trim()
    if (password.isEmpty() || password.isBlank()) {
        exit()
    }

    return password
}

private fun exit() {
    System.err.println("The pass-phrase cannot be blank or empty.")
    System.exit(1)
}