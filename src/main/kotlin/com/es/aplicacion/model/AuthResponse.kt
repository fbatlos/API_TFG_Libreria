package com.es.aplicacion.model

import com.es.aplicacion.dto.UsuarioDTO

data class AuthResponse(
    val token: String,
    val user: UsuarioDTO
)
