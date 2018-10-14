package uk.co.akm.util.manager.password.console.impl

import uk.co.akm.util.manager.password.clipboard.ClipboardService
import uk.co.akm.util.manager.password.clipboard.ClipboardServiceImpl
import uk.co.akm.util.manager.password.console.AbstractIndexedConsolePresenter
import uk.co.akm.util.manager.password.model.Credentials
import java.util.*

/**
 * Created by Thanos Mavroidis on 24/09/2018.
 */
class CredentialsPresenter(credentials: Collection<Credentials>): AbstractIndexedConsolePresenter<DisplayState>() {
    private val noIndex = -1

    private val addChar = 'a'
    private val addString = "$addChar"

    private val cancelChar = 'c'
    private val cancelString = "$cancelChar"

    private val deleteChar = 'd'
    private val deleteString = "$deleteChar"

    private val helpChar = 'h'
    private val helpString = "$helpChar"

    private val noChar = 'n'
    private val yesChar = 'y'
    private val noString = "$noChar"
    private val yesString = "$yesChar"
    private val confirmStrings = setOf(noString, yesString)

    private val helpMessage = "Example help mesage."

    private var indexedCredentials: Map<Int, Credentials>
    private var indexedSelectedItems: Map<Int, Map.Entry<String, String>>? = null

    private var newCredentialsKey: String? = null
    private var newCredentialsName: String? = null
    private var newCredentials = LinkedHashMap<String, String>()

    private val clipboardService: ClipboardService = ClipboardServiceImpl()

    private var deleteCredentialsIndex: Int? = null

    private var change = false
    private var confirmationMode = false

    init {
        indexedCredentials = indexItems(credentials)
    }

    val haveChanges: Boolean
        get() = change

    val credentials: Collection<Credentials>
        get() = indexedCredentials.values

    override val state: DisplayState
        get() = determineState()

    private fun determineState(): DisplayState {
        if (confirmationMode) {
            return DisplayState.CONFIRM
        } else if (newCredentialsName != null) {
            return DisplayState.ADD
        } else if (indexedSelectedItems != null) {
            return DisplayState.SELECTED
        } else if (deleteCredentialsIndex != null) {
            return DisplayState.DELETE
        } else {
            return DisplayState.ALL
        }
    }

    override fun back(state: DisplayState) {
        if (state == DisplayState.SELECTED) {
            show()
        }
    }

    override fun show() {
        indexedSelectedItems = null
        if (indexedCredentials.isEmpty()) {
            println("No credentials entered. Press '$addChar' to add or '$exitChar' to exit.")
        } else {
            showCredentialsList()
            println("Select existing credentials or press '$addChar' to add, '$deleteChar' to delete or '$exitChar' to exit.")
        }
    }

    override fun showSelection(state: DisplayState, selectionIndex: Int) {
        when (state) {
            DisplayState.ALL -> processCredentialsSelection(selectionIndex)
            DisplayState.SELECTED -> processCredentialsItemSelection(selectionIndex)
            DisplayState.DELETE -> processDeleteCredentialsItemSelection(selectionIndex)
        }
    }

    override fun showCommand(state: DisplayState, command: String) {
        when (state) {
            DisplayState.ALL -> enterAddOrDeleteMode(command)
            DisplayState.ADD -> processAddModeInput(command)
            DisplayState.CONFIRM -> processPositiveConfirmationEntry(command)
        }
    }

    override fun selectionIsValid(state: DisplayState, selectionIndex: Int): Boolean {
        return when (state) {
            DisplayState.ALL -> indexedCredentials.containsKey(selectionIndex)
            DisplayState.DELETE -> indexedCredentials.containsKey(selectionIndex)
            DisplayState.SELECTED -> indexedSelectedItems?.containsKey(selectionIndex) ?: false
            else -> false
        }
    }

    override fun stringCommandIsValid(state: DisplayState, command: String): Boolean {
        return when (state) {
            DisplayState.ALL -> addString.equals(command, ignoreCase = true) || deleteString.equals(command, ignoreCase = true)
            DisplayState.ADD -> true
            DisplayState.CONFIRM -> confirmStrings.contains(command.toLowerCase()) || confirmStrings.contains(command.toUpperCase())
            else -> false
        }
    }

    private fun processCredentialsSelection(selectionIndex: Int) {
        val selected = indexedCredentials[selectionIndex]
        if (selected != null) {
            indexedSelectedItems = indexItems(selected.credentials.entries)
            indexedSelectedItems?.let { showSelectedCredentials(selected.name, it) }
        }
    }

    private fun showSelectedCredentials(selectedName: String, selectedItems: Map<Int, Map.Entry<String, String>>) {
        println("Credentials for $selectedName:")
        selectedItems.forEach {  println("${it.key}) ${it.value.key}") }
        println("Select credentials item to copy to clipboard, '$backChar' to go back to display all the credentials or '$exitChar' to exit.")
    }

    private fun processCredentialsItemSelection(selectionIndex: Int) {
        indexedSelectedItems?.let {
            val itemName = it[selectionIndex]?.key
            val itemValue = it[selectionIndex]?.value
            itemValue?.let { clipboardService.store(it) }
            println("$itemName copied to the clipboard.")
        }
    }

    private fun enterAddOrDeleteMode(command: String) {
        if (addString.equals(command, ignoreCase = true)) {
            enterAddMode()
        } else if (deleteString.equals(command, ignoreCase = true)) {
            enterDeleteMode()
        }
    }

    private fun enterAddMode() {
        indexedSelectedItems = null
        newCredentials.clear()
        newCredentialsName = ""
        println("Enter the credential items in multiple lines and an empty line when you finish or enter '$cancelChar' to cancel. For an example, enter '$helpChar' or '$exitChar' to exit.")
    }

    private fun processAddModeInput(command: String) {
        if (helpString.equals(command, ignoreCase = true)) {
            println(helpMessage)
        } else {
            processCredentialsItemEntry(command)
        }
    }

    private fun processCredentialsItemEntry(line: String) {
        if (line.isEmpty() || line.isBlank()) {
            processNewCredentialsCompleteEntry()
        } else if (cancelString.equals(line, ignoreCase = true)) {
            cancelAddOrDeleteMode()
        } else {
            addCredentialsItem(line.trim())
        }
    }

    private fun processNewCredentialsCompleteEntry() {
        if (haveNewCredentialsToAdd()) {
            if (isOverwrite()) {
                enterOverwriteConfirmationMode()
            } else {
                addNewCredentialsAndExitAddMode()
            }
        } else {
            cancelAddOrDeleteMode()
        }
    }

    private fun processPositiveConfirmationEntry(command: String) {
        confirmationMode = false
        if (noString.equals(command, ignoreCase = true)) {
            cancelAddOrDeleteMode()
        } else if (yesString.equals(command, ignoreCase = true)) {
            if (deleteCredentialsIndex == null) {
                addNewCredentialsAndExitAddMode(true)
            } else {
                deleteCredentialsAndExitDeleteMode()
            }
        }
    }

    private fun addNewCredentialsAndExitAddMode(overwrite: Boolean = false) {
        change = true
        val action = if (overwrite) "overwritten" else "added"
        addNewCredentials(Credentials(newCredentialsName!!, LinkedHashMap(newCredentials)))
        exitAddOrDeleteMode("Credentials for '$newCredentialsName' have been $action.")
    }

    private fun addCredentialsItem(line: String) {
        if (newCredentialsName == "") {
            newCredentialsName = line
        } else if (newCredentialsKey == null) {
            newCredentialsKey = line
        } else {
            newCredentialsKey?.let { newCredentials[it] = line }
            newCredentialsKey = null
        }
    }

    private fun addNewCredentials(credentials: Credentials) {
        val existingCredentials = TreeSet(indexedCredentials.values)
        val overwrite = existingCredentials.firstOrNull { it.name == credentials.name }
        if (overwrite != null) {
            existingCredentials.remove(overwrite) // If we are going to overwrite any credentials then remove the old credentials for the overwrite to take effect.
        }

        existingCredentials.add(credentials)
        indexedCredentials = indexItems(existingCredentials)
    }

    private fun haveNewCredentialsToAdd(): Boolean {
        if (newCredentialsName == null || newCredentials.isEmpty()) {
            return false
        }

        return newCredentialsName?.let { it.isNotEmpty() && it.isNotBlank() } ?: false
    }

    private fun isOverwrite() = indexedCredentials.values.asSequence().map { it.name }.contains(newCredentialsName)

    private fun cancelAddOrDeleteMode() {
        val message = if (deleteCredentialsIndex == null) "No new credentials added." else "No credentials deleted."
        exitAddOrDeleteMode(message)
    }

    private fun enterOverwriteConfirmationMode() {
        confirmationMode = true
        println("Overwrite existing credentials? Enter 'y' to confirm the overwrite or 'n' to cancel.")
    }

    private fun enterDeleteMode() {
        deleteCredentialsIndex = noIndex
        showCredentialsList()
        println("Select credentials to delete.")
    }

    private fun processDeleteCredentialsItemSelection(selectionIndex: Int) {
        deleteCredentialsIndex = selectionIndex
        confirmationMode = true
        val name = indexedCredentials[selectionIndex]?.name
        println("Delete credentials for '$name'? Enter 'y' to confirm the overwrite or 'n' to cancel.")
    }

    private fun deleteCredentialsAndExitDeleteMode() {
        change = true
        deleteCredentials()
        val name = indexedCredentials[deleteCredentialsIndex]?.name
        exitAddOrDeleteMode("Credentials for '$name' have been deleted.")
    }

    private fun deleteCredentials() {
        val remaining = indexedCredentials.entries.filter { it.key != deleteCredentialsIndex }.map { it.value }
        indexedCredentials = indexItems(remaining)
    }

    private fun exitAddOrDeleteMode(message: String) {
        println(message)
        deleteCredentialsIndex = null
        newCredentials.clear()
        newCredentialsName = null
        show()
    }

    private fun <T> indexItems(items: Collection<T>): Map<Int, T> {
        val indexedItems = LinkedHashMap<Int, T>(items.size)

        var i = 1 // This is for a user display so the first index is 1 and not 0.
        items.forEach { indexedItems[i++] = it }

        return indexedItems
    }

    private fun showCredentialsList() {
        indexedCredentials.entries.forEach { println("${it.key}) ${it.value}") }
    }

    override fun onExit() {
        clipboardService.clear()
    }
}