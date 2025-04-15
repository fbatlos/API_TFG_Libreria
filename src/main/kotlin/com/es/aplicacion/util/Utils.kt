package com.es.aplicacion.util

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.model.Libro

object Utils {
    fun ValidaEmail(email: String): Boolean{
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        return emailRegex.matches(email)

    }

    fun validacionesLibroSimples(libro: Libro){
        if (libro.precio!! <= 0.0 ){
            BadRequest("El precio no puede ser igual o inferior a 0.")
        }

        if (libro.categorias.isEmpty()){
            BadRequest("El libro requiere de una categoria.")
        }
    }
}