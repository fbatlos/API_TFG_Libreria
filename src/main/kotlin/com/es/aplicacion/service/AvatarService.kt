package com.es.aplicacion.service

import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Avatar
import com.es.aplicacion.repository.AvatarRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AvatarService {

    @Autowired
    private lateinit var avatarRepository: AvatarRepository

    fun getAllAvatares(): List<Avatar>{
        return avatarRepository.findAll()
    }

    fun getAvatar(idAvatar:String): Avatar{
        return avatarRepository.findById(idAvatar).orElseThrow { NotFound("No se ha encontrado el avatar.") }
    }

}