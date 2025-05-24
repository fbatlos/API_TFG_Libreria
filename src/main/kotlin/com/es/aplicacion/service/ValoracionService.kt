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
import java.time.LocalDateTime

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

    fun addValoracion(valoracion: Valoracion): String {
        usuarioRepository.findByUsername(valoracion.usuarioName).orElseThrow { BadRequest("Id Usuario no registrada.") }
        libroRepository.findById(valoracion.libroid).orElseThrow { BadRequest("Libro no registrado.") }

        val compra = compraRepository.findByUsuarioName(valoracion.usuarioName).filter {
            it.items.map { it.libro._id }.contains(valoracion.libroid)
        }

        println(compra)

        if (valoracion.valoracion < 0 || valoracion.comentario.isEmpty()) {
            throw BadRequest("Un valor no es válido.")
        }

        val yaValorado = valoracionRepository.findByLibroidAndUsuarioName(valoracion.libroid, valoracion.usuarioName).isPresent

        if (yaValorado) {
            throw Conflict("Ya has dado tu valoración a este libro.")
        }

        valoracionRepository.save(valoracion)
        return "Valoración añadida con éxito"
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

    fun poblarValoracionesAleatorias() {
        val libros = libroRepository.findAll()

        val usuariosEjemplo = listOf("marta", "javi", "lucas", "nerea", "ines")

        libros.forEach { libro ->
            usuariosEjemplo.forEach { usuario ->
                val numValoraciones = (1..5).random()  // de 1 a 5 valoraciones aleatorias por libro

                repeat(numValoraciones) {
                    val valoracion = Valoracion(
                        _id = null,
                        libroid = libro._id!!,
                        usuarioName = usuario,
                        valoracion = (1..5).random(),
                        comentario = "Valoración de prueba",
                        fecha = LocalDateTime.now()
                    )
                    valoracionRepository.save(valoracion)
                }
            }
        }
    }
}