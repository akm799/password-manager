package uk.co.akm.util.manager.password.console

/**
 * Created by Thanos Mavroidis on 25/09/2018.
 */
abstract class AbstractIndexedConsolePresenter<S> : AbstractConsolePresenter<S>() {

    override fun show(state: S, command: String) {
        val index = toIndex(command)

        if (index == null) {
            showCommand(state, command)
        } else {
            showSelection(state, index)
        }
    }

    override fun commandIsValid(state: S, command: String): Boolean {
        val index = toIndex(command)

        return if (index == null) stringCommandIsValid(state, command) else selectionIsValid(state, index)
    }

    abstract fun selectionIsValid(state: S, selectionIndex: Int): Boolean

    abstract fun stringCommandIsValid(state: S, command: String): Boolean

    abstract fun showSelection(state: S, selectionIndex: Int)

    abstract fun showCommand(state: S, command: String)

    private fun toIndex(command: String): Int? {
        try {
            return command.toInt()
        } catch (nfe: NumberFormatException) {
            return null
        }
    }
}