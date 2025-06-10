package com.es.aplicacion.controller

import com.es.aplicacion.model.Avatar
import com.es.aplicacion.service.AvatarService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/avatar")
@Tag(name = "Avatares", description = "Operaciones relacionadas con los avatares de usuario")
class AvatarController {

    @Autowired
    private lateinit var avatarService: AvatarService

    @Operation(
        summary = "Obtener avatar por ID",
        description = "Recupera los detalles de un avatar concreto a partir de su ID."
    )
    @GetMapping("/miAvatar/{idAvatar}")
    fun getAvatar(
        @PathVariable idAvatar: String,
        authentication: Authentication
    ): ResponseEntity<Avatar> {
        val avatar = avatarService.getAvatar(idAvatar)
        return ResponseEntity.ok(avatar)
    }

    @Operation(
        summary = "Obtener todos los avatares",
        description = "Devuelve la lista completa de avatares disponibles en la aplicación."
    )
    @GetMapping("/allAvatares")
    fun getAllAvatar(
        authentication: Authentication
    ): ResponseEntity<List<Avatar>> {
        val avatares = avatarService.getAllAvatares()
        return ResponseEntity.ok(avatares)
    }
}
