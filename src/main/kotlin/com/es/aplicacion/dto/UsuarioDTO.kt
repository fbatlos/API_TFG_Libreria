package com.es.aplicacion.dto

import com.es.aplicacion.model.Direccion
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO con los datos completos del usuario")
data class UsuarioDTO(
    @Schema(description = "Nombre de usuario", example = "usuario123")
    val username: String,

    @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com")
    val email: String,

    @Schema(description = "Lista de direcciones del usuario")
    var direccion: MutableList<Direccion> = mutableListOf(),

    @Schema(description = "Lista de IDs de libros favoritos")
    val librosfav: MutableList<String> = mutableListOf(),

    @Schema(description = "ID del avatar asociado al usuario", example = "68402c80be43b505fe8a2c78")
    val avatar: String,

    @Schema(description = "Rol del usuario", example = "USER")
    val rol: String?
)

