package uk.co.akm.util.manager.password

import uk.co.akm.util.manager.password.console.impl.CredentialsPresenter
import uk.co.akm.util.manager.password.exceptions.CredentialsParseException
import uk.co.akm.util.manager.password.io.*

fun main(args: Array<String>) {
    val password = readPassword()
    val file = findOrCreateStoreFile()
    val storeHandle: CredentialsStoreHandle = CredentialsStoreHandleImpl(password, file)

    try {
        val credentials = storeHandle.read()
        println("Credentials read from ${file.absolutePath}")

        val presenter = CredentialsPresenter(credentials, storeHandle)
        presenter.launch()

        if (presenter.haveChanges) {
            storeHandle.save(presenter.credentials)
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
