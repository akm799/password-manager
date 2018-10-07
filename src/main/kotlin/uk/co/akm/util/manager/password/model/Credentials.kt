package uk.co.akm.util.manager.password.model

/**
 * Created by Thanos Mavroidis on 24/09/2018.
 */
data class Credentials(val name: String, val credentials: Map<String, String>): Comparable<Credentials> {

    /**
     * To short credentials according in alphabetical order of the name.
     */
    override fun compareTo(other: Credentials): Int = name.compareTo(other.name)

    override fun toString(): String = name
}