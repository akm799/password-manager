package uk.co.akm.util.manager.password.model


/**
 * Created by Thanos Mavroidis on 06/10/2018.
 */
class EncryptedData(val ivBytes: ByteArray, val encLen: Int, val encryptedBytes: ByteArray)