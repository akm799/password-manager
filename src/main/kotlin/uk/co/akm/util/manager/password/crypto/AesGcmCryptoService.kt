package uk.co.akm.util.manager.password.crypto

import uk.co.akm.util.manager.password.extensions.readEncryptedData
import uk.co.akm.util.manager.password.extensions.writeEncryptedData
import uk.co.akm.util.manager.password.model.EncryptedData
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.Key
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Useful links:
 *
 * https://stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
 *
 * https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 *
 * https://stackoverflow.com/questions/18228579/how-to-create-a-secure-random-aes-key-in-java
 *
 * Created by Thanos Mavroidis on 28/09/2018.
 */
class AesGcmCryptoService(password: String) : CryptoService {
    private val algorithm = "AES"
    private val gcmIvAuthTagLen = 128
    private val salt = byteArrayOf(-51, 126, 114, -19, 21, 92, 52, -89, 53, -44, 93, 47, 36, 12, 44, 37)
    private val random = SecureRandom.getInstance("SHA1PRNG")
    private val cipher = Cipher.getInstance("$algorithm/GCM/NoPadding")
    private val keyBytes = buildKeyBytes(password)

    private fun buildKeyBytes(password: String): ByteArray {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 128)

        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec).encoded
    }

    override fun encrypt(plaintext: ByteArray): ByteArray {
        val ivBytes = ByteArray(cipher.blockSize)
        random.nextBytes(ivBytes)

        val specs = buildKeyAndIvSpecs(keyBytes, ivBytes)

        cipher.init(Cipher.ENCRYPT_MODE, specs.first, specs.second);
        val encrypted = ByteArray(cipher.getOutputSize(plaintext.size))
        var encLen = cipher.update(plaintext, 0, plaintext.size, encrypted, 0)
        encLen += cipher.doFinal(encrypted, encLen)

        return writeToByteArray(ivBytes, encLen, encrypted)
    }

    private fun writeToByteArray(ivBytes: ByteArray, encLen: Int, encrypted: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream()
        DataOutputStream(bos).writeEncryptedData(EncryptedData(ivBytes, encLen, encrypted))

        return bos.toByteArray()
    }

    override fun decrypt(ciphertext: ByteArray): ByteArray {
        val dis = DataInputStream(ByteArrayInputStream(ciphertext))
        val encryptedData = dis.readEncryptedData()

        val specs = buildKeyAndIvSpecs(keyBytes, encryptedData.ivBytes)

        cipher.init(Cipher.DECRYPT_MODE, specs.first, specs.second)
        val decrypted = ByteArray(cipher.getOutputSize(encryptedData.encLen))
        var decLen = cipher.update(encryptedData.encryptedBytes, 0, encryptedData.encLen, decrypted, 0)
        decLen += cipher.doFinal(decrypted, decLen)

        return decrypted
    }

    private fun buildKeyAndIvSpecs(keyBytes: ByteArray, ivBytes: ByteArray): Pair<Key, AlgorithmParameterSpec> {
        val key = SecretKeySpec(keyBytes, algorithm)
        val iv = GCMParameterSpec(gcmIvAuthTagLen, ivBytes)

        return Pair(key, iv)
    }
}