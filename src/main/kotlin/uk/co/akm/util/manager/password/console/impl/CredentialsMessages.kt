package uk.co.akm.util.manager.password.console.impl

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

val helpMessage = "Example help mesage."

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