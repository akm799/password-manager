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
    private val addChar = 'a'
    private val addString = "$addChar"

    private val cancelChar = 'c'
    private val cancelString = "$cancelChar"

    private var indexedCredentials: Map<Int, Credentials>
    private var indexedSelectedItems: Map<Int, Map.Entry<String, String>>? = null

    private var newCredentialsKey: String? = null
    private var newCredentialsName: String? = null
    private var newCredentials = LinkedHashMap<String, String>()

    private val clipboardService: ClipboardService = ClipboardServiceImpl()

    private var change = false

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
        if (newCredentialsName != null) {
            return DisplayState.ADD
        } else if (indexedSelectedItems != null) {
            return DisplayState.SELECTED
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
            indexedCredentials.entries.forEach { println("${it.key}) ${it.value}") }
            println("Select existing credentials or press '$addChar' to add or '$exitChar' to exit.")
        }
    }

    override fun showSelection(state: DisplayState, selectionIndex: Int) {
        when (state) {
            DisplayState.ALL -> processCredentialsSelection(selectionIndex)
            DisplayState.SELECTED -> processCredentialsItemSelection(selectionIndex)
        }
    }

    override fun showCommand(state: DisplayState, command: String) {
        when (state) {
            DisplayState.ALL -> enterAddMode()
            DisplayState.ADD -> processCredentialsItemEntry(command)
        }
    }

    override fun selectionIsValid(state: DisplayState, selectionIndex: Int): Boolean {
        return when (state) {
            DisplayState.ALL -> indexedCredentials.containsKey(selectionIndex)
            DisplayState.SELECTED -> indexedSelectedItems?.containsKey(selectionIndex) ?: false
            else -> false
        }
    }

    override fun stringCommandIsValid(state: DisplayState, command: String): Boolean {
        return when (state) {
            DisplayState.ALL -> addString.equals(command, ignoreCase = true)
            DisplayState.ADD -> true
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
        println("Credentials for $selectedName")
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

    private fun <T> indexItems(items: Collection<T>): Map<Int, T> {
        val indexedItems = LinkedHashMap<Int, T>(items.size)

        var i = 1 // This is for a user display so the first index is 1 and not 0.
        items.forEach { indexedItems[i++] = it }

        return indexedItems
    }

    private fun enterAddMode() {
        indexedSelectedItems = null
        newCredentials.clear()
        newCredentialsName = ""
        println("Enter the credential items in multiple lines and an empty line when you finish or enter '$cancelChar' to cancel.")
    }

    private fun processCredentialsItemEntry(line: String) {
        if (line.isEmpty() || line.isBlank()) {
            processNewCredentialsCompleteEntry()
        } else if (cancelString.equals(line, ignoreCase = true)) {
            cancelAddMode()
        } else {
            addCredentialsItem(line.trim())
        }
    }

    private fun processNewCredentialsCompleteEntry() {
        if (haveNewCredentialsToAdd()) {
            change = true
            addNewCredentials(Credentials(newCredentialsName!!, LinkedHashMap(newCredentials)))
            exitAddMode("Credentials for '$newCredentialsName' have been added.")
        } else {
            cancelAddMode()
        }
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
        existingCredentials.add(credentials)

        indexedCredentials = indexItems(existingCredentials)
    }

    private fun haveNewCredentialsToAdd(): Boolean {
        if (newCredentialsName == null || newCredentials.isEmpty()) {
            return false
        }

        return newCredentialsName?.let { it.isNotEmpty() && it.isNotBlank() } ?: false
    }

    private fun cancelAddMode() {
        exitAddMode("No new credentials added.")
    }

    private fun exitAddMode(message: String) {
        println(message)
        newCredentials.clear()
        newCredentialsName = null
        show()
    }

    override fun onExit() {
        clipboardService.clear()
    }
}