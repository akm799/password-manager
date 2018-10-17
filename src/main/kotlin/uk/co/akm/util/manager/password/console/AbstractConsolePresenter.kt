package uk.co.akm.util.manager.password.console

import uk.co.akm.util.manager.password.io.readInputLine


/**
 * Created by Thanos Mavroidis on 25/09/2018.
 */
abstract class AbstractConsolePresenter<S> {

    abstract val state: S

    /**
     * Launches the console presenter.
     */
    fun launch() {
        show()
        loop()
    }

    private fun loop() {
        var listen = true

        while (listen) {
            val command = readInputLine()
            if (isExitCommand(command)) {
                listen = false
            } else if (isBackCommand(command)) {
                back(state)
            } else if (commandIsValid(state, command)) {
                show(state, command)
            } else {
                System.err.println(invalid(command))
            }
        }

        onExit()
    }

    /**
     * Shows the initial view.
     */
    abstract fun show()

    /**
     * Shows the view that results from the issuing of the command [command], issued when the state is [state].
     */
    abstract fun show(state: S, command: String)

    /**
     * Shows the view when the user issues a 'back' command when the state is [state].
     */
    abstract fun back(state: S)

    /**
     * Returns true if the user issued command [command] when the state is [state] is a valid command, or false otherwise.
     */
    abstract fun commandIsValid(state: S, command: String): Boolean

    /**
     * Implementations of this method should perform any clean-up operations just before we exit.
     */
    abstract fun onExit()
}