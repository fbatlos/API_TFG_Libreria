package com.es.aplicacion.controller

import com.es.aplicacion.model.Avatar
import com.es.aplicacion.service.AvatarService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/avatar")
class AvatarController {
    @Autowired
    private lateinit var avatarService: AvatarService

    @GetMapping("/miAvatar")
    fun getAvatar(
        @RequestBody idAvatar: String,
        authentication: Authentication
    ): ResponseEntity<Avatar> {
        val avatar = avatarService.getAvatar(idAvatar)
        return ResponseEntity.ok(avatar)
    }

    @GetMapping("/allAvatares")
    fun getAllAvatar(authentication: Authentication): ResponseEntity<List<Avatar>>{
        val avatares = avatarService.getAllAvatares()
        return ResponseEntity.ok(avatares)
    }
}