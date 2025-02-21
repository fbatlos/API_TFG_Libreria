package com.es.aplicacion.model

import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioInterfazDTO

data class AuthResponse(
    val token: String,
    val user: UsuarioInterfazDTO
)
