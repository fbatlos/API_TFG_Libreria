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
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class ValoracionService(
    private val valoracionRepository: ValoracionRepository,
    private val libroRepository: LibroRepository,
    private val usuarioRepository: UsuarioRepository,
    private val compraRepository: CompraRepository
) {

    fun gatValoraciones(libroId: String): List<Valoracion>? {
        libroRepository.findById(libroId).orElseThrow { NotFound("Libro no encontrado.") }

        return valoracionRepository.findValoracionesByLibroId(libroId)
            .orElseThrow { BadRequest("Libro no encontrado.") }
    }

    fun addValoracion(valoracion: Valoracion): String {
        usuarioRepository.findByUsername(valoracion.usuarioName)
            .orElseThrow { BadRequest("Id Usuario no registrada.") }

        val libro = libroRepository.findById(valoracion.libroid)
            .orElseThrow { BadRequest("Libro no registrado.") }

        val compra = compraRepository.findByUsuarioName(valoracion.usuarioName).filter {
            it.items.map { it.libro._id }.contains(valoracion.libroid)
        }

        if (compra.isEmpty()){
            throw BadRequest("Tienes que comprar el libro para comentar.")
        }

        if (valoracion.valoracion < 0 || valoracion.comentario.isEmpty()) {
            throw BadRequest("Un valor no es válido.")
        }

        val yaValorado = valoracionRepository.findByLibroidAndUsuarioName(
            valoracion.libroid,
            valoracion.usuarioName
        ).isPresent

        if (yaValorado) {
            throw Conflict("Ya has dado tu valoración a este libro.")
        }

        valoracionRepository.save(valoracion)
        actualizarMediaLibro(libro._id!!)

        return "Valoración añadida con éxito"
    }

    fun deleteValoracion(valoracionId: String, authentication: Authentication): String {

        val usuario = usuarioRepository.findByUsername(authentication.name)
            .orElseThrow { BadRequest("Usuario no registrado.") }

        val valoracion = valoracionRepository.findById(valoracionId)
            .orElseThrow { BadRequest("Valoracion no encontrada.") }

        if (valoracion.usuarioName != usuario.username &&
            !authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))
        ) {
            throw UnauthorizedException("Usuario no autorizado.")
        }

        valoracionRepository.deleteById(valoracionId)
        actualizarMediaLibro(valoracion.libroid)

        return "Valoración eliminada con éxito"
    }

    fun getMisValoraciones(authentication: Authentication): List<Valoracion> {
        usuarioRepository.findByUsername(authentication.name)
            .orElseThrow { NotFound("Usuario no encontrado.") }

        return valoracionRepository.findValoracionByUsuarioName(authentication.name)
            .get()
    }

    private fun actualizarMediaLibro(libroId: String) {
        val libro = libroRepository.findById(libroId)
            .orElseThrow { NotFound("Libro no encontrado.") }

        val valoraciones = valoracionRepository.findValoracionesByLibroId(libroId).orElse(emptyList())

        val media = if (valoraciones.isNotEmpty()) {
            valoraciones.map { it.valoracion }.average()
        } else {
            0.0
        }

        libro.valoracionMedia = BigDecimal(media).setScale(2, RoundingMode.HALF_UP).toDouble()
        libroRepository.save(libro)
    }
}
