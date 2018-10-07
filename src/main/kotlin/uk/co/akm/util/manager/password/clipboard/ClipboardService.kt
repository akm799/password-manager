package uk.co.akm.util.manager.password.clipboard

/**
 * Created by Thanos Mavroidis on 26/09/2018.
 */
interface ClipboardService {

    fun store(text: String)

    fun clear()
}