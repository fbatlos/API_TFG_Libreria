package com.es.aplicacion.util

object Utils {
    fun ValidaEmail(email: String): Boolean{
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        return emailRegex.matches(email)

    }
}