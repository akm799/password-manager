package uk.co.akm.util.manager.password.io

import uk.co.akm.util.manager.password.crypto.CryptoService
import uk.co.akm.util.manager.password.model.Credentials
import java.io.File

/**
 * Created by Thanos Mavroidis on 26/10/2018.
 */
class CredentialsStoreHandleImpl(password: String, private val file: File) : CredentialsStoreHandle {
    private var credentialsStore: CredentialsStore = buildCredentialsStore(password)

    override fun canSave(): Boolean = file.canWrite()

    override fun read(): Collection<Credentials> = credentialsStore.read(file)

    override fun save(credentials: Collection<Credentials>) {
        credentialsStore.write(credentials, file)
    }

    override fun save(credentials: Collection<Credentials>, newPassword: String) {
        credentialsStore = buildCredentialsStore(newPassword)
        credentialsStore.write(credentials, file)
    }

    private fun buildCredentialsStore(password: String): CredentialsStore {
        val cryptoService = CryptoService.aesGcmInstance(password)

        return CredentialsStore.encryptedInstance(cryptoService)
    }

    override fun filePath(): String = file.absolutePath
}