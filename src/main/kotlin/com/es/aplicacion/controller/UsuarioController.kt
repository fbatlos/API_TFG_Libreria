package com.es.aplicacion.controller

import com.es.aplicacion.dto.LoginUsuarioDTO
import com.es.aplicacion.dto.UsuarioInterfazDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.AuthResponse
import com.es.aplicacion.model.Direccion
import com.es.aplicacion.service.TokenService
import com.es.aplicacion.service.UsuarioService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    @Autowired
    private lateinit var tokenService: TokenService
    @Autowired
    private lateinit var usuarioService: UsuarioService

    @PostMapping("/register")
    fun insert(
        httpRequest: HttpServletRequest,
        @RequestBody usuarioRegisterDTO: UsuarioRegisterDTO
    ) : ResponseEntity<AuthResponse> {

        // TODO: Implementar este metodo

        val usuario = usuarioService.insertUser(usuarioRegisterDTO)

        val authentication: Authentication
        try {
            authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(usuarioRegisterDTO.username, usuarioRegisterDTO.password))
        } catch (e: AuthenticationException) {
            throw UnauthorizedException("Credenciales incorrectas")
        }

        var token = tokenService.generarToken(authentication)


        return ResponseEntity(AuthResponse(token = token, user = UsuarioInterfazDTO(usuario.username,usuario.rol)), HttpStatus.CREATED)

    }

    @PostMapping("/login")
    fun login(@RequestBody usuario: LoginUsuarioDTO) : ResponseEntity<Any>? {

        val authentication: Authentication
        try {
            authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(usuario.username, usuario.password))
        } catch (e: AuthenticationException) {
            throw UnauthorizedException("Credenciales incorrectas")
        }

        // SI PASAMOS LA AUTENTICACIÓN, SIGNIFICA QUE ESTAMOS BIEN AUTENTICADOS
        // PASAMOS A GENERAR EL TOKEN
        var token = tokenService.generarToken(authentication)

        return ResponseEntity(mapOf("token" to token), HttpStatus.OK)
    }


    @PutMapping("/direccion")
    fun addDireccion(
        @RequestBody direccion: Direccion,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(usuarioService.addDireccion(direccion, authentication),HttpStatus.OK)
    }

    @GetMapping("/favoritos")
    fun getFavoritos(
        authentication: Authentication
    ):ResponseEntity<MutableList<String>>{
        return ResponseEntity(usuarioService.getFavoritos(authentication),HttpStatus.OK)
    }

    @PostMapping("/favoritos/{libroId}")
    fun addFavorite(
        @PathVariable libroId:String,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(usuarioService.addFavorito(authentication, libroId),HttpStatus.CREATED)
    }

    @DeleteMapping("/favoritos/{libroId}")
    fun removeFavorite(
        @PathVariable libroId:String,
        authentication: Authentication
    ): ResponseEntity<String> {
        usuarioService.removeFavorito(authentication, libroId)
        return ResponseEntity.noContent().build()
    }




}