package uk.co.akm.util.manager.password

import uk.co.akm.util.manager.password.console.impl.CredentialsPresenter
import uk.co.akm.util.manager.password.io.CredentialsStoreHandle
import uk.co.akm.util.manager.password.io.findOrCreateStoreFile
import uk.co.akm.util.manager.password.model.Credentials

@Deprecated(message = "App handle for tests.")
fun main(args: Array<String>) {
    val alpha = Credentials("Alpha", mapOf(Pair("username", "john"), Pair("password", "secret")))
    val bravo = Credentials("Bravo", mapOf(Pair("username", "jack"), Pair("password", "something")))
    val charlie = Credentials("Charlie", mapOf(Pair("url", "http://www.mysite.com"), Pair("username", "jake"), Pair("password", "some word")))
    val credentials = arrayListOf(alpha, bravo, charlie)

    val dummyStoreHandle = object : CredentialsStoreHandle {
        override fun canSave(): Boolean = false

        override fun read(): Collection<Credentials> = emptyList()

        override fun save(credentials: Collection<Credentials>) {}

        override fun save(credentials: Collection<Credentials>, newPassword: String) {}

        override fun filePath(): String = ""
    }

    println("Test")
    val presenter = CredentialsPresenter(credentials, dummyStoreHandle)
    presenter.launch()
}