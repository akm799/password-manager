package uk.co.akm.util.manager.password.io

import uk.co.akm.util.manager.password.crypto.CryptoService
import uk.co.akm.util.manager.password.model.Credentials
import java.io.File

/**
 * Created by Thanos Mavroidis on 06/10/2018.
 */
interface CredentialsStore {

    companion object {
        fun encryptedInstance(cryptoService: CryptoService): CredentialsStore = EncryptedCredentialsStore(cryptoService)
    }

    fun read(file: File): Collection<Credentials>

    fun write(credentials: Collection<Credentials>, file: File)
}