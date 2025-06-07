package com.es.aplicacion.service

import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Avatar
import com.es.aplicacion.repository.AvatarRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AvatarService(
    private val avatarRepository: AvatarRepository
) {

    fun getAllAvatares(): List<Avatar> =
        avatarRepository.findAll()

    fun getAvatar(idAvatar: String): Avatar =
        avatarRepository.findById(idAvatar)
            .orElseThrow { NotFound("No se ha encontrado el avatar.") }
}
