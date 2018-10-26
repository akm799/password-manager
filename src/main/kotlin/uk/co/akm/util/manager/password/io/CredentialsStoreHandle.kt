package uk.co.akm.util.manager.password.io

import uk.co.akm.util.manager.password.model.Credentials

/**
 * Created by Thanos Mavroidis on 26/10/2018.
 */
interface CredentialsStoreHandle {

    fun canSave(): Boolean

    fun read(): Collection<Credentials>

    fun save(credentials: Collection<Credentials>)

    fun save(credentials: Collection<Credentials>, newPassword: String)

    fun filePath(): String
}