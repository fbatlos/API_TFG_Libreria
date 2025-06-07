package com.es.aplicacion.compra
import com.es.aplicacion.dto.LibroDTO
import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.*
import com.es.aplicacion.repository.CompraRepository
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.UsuarioRepository
import com.es.aplicacion.service.CompraService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.core.Authentication
import java.util.*

class CompraServiceTest {
    private val compraRepository: CompraRepository = mock()
    private val usuarioRepository: UsuarioRepository = mock()
    private val libroRepository: LibroRepository = mock()

    private val compraService = CompraService(compraRepository, usuarioRepository, libroRepository)

    private val direccionMock = mock<Direccion>()

    @Test
    fun `addTicketCompra exitoso`() {
        val compra = Compra(
            usuarioName = "user1",
            items = listOf(mock<ItemCompra>()),
            fechaCompra = "2025-06-06",
            direccion = direccionMock
        )

        whenever(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(mock()))
        whenever(compraRepository.save(compra)).thenReturn(compra)

        val result = compraService.addTicketCompra(compra)

        assertTrue(result)
        verify(compraRepository).save(compra)
    }

    @Test
    fun `addTicketCompra lanza BadRequest si usuario no existe`() {
        val compra = Compra(
            usuarioName = "user1",
            items = listOf(mock<ItemCompra>()),
            fechaCompra = "2025-06-06",
            direccion = direccionMock
        )

        whenever(usuarioRepository.findByUsername("user1")).thenReturn(Optional.empty())

        val ex = assertThrows<BadRequest> {
            compraService.addTicketCompra(compra)
        }

        assertEquals("Usuario no existe.", ex.message)
    }

    @Test
    fun `addTicketCompra lanza BadRequest si items vacios`() {
        val compra = Compra(
            usuarioName = "user1",
            items = emptyList(),
            fechaCompra = "2025-06-06",
            direccion = direccionMock
        )

        whenever(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(mock()))

        val ex = assertThrows<BadRequest> {
            compraService.addTicketCompra(compra)
        }

        assertEquals("La compra no puede estar vacia.", ex.message)
    }

    @Test
    fun `obtenerCompras devuelve lista de compras`() {
        val compraList = mutableListOf(
            Compra("user1", listOf(mock()), "2025-06-06", direccionMock)
        )
        whenever(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(mock()))
        whenever(compraRepository.findByUsuarioName("user1")).thenReturn(compraList)

        val result = compraService.obtenerCompras("user1")

        assertEquals(compraList, result)
    }

    @Test
    fun `obtenerCompras lanza BadRequest si usuario no existe`() {
        whenever(usuarioRepository.findByUsername("user1")).thenReturn(Optional.empty())

        val ex = assertThrows<BadRequest> {
            compraService.obtenerCompras("user1")
        }

        assertEquals("Usuario no existe.", ex.message)
    }

    @Test
    fun `obtenerAllCompras devuelve todas las compras`() {
        val compras = mutableListOf(
            Compra("user1", listOf(mock()), "2025-06-06", direccionMock)
        )
        whenever(compraRepository.findAll()).thenReturn(compras)

        val result = compraService.obtenerAllCompras()

        assertEquals(compras, result)
    }

    @Test
    fun `actualizarStock lanza NotFound si usuario no encontrado`() {
        val authentication: Authentication = mock()
        whenever(authentication.name).thenReturn("user1")
        whenever(usuarioRepository.findByUsername("user1")).thenReturn(Optional.empty())

        val compra = Compra("user1", emptyList(), "2025-06-06", direccionMock)

        val ex = assertThrows<NotFound> {
            compraService.actualizarStock(compra, authentication)
        }

        assertEquals("Usuario no encontrado.", ex.message)
    }

    @Test
    fun `actualizarStock lanza NotFound si libro no encontrado`() {
        val libroId = "libro1"
        val usuarioName = "user1"
        val authentication: Authentication = mock()
        whenever(authentication.name).thenReturn(usuarioName)

        val itemCompra = ItemCompra(libro = LibroDTO(_id = libroId, titulo = "Libro 1",9.99,"EUR"), cantidad = 2)
        val compra = Compra(usuarioName, listOf(itemCompra), "2025-06-06", direccionMock)

        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(mock()))
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.empty())

        val ex = assertThrows<NotFound> {
            compraService.actualizarStock(compra, authentication)
        }

        assertEquals("Libro Libro 1 no encontrado.", ex.message)
    }

    @Test
    fun `actualizarStock lanza BadRequest si stock insuficiente`() {
        val libroId = "libro1"
        val usuarioName = "user1"
        val authentication: Authentication = mock()
        whenever(authentication.name).thenReturn(usuarioName)

        val stock = Stock(numero = 2, tipo = TipoStock.DISPONIBLE)
        val libro = Libro(_id = libroId, titulo = "Libro 1", stock = stock)
        val libroDTO = LibroDTO("libro2","libro2",9.99,"EUR")
        val itemCompra = ItemCompra(libro = libroDTO, cantidad = 2)
        val compra = Compra(usuarioName, listOf(itemCompra), "2025-06-06", direccionMock)

        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(mock()))

        val ex = assertThrows<NotFound> {
            compraService.actualizarStock(compra, authentication)
        }

        assertEquals("Libro libro2 no encontrado.", ex.message)
    }
}
