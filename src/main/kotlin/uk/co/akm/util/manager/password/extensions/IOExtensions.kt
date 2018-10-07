package uk.co.akm.util.manager.password.extensions

import uk.co.akm.util.manager.password.model.EncryptedData
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Created by Thanos Mavroidis on 06/10/2018.
 */

fun DataOutputStream.writeEncryptedData(data: EncryptedData) {
    writeInt(data.ivBytes.size)
    write(data.ivBytes, 0, data.ivBytes.size)
    writeInt(data.encLen)
    writeInt(data.encryptedBytes.size)
    write(data.encryptedBytes, 0, data.encryptedBytes.size)
}

fun DataInputStream.readEncryptedData(): EncryptedData {
    val ivBytes = ByteArray(readInt())
    read(ivBytes)

    val encLen = readInt()
    val encryptedBytes = ByteArray(readInt())
    read(encryptedBytes)

    return EncryptedData(ivBytes, encLen, encryptedBytes)
}
