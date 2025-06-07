package com.es.aplicacion.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para el registro de un nuevo usuario")
data class UsuarioRegisterDTO(
    @Schema(description = "Nombre de usuario", example = "nuevoUsuario")
    val username: String,

    @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com")
    val email: String,

    @Schema(description = "Contraseña del usuario", example = "Password123!")
    val password: String,

    @Schema(description = "Repetición de la contraseña para confirmación", example = "Password123!")
    val passwordRepeat: String,

    @Schema(description = "Rol del usuario", example = "USER")
    val rol: String? = "USER"
)

