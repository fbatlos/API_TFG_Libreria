package com.es.aplicacion.dto

import com.es.aplicacion.model.Direccion

data class UsuarioDTO(
    val username: String,
    val email: String,
    var direccion: MutableList<Direccion> = mutableListOf(),
    val librosfav: MutableList<String> = mutableListOf(),
    val avatar:String,
    val rol: String?
)
