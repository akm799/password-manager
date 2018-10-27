package uk.co.akm.util.manager.password.console.impl

import uk.co.akm.util.manager.password.console.backChar
import uk.co.akm.util.manager.password.console.exitChar
import uk.co.akm.util.manager.password.console.isCommand
import java.util.*

/**
 * Created by Thanos Mavroidis on 14/10/2018.
 */

val addChar = 'a'
private val addString = "$addChar"

val deleteChar = 'd'
private val deleteString = "$deleteChar"

val helpChar = 'h'
private val helpString = "$helpChar"

val passwordChar = 'p'
private val passwordString = "$passwordChar"

val noChar = 'n'
val yesChar = 'y'
private val noString = "$noChar"
private val yesString = "$yesChar"
private val confirmStrings = setOf(noString, yesString)
private val confirmationMessagePostfix = "Enter '$yesChar' to confirm the %s or '$noChar' to cancel."

val invalidBack = "Invalid 'back' command."
val emptyCredentialsListInstruction = "No credentials entered. Press '$addChar' to add or '${exitChar}' to exit."
val credentialsListInstruction = "Select existing credentials or press '$addChar' to add new credentials, '$deleteChar' to delete existing credentials, '$passwordChar' to change the password or '$exitChar' to exit."
val selectedCredentialsInstruction = "Select credentials item to copy to clipboard, '${backChar}' to go back to display all the credentials or '$exitChar' to exit."
val addCredentialsInstruction = "Enter the credential items in multiple lines and an empty line when you finish or enter '$backChar' to go back and cancel the add operation. For an example, enter '$helpChar' or '$exitChar' to exit."
val addedAction = "added"
val overwrittenAction = "overwritten"
val addActionCancellationMessage = "No new credentials added."
val deleteActionCancellationMessage = "No credentials deleted."
val changePasswordActionCancellationMessage = "No password changed."
val deleteActionSelectionInstruction = "Select credentials to delete or enter '$backChar' to go back and cancel the delete operation."
val newPasswordInstruction = "Please enter the new pass-phrase or '$backChar' to go back and cancel the change password operation."
val confirmNewPasswordInstruction = "Please re-enter the new pass-phrase for confirmation or '$backChar' to go back and cancel the change password operation."
val passwordMismatchMessage = "The pass-phrases entered do not match. The pass-phrase cannot be changed."
val passwordChangedMessage = "The pass-phrase has been changed."

fun selectedCredentialsHeader(selectedName: String): String = "Credentials for $selectedName:"

fun copiedToClipboardMessage(itemName: String?) = "$itemName copied to the clipboard."

fun credentialsActionConfirmationMessage(name: String, action: String) = "Credentials for '$name' have been $action."

fun confirmOverwriteAction(): String = confirmQuestionMessage("Overwrite existing credentials?", "overwrite")

fun confirmDeleteAction(name: String?): String = confirmQuestionMessage("Delete credentials for '$name'?", "deletion")

private fun confirmQuestionMessage(question: String, action: String): String = "$question ${String.format(confirmationMessagePostfix, action)}"

fun deletionConfirmationMessage(name: String?) = "Credentials for '$name' have been deleted."

fun noModificationsAllowed(filePath: String): String = "Unable to make any modifications because the file '$filePath' is in read-only mode. Please enable write mode on that file before attempting any modifications."



fun isAddCommand(command: String) = isCommand(addString, command)

fun isDeleteCommand(command: String) = isCommand(deleteString, command)

fun isHelpCommand(command: String) = isCommand(helpString, command)

fun isNoCommand(command: String) = isCommand(noString, command)

fun isYesCommand(command: String) = isCommand(yesString, command)

fun isConfirmCommand(command: String) : Boolean {
    val lowerCase = command.toLowerCase(Locale.ENGLISH)
    val upperCase = command.toUpperCase(Locale.ENGLISH)

    return confirmStrings.contains(lowerCase) || confirmStrings.contains(upperCase)
}

fun isPasswordCommand(command: String): Boolean = isCommand(passwordString, command)

val addCredentialsHelpMessage = """Example for adding credentials:
If you have a Gmail email which is 'jsmith@gmail.com' and your password is 'very-secret' then to add these credentials, you enter them as follows:
Gmail
login name
jsmith
password
very-secret

The last blank line indicates that all credentials have been entered. After that, a new credentials item, called 'Gmail' will appear in the credentials list. If you select it, then it will show as:
Credentials for Gmail:
1) login name
2) password"""
