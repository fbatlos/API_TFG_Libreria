package com.es.aplicacion.service

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Valoracion
import com.es.aplicacion.repository.CompraRepository
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

    @Autowired
    private lateinit var compraRepository: CompraRepository

    fun gatValoraciones(libroId:String): List<Valoracion>?{
        libroRepository.findById(libroId).orElseThrow { NotFound("Libro no encontrado.") }

        return valoracionRepository.findValoracionesByLibroId(libroId).orElseThrow { BadRequest("Libro no encontrado.") }

    }

    fun addValoracion(valoracion: Valoracion): String{
        usuarioRepository.findByUsername(valoracion.usuarioName).orElseThrow { BadRequest("Id Usuario no registrada.") }
        libroRepository.findById(valoracion.libroid).orElseThrow { BadRequest("Libro no registrada.") }
        val compra = compraRepository.findByUsuarioName(valoracion.usuarioName).filter {
            it.items.map { it.libro._id }.contains(valoracion.libroid)
        }

        println(compra)

        if (valoracion.valoracion < 0 || valoracion.comentario.isEmpty() ){
            throw BadRequest("Un valor no es valido.")
        }
        println(!valoracionRepository.findValoracionesByLibroId(valoracion.libroid).get().isEmpty())
        println(!valoracionRepository.findValoracionByUsuarioName(valoracion.usuarioName).get().isEmpty())

        if(!valoracionRepository.findValoracionesByLibroId(valoracion.libroid).get().isEmpty() && (!valoracionRepository.findValoracionByUsuarioName(valoracion.usuarioName).get().isEmpty())){
            throw Conflict("Ya has dado tu valoracion.")
        }

        valoracionRepository.save(valoracion)
        return  "Valoracion añadida con exito"
    }

    fun deleteValoracion(valoracionId: String,authentication: Authentication): String{
        val valoracion = valoracionRepository.findById(valoracionId).orElseThrow { BadRequest("Valoracion no encontrado.") }

        val usuario = usuarioRepository.findByUsername(authentication.name).orElseThrow { BadRequest("Usuario no registrado.") }

        if (valoracion.usuarioName != usuario.username && !authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))){
            throw UnauthorizedException("Usuario no autorizado.")
        }

        valoracionRepository.deleteById(valoracionId)

        return  "Valoracion exito"
    }

    fun getMisValoraciones(authentication: Authentication): List<Valoracion>{
        usuarioRepository.findByUsername(authentication.name).orElseThrow { NotFound("Usuario no encontrado.") }

        val valoraciones = valoracionRepository.findValoracionByUsuarioName(authentication.name).get()
        return valoraciones
    }
}