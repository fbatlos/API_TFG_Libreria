package com.es.aplicacion.service

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Valoracion
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.UsuarioRepository
import com.es.aplicacion.repository.ValoracionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class ValoracionService {
    @Autowired
    private lateinit var valoracionRepository: ValoracionRepository

    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    fun gatValoraciones(libroId:String): List<Valoracion>?{
        libroRepository.findById(libroId).orElseThrow { NotFound("Libro no encontrado.") }

        return valoracionRepository.findValoracionesByLibroId(libroId).orElseThrow { BadRequest("Libro no encontrado.") }

    }

    fun addValoracion(valoracion: Valoracion): String{
        usuarioRepository.findById(valoracion.usuarioid).orElseThrow { BadRequest("Id Usuario no registrada.") }
        libroRepository.findById(valoracion.libroid).orElseThrow { BadRequest("Libro no registrada.") }

        if (valoracion.valoracion < 0 || valoracion.comentario.isEmpty() ){
            throw BadRequest("Un valor no es valido.")
        }
        println(valoracionRepository.findValoracionesByLibroId(valoracion.libroid).isPresent)
        println(valoracionRepository.findValoracionByUsuarioId(valoracion.usuarioid).isPresent)

        if((valoracionRepository.findValoracionesByLibroId(valoracion.libroid).isPresent) && (valoracionRepository.findValoracionByUsuarioId(valoracion.usuarioid).isPresent)){
            throw Conflict("Ya has dado tu valoracion.")
        }

        valoracionRepository.save(valoracion)
        return  "Valoracion añadida con exito"
    }

    fun deleteValoracion(valoracionId: String,authentication: Authentication): String{
        val valoracion = valoracionRepository.findById(valoracionId).orElseThrow { BadRequest("Valoracion no encontrado.") }

        val usuario = usuarioRepository.findByUsername(authentication.name).orElseThrow { BadRequest("Usuario no registrado.") }

        if (valoracion.usuarioid != usuario.username && !authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))){
            throw UnauthorizedException("Usuario no autorizado.")
        }

        valoracionRepository.deleteById(valoracionId)

        return  "Valoracion exito"
    }
}