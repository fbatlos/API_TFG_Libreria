package com.es.aplicacion.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para las credenciales de login de usuario")
data class LoginUsuarioDTO(
    @Schema(description = "Nombre de usuario para autenticación", example = "usuario123")
    val username: String,

    @Schema(description = "Contraseña del usuario", example = "P@ssw0rd!")
    val password: String
)
