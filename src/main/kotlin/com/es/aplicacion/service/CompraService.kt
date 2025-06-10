package com.es.aplicacion.service

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Compra
import com.es.aplicacion.model.TipoStock
import com.es.aplicacion.repository.CompraRepository
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class CompraService(
    private val compraRepository: CompraRepository,
    private val usuarioRepository: UsuarioRepository,
    private val libroRepository: LibroRepository
) {

    fun addTicketCompra(compra: Compra): Boolean {
        usuarioRepository.findByUsername(compra.usuarioName).orElseThrow { BadRequest("Usuario no existe.") }

        if (compra.items.isEmpty()) {
            throw BadRequest("La compra no puede estar vacia.")
        }

        compraRepository.save(compra)
        return true
    }

    fun obtenerCompras(usuarioName: String): MutableList<Compra> {
        usuarioRepository.findByUsername(usuarioName).orElseThrow { BadRequest("Usuario no existe.") }
        return compraRepository.findByUsuarioName(usuarioName).toMutableList()
    }

    fun obtenerAllCompras(): MutableList<Compra> {
        return compraRepository.findAll()
    }

    fun actualizarStock(compra: Compra, authentication: Authentication) {
        val usuario = usuarioRepository.findByUsername(authentication.name)
            .orElseThrow { NotFound("Usuario no encontrado.") }

        compra.items.forEach { item ->
            val libro = libroRepository.findById(item.libro._id!!)
                .orElseThrow { NotFound("Libro ${item.libro.titulo} no encontrado.") }

            if (libro.stock.numero < item.cantidad) {
                throw BadRequest("Stock insuficiente para ${item.libro.titulo}. Stock actual: ${libro.stock.numero}")
            }
        }

        compra.items.forEach { item ->
            val libro = libroRepository.findById(item.libro._id!!).get()
            libro.stock.numero -= item.cantidad
            if (libro.stock.numero == 0) {
                libro.stock.tipo = TipoStock.AGOTADO
            }
            libroRepository.save(libro)
        }
    }
}
