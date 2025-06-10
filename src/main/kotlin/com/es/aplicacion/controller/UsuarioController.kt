package com.es.aplicacion.controller

import com.es.aplicacion.dto.LoginUsuarioDTO
import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioInterfazDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.*
import com.es.aplicacion.service.TokenService
import com.es.aplicacion.service.UsuarioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
class UsuarioController {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    @Autowired
    private lateinit var tokenService: TokenService
    @Autowired
    private lateinit var usuarioService: UsuarioService

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario y devuelve un token de autenticación")
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
            throw UnauthorizedException("Credenciales incorrectas repetidas")
        }

        var token = tokenService.generarToken(authentication)


        return ResponseEntity(AuthResponse(token = token, user = UsuarioInterfazDTO(usuario.username,usuario.rol)), HttpStatus.CREATED)

    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario existente y devuelve un token JWT")
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

    @Operation(summary = "Obtener datos del usuario autenticado", description = "Devuelve la información del usuario actual")
    @GetMapping("/usuario")
    fun getUsuario(
        authentication: Authentication
    ): ResponseEntity<UsuarioDTO>{
        return ResponseEntity(usuarioService.getUsuario(authentication),HttpStatus.OK)
    }

    @Operation(summary = "Añadir dirección", description = "Añade una dirección a la cuenta del usuario autenticado")
    @PutMapping("/direccion")
    fun addDireccion(
        @RequestBody direccion: Direccion,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(usuarioService.addDireccion(direccion, authentication),HttpStatus.OK)
    }

    @Operation(summary = "Eliminar dirección", description = "Elimina una dirección de la cuenta del usuario autenticado")
    @DeleteMapping("/direccion")
    fun deleteDireccion(
        @RequestBody direccion: Direccion,
        authentication: Authentication
    ): ResponseEntity<String> {
         usuarioService.deleteDireccion(direccion, authentication)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Obtener favoritos", description = "Devuelve la lista de libros favoritos del usuario autenticado")
    @GetMapping("/favoritos")
    fun getFavoritos(
        authentication: Authentication
    ):ResponseEntity<MutableList<String>>{
        return ResponseEntity(usuarioService.getFavoritos(authentication),HttpStatus.OK)
    }

    @Operation(summary = "Añadir libro favorito", description = "Añade un libro a los favoritos del usuario")
    @PostMapping("/favoritos/{libroId}")
    fun addFavorite(
        @PathVariable libroId:String,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(usuarioService.addFavorito(authentication, libroId),HttpStatus.CREATED)
    }

    @Operation(summary = "Eliminar libro favorito", description = "Elimina un libro de los favoritos del usuario")
    @DeleteMapping("/favoritos/{libroId}")
    fun removeFavorite(
        @PathVariable libroId:String,
        authentication: Authentication
    ): ResponseEntity<String> {
        usuarioService.removeFavorito(authentication, libroId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Obtener cesta de compra", description = "Devuelve la cesta actual del usuario autenticado")
    @GetMapping("/cesta")
    fun getCesta(
        authentication: Authentication
    ): ResponseEntity<MutableList<ItemCompra>> {
        return ResponseEntity(usuarioService.getCesta(authentication),HttpStatus.OK)
    }

    @Operation(summary = "Añadir libro a la cesta", description = "Añade un libro a la cesta del usuario")
    @PostMapping("/cesta")
    fun addItemCompra(
        @RequestBody itemCompra: ItemCompra,
        authentication: Authentication
    ): ResponseEntity<String> {
        usuarioService.addItem(authentication, itemCompra)
        return ResponseEntity.ok("Añadido")
    }

    @Operation(summary = "Actualizar libros en la cesta", description = "Actualiza la lista de libros en la cesta del usuario")
    @PutMapping("/cesta")
    fun updateItemCompra(
        @RequestBody itemCompra: List<ItemCompra>,
        authentication: Authentication
    ): ResponseEntity<String> {
        usuarioService.updateItems(authentication, itemCompra)
        return ResponseEntity.ok("Actualizado")
    }

    @Operation(summary = "Eliminar libro de la cesta", description = "Elimina un libro concreto de la cesta del usuario")
    @DeleteMapping("/cesta/{libroId}")
    fun removeItemCompra(
        @PathVariable libroId: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        usuarioService.removeItem(authentication, libroId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Vaciar la cesta", description = "Elimina todos los libros de la cesta del usuario")
    @DeleteMapping("/cesta")
    fun removeAllCompra(
        authentication: Authentication
    ): ResponseEntity<Void> {
        usuarioService.removeAllCesta(authentication)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Actualizar avatar", description = "Actualiza el avatar del usuario autenticado")
    @PutMapping("/avatar")
    fun updateAvatar(
        @RequestBody avatarId:String,
        authentication: Authentication
    ): ResponseEntity<String> {
        return ResponseEntity(usuarioService.updateAvatar(authentication,avatarId),HttpStatus.OK)
    }
}