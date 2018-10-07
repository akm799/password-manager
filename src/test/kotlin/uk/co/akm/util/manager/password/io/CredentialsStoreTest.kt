package uk.co.akm.util.manager.password.io

import org.junit.After
import org.junit.Assert
import org.junit.Test
import uk.co.akm.util.manager.password.crypto.CryptoService
import uk.co.akm.util.manager.password.model.Credentials
import java.io.File

/**
 * Created by Thanos Mavroidis on 24/09/2018.
 */
class CredentialsStoreTest {
    // The tests here do not include encryption so we use this dummy crypto-service.
    private val echoCryptoService = object : CryptoService {
        override fun encrypt(plaintext: ByteArray): ByteArray = plaintext
        override fun decrypt(ciphertext: ByteArray): ByteArray = ciphertext
    }

    private val file = File("./src/test/resources/credentials.dat")
    private val underTest = CredentialsStore.encryptedInstance(echoCryptoService)

    @Test
    fun shouldReadEmptyCredentialsList() {
        Assert.assertFalse(file.exists())
        file.createNewFile()
        Assert.assertTrue(file.exists())

        val credentials = underTest.read(file)
        Assert.assertNotNull(credentials)
        Assert.assertTrue(credentials.isEmpty())

    }

    @Test
    fun shouldWriteAndReadCredentials() {
        val alpha = Credentials("Alpha", mapOf(Pair("username", "john"), Pair("password", "secret")))
        val bravo = Credentials("Bravo", mapOf(Pair("username", "jack"), Pair("password", "something")))
        val charlie = Credentials("Charlie", mapOf(Pair("url", "http://www.mysite.com"), Pair("username", "jake"), Pair("password", "some word")))
        val credentials = arrayListOf(charlie, bravo, alpha)

        Assert.assertFalse(file.exists())

        underTest.write(credentials, file)
        Assert.assertTrue(file.exists())
        Assert.assertTrue(file.length() > 0)

        val expected = arrayListOf(alpha, bravo, charlie)
        val actual = underTest.read(file)
        assertEquals(expected, actual)
    }

    private fun assertEquals(expected: Collection<Credentials>, actual: Collection<Credentials>) {
        Assert.assertEquals(expected.size, actual.size)
        val iterator = actual.iterator()
        expected.forEach { Assert.assertEquals(it, iterator.next()) }
    }

    @After
    fun tearDown() {
        file.delete()
    }
}