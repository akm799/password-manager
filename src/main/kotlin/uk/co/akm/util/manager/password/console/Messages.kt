package uk.co.akm.util.manager.password.console

/**
 * Created by Thanos Mavroidis on 17/10/2018.
 */

val backChar = 'b'
private val backString = "$backChar"

val exitChar = 'e'
private val exitString = "$exitChar"

private val invalidInput = "Invalid input: '%s'."

fun isBackCommand(command: String): Boolean = isCommand(backString, command)

fun isExitCommand(command: String): Boolean = isCommand(exitString, command)

fun isCommand(testCommand: String, actualCommand: String): Boolean = testCommand.equals(actualCommand, ignoreCase = true)

fun invalid(command: String): String = String.format(invalidInput, command)