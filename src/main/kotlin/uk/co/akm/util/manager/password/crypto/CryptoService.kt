package uk.co.akm.util.manager.password.crypto

/**
 * Created by Thanos Mavroidis on 28/09/2018.
 */
interface CryptoService {

    companion object {
        fun aesGcmInstance(password: String): CryptoService = AesGcmCryptoService(password)
    }

    fun encrypt(plaintext: ByteArray): ByteArray

    fun decrypt(ciphertext: ByteArray): ByteArray
}