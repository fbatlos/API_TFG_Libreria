package com.es.aplicacion.model

import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioInterfazDTO

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Respuesta de autenticación que contiene el token JWT y la información del usuario.")
data class AuthResponse(
    @Schema(description = "Token JWT generado tras autenticación exitosa", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val token: String,

    @Schema(description = "Información básica del usuario autenticado")
    val user: UsuarioInterfazDTO
)

