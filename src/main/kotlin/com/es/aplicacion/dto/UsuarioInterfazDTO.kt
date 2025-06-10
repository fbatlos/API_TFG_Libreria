package com.es.aplicacion.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para mostrar información básica del usuario")
data class UsuarioInterfazDTO(
    @Schema(description = "Nombre de usuario", example = "usuario123")
    val username: String,

    @Schema(description = "Rol del usuario", example = "USER")
    val rol: String?
)

