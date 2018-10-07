package uk.co.akm.util.manager.password.crypto

import org.junit.Assert
import org.junit.Test

/**
 * Created by Thanos Mavroidis on 29/09/2018.
 */
class CryptoServiceTest {

    @Test
    fun shouldEncryptAndDecrypt() {
        val password = "top_secret"
        val plaintext = "some secret multi-line message\nwith many lines\nnumbers (1,2,3)\nand some other symbols like % £ * ! ~\n and, of course, normal characters too."

        val encryptionTestInstance = CryptoService.aesGcmInstance(password)
        val ciphertextBytes = encryptionTestInstance.encrypt(plaintext.toByteArray())
        assertVeryDifferent(plaintext.toByteArray(), ciphertextBytes)

        val decryptionTestInstance = CryptoService.aesGcmInstance(password)
        val decryptedBytes = decryptionTestInstance.decrypt(ciphertextBytes)
        val decryptedText = String(decryptedBytes)
        Assert.assertEquals(plaintext, decryptedText)
    }

    private fun assertVeryDifferent(expected: ByteArray, actual: ByteArray) {
        assertDifferent(expected, actual, 0.9f)
    }

    private fun assertDifferent(expected: ByteArray, actual: ByteArray, limitFraction: Float) {
        val len = Math.min(expected.size, actual.size)

        var nDiff = 0
        for (i in 0 until len) {
            nDiff += if (expected[i] == actual[i]) 0 else 1
        }

        Assert.assertTrue(nDiff/len.toFloat() > limitFraction)
    }
}