package com.es.aplicacion.service

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.model.Compra
import com.es.aplicacion.repository.CompraRepository
import com.es.aplicacion.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompraService {
    @Autowired
    private lateinit var compraRepository: CompraRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    fun addTicketCompra(compra: Compra):Boolean {
        usuarioRepository.findByUsername(compra.usuarioName).orElseThrow { BadRequest("Usuario no existe.") }

        if (compra.items.isEmpty()){throw BadRequest("La compra no puede estar vacia.")}

        compraRepository.save(compra)
        return true
    }

    fun obtenerCompras(usuarioName:String): MutableList<Compra>{
        usuarioRepository.findByUsername(usuarioName).orElseThrow { BadRequest("Usuario no existe.") }

        return compraRepository.findByUsuarioName(usuarioName).toMutableList()
    }

    fun obtenerAllCompras(): MutableList<Compra>{
        return compraRepository.findAll()
    }
}