package uk.co.akm.util.manager.password.console.impl

import uk.co.akm.util.manager.password.model.Credentials
import java.util.LinkedHashMap

/**
 * Created by Thanos Mavroidis on 21/10/2018.
 */
class NewCredentialsData {
    private var newCredentialsName: String? = null
    private var newCredentialsKey: String? = null
    private var newCredentials = LinkedHashMap<String, String>()

    val name: String
        get() = newCredentialsName ?: throw IllegalAccessException("No name defined in the new credentials data. Please ensure that the 'haveNewCredentials(): Boolean' returns true before accessing this property.")

    val credentials: Credentials
        get() = Credentials(name, newCredentials)

    fun addCredentialsItem(line: String) {
        if (newCredentialsName == null) {
            newCredentialsName = line
        } else if (newCredentialsKey == null) {
            newCredentialsKey = line
        } else {
            newCredentialsKey?.let { newCredentials[it] = line }
            newCredentialsKey = null
        }
    }

    fun haveNewCredentials(): Boolean = (newCredentialsName != null && newCredentials.isNotEmpty())
}