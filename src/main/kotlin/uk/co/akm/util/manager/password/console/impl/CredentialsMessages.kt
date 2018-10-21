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

val noChar = 'n'
val yesChar = 'y'
private val noString = "$noChar"
private val yesString = "$yesChar"
private val confirmStrings = setOf(noString, yesString)
private val confirmationMessagePostfix = "Enter '$yesChar' to confirm the %s or '$noChar' to cancel."

val invalidBack = "Invalid 'back' command."
val emptyCredentialsListInstruction = "No credentials entered. Press '$addChar' to add or '${exitChar}' to exit."
val credentialsListInstruction = "Select existing credentials or press '$addChar' to add, '$deleteChar' to delete or '$exitChar' to exit."
val selectedCredentialsInstruction = "Select credentials item to copy to clipboard, '${backChar}' to go back to display all the credentials or '$exitChar' to exit."
val addCredentialsInstruction = "Enter the credential items in multiple lines and an empty line when you finish or enter '$backChar' to go back and cancel the add operation. For an example, enter '$helpChar' or '$exitChar' to exit."
val addCredentialsHelpMessage = "Example help message."
val addedAction = "added"
val overwrittenAction = "overwritten"
val addActionCancellationMessage = "No new credentials added."
val deleteActionCancellationMessage = "No credentials deleted."
val deleteActionSelectionInstruction = "Select credentials to delete or enter '$backChar' to go back and cancel the delete operation."

fun selectedCredentialsHeader(selectedName: String): String = "Credentials for $selectedName:"

fun copiedToClipboardMessage(itemName: String?) = "$itemName copied to the clipboard."

fun credentialsActionConfirmationMessage(name: String, action: String) = "Credentials for '$name' have been $action."

fun confirmOverwriteAction(): String = confirmQuestionMessage("Overwrite existing credentials?", "overwrite")

fun confirmDeleteAction(name: String?): String = confirmQuestionMessage("Delete credentials for '$name'?", "deletion")

private fun confirmQuestionMessage(question: String, action: String): String = "$question ${String.format(confirmationMessagePostfix, action)}"

fun deletionConfirmationMessage(name: String?) = "Credentials for '$name' have been deleted."



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