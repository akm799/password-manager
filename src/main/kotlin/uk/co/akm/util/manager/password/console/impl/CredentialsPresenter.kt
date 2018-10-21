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

    private var indexedCredentials: Map<Int, Credentials>
    private var indexedSelectedItems: Map<Int, Map.Entry<String, String>>? = null

    private var deleteCredentialsIndex: Int? = null
    private var newCredentialsData: NewCredentialsData? = null
    private var changePasswordData: ChangePasswordData? = null

    private var change = false
    private var confirmationMode = false

    private val clipboardService: ClipboardService = ClipboardServiceImpl()

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
        } else if (newCredentialsData != null) {
            return DisplayState.ADD
        } else if (indexedSelectedItems != null) {
            return DisplayState.SELECTED
        } else if (deleteCredentialsIndex != null) {
            return DisplayState.DELETE
        } else if (changePasswordData != null) {
            return DisplayState.PASSWORD
        } else {
            return DisplayState.ALL
        }
    }

    override fun back(state: DisplayState) {
        when (state) {
            DisplayState.SELECTED -> { show() }
            DisplayState.ADD, DisplayState.DELETE, DisplayState.PASSWORD -> { cancelAddOrDeleteOrPasswordMode() }
            else -> { println(invalidBack) }
        }
    }

    override fun show() {
        indexedSelectedItems = null
        if (indexedCredentials.isEmpty()) {
            println(emptyCredentialsListInstruction)
        } else {
            showCredentialsList()
            println(credentialsListInstruction)
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
            DisplayState.ALL -> enterAddOrDeleteOrPasswordMode(command)
            DisplayState.ADD -> processAddModeInput(command)
            DisplayState.PASSWORD -> processChangePasswordModeInput(command)
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
            DisplayState.ALL -> isAddCommand(command) || isDeleteCommand(command) || isPasswordCommand(command)
            DisplayState.ADD, DisplayState.PASSWORD -> true
            DisplayState.CONFIRM -> isConfirmCommand(command)
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
        println(selectedCredentialsHeader(selectedName))
        selectedItems.forEach {  println("${it.key}) ${it.value.key}") }
        println(selectedCredentialsInstruction)
    }

    private fun processCredentialsItemSelection(selectionIndex: Int) {
        indexedSelectedItems?.let {
            val itemName = it[selectionIndex]?.key
            val itemValue = it[selectionIndex]?.value
            itemValue?.let { clipboardService.store(it) }
            println(copiedToClipboardMessage(itemName))
        }
    }

    private fun enterAddOrDeleteOrPasswordMode(command: String) {
        if (isAddCommand(command)) {
            enterAddMode()
        } else if (isDeleteCommand(command)) {
            enterDeleteMode()
        } else if (isPasswordCommand(command)) {
            enterChangePasswordMode()
        }
    }

    private fun enterAddMode() {
        indexedSelectedItems = null
        deleteCredentialsIndex = null
        newCredentialsData = NewCredentialsData()
        println(addCredentialsInstruction)
    }

    private fun processAddModeInput(command: String) {
        if (isHelpCommand(command)) {
            println(addCredentialsHelpMessage)
        } else {
            processCredentialsItemEntry(command)
        }
    }

    private fun processCredentialsItemEntry(line: String) {
        if (line.isEmpty() || line.isBlank()) {
            processNewCredentialsCompleteEntry()
        } else {
            newCredentialsData?.addCredentialsItem(line.trim())
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
            cancelAddOrDeleteOrPasswordMode()
        }
    }

    private fun processPositiveConfirmationEntry(command: String) {
        confirmationMode = false
        if (isNoCommand(command)) {
            cancelAddOrDeleteOrPasswordMode()
        } else if (isYesCommand(command)) {
            if (deleteCredentialsIndex == null) {
                addNewCredentialsAndExitAddMode(true)
            } else {
                deleteCredentialsAndExitDeleteMode()
            }
        }
    }

    private fun addNewCredentialsAndExitAddMode(overwrite: Boolean = false) {
        newCredentialsData?.let {
            change = true
            val action = if (overwrite) overwrittenAction else addedAction
            val credentialsToAdd = it.credentials
            addNewCredentials(credentialsToAdd)
            exitAddOrDeleteOrPasswordMode(credentialsActionConfirmationMessage(credentialsToAdd.credentials.toString(), action))
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

    private fun haveNewCredentialsToAdd(): Boolean = newCredentialsData?.haveNewCredentials() ?: false

    private fun isOverwrite(): Boolean {
        return newCredentialsData?.let { newCredentials ->
            indexedCredentials.values.asSequence().map { it.name }.contains(newCredentials.name)
        } ?: false
    }

    private fun cancelAddOrDeleteOrPasswordMode() {
        val state = determineState()
        val message = when (state) {
            DisplayState.ADD -> addActionCancellationMessage
            DisplayState.DELETE -> deleteActionCancellationMessage
            DisplayState.PASSWORD -> changePasswordActionCancellationMessage
            else -> ""
        }

        exitAddOrDeleteOrPasswordMode(message)
    }

    private fun enterOverwriteConfirmationMode() {
        confirmationMode = true
        println(confirmOverwriteAction())
    }

    private fun enterDeleteMode() {
        indexedSelectedItems = null
        deleteCredentialsIndex = noIndex
        showCredentialsList()
        println(deleteActionSelectionInstruction)
    }

    private fun processDeleteCredentialsItemSelection(selectionIndex: Int) {
        deleteCredentialsIndex = selectionIndex
        confirmationMode = true
        val name = indexedCredentials[selectionIndex]?.name
        println(confirmDeleteAction(name))
    }

    private fun deleteCredentialsAndExitDeleteMode() {
        change = true
        deleteCredentials()
        val name = indexedCredentials[deleteCredentialsIndex]?.name
        exitAddOrDeleteOrPasswordMode(deletionConfirmationMessage(name))
    }

    private fun deleteCredentials() {
        val remaining = indexedCredentials.entries.filter { it.key != deleteCredentialsIndex }.map { it.value }
        indexedCredentials = indexItems(remaining)
    }

    private fun enterChangePasswordMode() {
        indexedSelectedItems = null
        newCredentialsData = null
        deleteCredentialsIndex = null
        changePasswordData = ChangePasswordData()
        println(newPasswordInstruction)
    }

    private fun processChangePasswordModeInput(command: String) {
        changePasswordData?.setNewPassword(command)

        if (changePasswordData?.haveBothPasswords() ?: false) {
            if (changePasswordData?.passwordConfirmed() ?: false) {
                changePasswordData?.let { changePassword(it.password) }
                exitAddOrDeleteOrPasswordMode(passwordChangedMessage)
            } else {
                println(passwordMismatchMessage)
                println(newPasswordInstruction)
            }

            changePasswordData?.clear()
        } else {
            println(confirmNewPasswordInstruction)
        }
    }

    private fun changePassword(password: String) {
        //TODO
        println("TODO Change the pass-phrase.")
    }

    private fun exitAddOrDeleteOrPasswordMode(message: String) {
        println(message)
        newCredentialsData = null
        deleteCredentialsIndex = null
        changePasswordData = null
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