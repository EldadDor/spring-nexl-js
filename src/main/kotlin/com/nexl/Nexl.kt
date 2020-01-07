/*
 * User: eldad
 */
package com.nexl

import java.util.*

class Nexl<T> {
    var name: String? = null
    var url: String? = null
    var resource: T? = null
    private val nexls = ArrayList<Nexl<Any>>()

    // mmm, there is no setter method for the 'nexls'
    fun addNexl(nexl: Nexl<Any>) {
        this.nexls.add(nexl)
    }

    fun getNexls(): List<Nexl<Any>> {
        return nexls
    }
}