package uk.co.akm.util.manager.password.console.impl

/**
 * Created by Thanos Mavroidis on 21/10/2018.
 */
class ChangePasswordData {
    private var newPassword: String? = null
    private var newPasswordConfirmation: String? = null

    val password: String
        get() {
            if (haveBothPasswords() && passwordConfirmed()) {
                return newPassword ?: throw IllegalAccessException("New password has not been set.")
            } else {
                throw IllegalAccessException("New password has not been set and confirmed.")
            }
        }

    fun setNewPassword(password: String) {
        if (newPassword == null) {
            newPassword = password
        } else {
            newPasswordConfirmation = password
        }
    }

    fun haveBothPasswords(): Boolean = (newPassword != null && newPasswordConfirmation != null)

    fun passwordConfirmed(): Boolean = (newPassword == newPasswordConfirmation)

    fun clear() {
        newPassword = null
        newPasswordConfirmation = null
    }
}