package uk.co.akm.util.manager.password.io

import uk.co.akm.util.manager.password.crypto.CryptoService
import uk.co.akm.util.manager.password.exceptions.CredentialsParseException
import uk.co.akm.util.manager.password.model.Credentials
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.stream.Collectors
import javax.crypto.BadPaddingException

/**
 * Created by Thanos Mavroidis on 06/10/2018.
 */
class EncryptedCredentialsStore(private val cryptoService: CryptoService) : CredentialsStore {
    private val minLines = 4 // Minimum is a single entry (1 name line + 1 number of entries line + 2 items in first entry).
    private val utf8 = Charset.forName("UTF8")

    override fun read(file: File): Collection<Credentials> {
        if (file.length() == 0L) {
            return ArrayList<Credentials>(0)
        }

        val encryptedBytes = file.readBytes()

        try {
            val decryptedBytes = cryptoService.decrypt(encryptedBytes)

            return parseBytes(decryptedBytes)
        } catch (bpe: BadPaddingException) { // Thrown when encryption fails due to incorret password.
            throw CredentialsParseException()
        }
    }

    private fun parseBytes(bytes: ByteArray): Collection<Credentials> {
        BufferedReader(InputStreamReader(ByteArrayInputStream(bytes))).use { br ->
            val lines = br.lines().collect(Collectors.toList())
            checkLines(lines)

            return parseLines(lines)
        }
    }

    private fun checkLines(lines: Collection<String>) {
        if (lines.size < minLines) {
            throw CredentialsParseException()
        }

        val iterator = lines.iterator()
        val line1 = iterator.next().trim() // Name of first entry.
        val line2 = iterator.next().trim() // Number of credential items in first entry.

        if (line1.isEmpty()) {
            throw CredentialsParseException()
        }

        try {
            if (line2.toInt() <= 0) { // There must be at least one item in the first credentials entry.
                throw CredentialsParseException()
            }
        } catch (nfe: NumberFormatException) {
            throw CredentialsParseException()
        }
    }

    private fun parseLines(lines: Collection<String>): Collection<Credentials> {
        val credentials = TreeSet<Credentials>()
        CredentialsIterator(lines).forEach { credentials.add(it) }

        return credentials
    }

    override fun write(credentials: Collection<Credentials>, file: File) {
        if (credentials.isNotEmpty()) {
            val content = toLines(credentials).joinToString(separator = "\n")
            val bytes = content.toByteArray(utf8)
            val encryptedBytes = cryptoService.encrypt(bytes)

            file.writeBytes(encryptedBytes)
        }
    }

    private fun toLines(credentials: Collection<Credentials>): Collection<String> {
        val lines = ArrayList<String>()
        credentials.forEach { addLines(it, lines) }

        return lines;
    }

    private fun addLines(credentials: Credentials, lines: MutableCollection<String>) {
        lines.add(credentials.name)
        lines.add(credentials.credentials.size.toString())
        credentials.credentials.entries.forEach {
            lines.add(it.key)
            lines.add(it.value)
        }
    }
}