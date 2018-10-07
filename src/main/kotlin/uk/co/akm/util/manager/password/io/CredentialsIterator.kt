package uk.co.akm.util.manager.password.io

import uk.co.akm.util.manager.password.model.Credentials
import java.util.*

/**
 * Created by Thanos Mavroidis on 24/09/2018.
 */
class CredentialsIterator : Iterator<Credentials> {
    private val lines: Iterator<String>

    constructor(lines: Collection<String>) {
        this.lines = lines.iterator()
    }

    override fun hasNext(): Boolean = lines.hasNext()

    override fun next(): Credentials {
        val name = lines.next()
        val numberOfEntries = lines.next().toInt()
        val entries = readCredentialsItem(numberOfEntries)

        return Credentials(name, entries)
    }

    private fun readCredentialsItem(number: Int): Map<String, String> {
        val credentials = LinkedHashMap<String, String>(number)
        for (i in 1..number) {
            credentials.put(lines.next(), lines.next())
        }

        return credentials;
    }
}