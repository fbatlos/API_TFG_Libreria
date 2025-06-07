package com.es.aplicacion.libro

import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Libro
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.ValoracionRepository
import com.es.aplicacion.service.LibroService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
class LibroServiceTest {

    @Mock
    lateinit var libroRepository: LibroRepository

    @Mock
    lateinit var valoracionRepository: ValoracionRepository

    lateinit var libroService: LibroService

    @BeforeEach
    fun setup() {
        libroService = LibroService(libroRepository, valoracionRepository)
    }

    val libroMock = Libro(
        _id = "1",
        isbn13 = "1234567890123",
        imagen = "imagen.jpg",
        precio = 10.0,
        titulo = "Título",
        autores = listOf("Autor 1"),
        categorias = listOf("Categoria 1"),
        moneda = "EUR"
    )

    @Test
    fun `addLibro lanza Conflict si el libro ya existe`() {
        whenever(libroRepository.findByIsbn13(libroMock.isbn13!!)).thenReturn(Optional.of(libroMock))

        val ex = assertThrows<Conflict> {
            libroService.addLibro(libroMock)
        }

        assertEquals("El libro ya existe.", ex.message)
        verify(libroRepository, never()).save(any())
    }

    @Test
    fun `addLibro guarda libro si no existe`() {
        whenever(libroRepository.findByIsbn13(libroMock.isbn13!!)).thenReturn(Optional.empty())
        // Utils.validacionesLibroSimples es función estática, asumimos que funciona bien, no la testeamos aquí

        whenever(libroRepository.save(libroMock)).thenReturn(libroMock)

        val result = libroService.addLibro(libroMock)

        assertTrue(result)
        verify(libroRepository).save(libroMock)
    }

    @Test
    fun `putLibro actualiza libro existente`() {
        val libroActualizado = libroMock.copy(titulo = "Nuevo título")

        whenever(libroRepository.findById("1")).thenReturn(Optional.of(libroMock))

        val result = libroService.putLibro("1", libroActualizado)

        assertTrue(result)
        verify(libroRepository).save(libroMock)
        assertEquals("Nuevo título", libroMock.titulo)
    }

    @Test
    fun `putLibro lanza NotFound si libro no existe`() {
        whenever(libroRepository.findById("2")).thenReturn(Optional.empty())

        val ex = assertThrows<NotFound> {
            libroService.putLibro("2", libroMock)
        }

        assertEquals("Libro no encontrado.", ex.message)
    }

    @Test
    fun `deleteLibro elimina libro existente`() {
        whenever(libroRepository.findById("1")).thenReturn(Optional.of(libroMock))
        doNothing().whenever(libroRepository).deleteById("1")

        val result = libroService.deleteLibro("1")

        assertTrue(result)
        verify(libroRepository).deleteById("1")
    }

    @Test
    fun `deleteLibro lanza BadRequest si libro no existe`() {
        whenever(libroRepository.findById("2")).thenReturn(Optional.empty())

        val ex = assertThrows<BadRequest> {
            libroService.deleteLibro("2")
        }

        assertEquals("El libro no encontrado.", ex.message)
    }

    @Test
    fun `getLibros filtra por categoria y autor`() {
        val listaMock = listOf(libroMock)
        whenever(libroRepository.buscarPorCategoriaOAutorSimilar("Categoria 1", "Autor 1")).thenReturn(listaMock)

        val result = libroService.getLibros("Categoria 1", "Autor 1")

        assertEquals(listaMock, result)
    }

    @Test
    fun `getLibros filtra por categoria solamente`() {
        val listaMock = listOf(libroMock)
        whenever(libroRepository.buscarPorCategoriaSimilar("Categoria 1")).thenReturn(listaMock)

        val result = libroService.getLibros("Categoria 1", null)

        assertEquals(listaMock, result)
    }

    @Test
    fun `getLibros filtra por autor solamente`() {
        val listaMock = listOf(libroMock)
        whenever(libroRepository.buscarPorAutorSimilar("Autor 1")).thenReturn(listaMock)

        val result = libroService.getLibros(null, "Autor 1")

        assertEquals(listaMock, result)
    }

    @Test
    fun `getLibros sin filtros devuelve todo`() {
        val listaMock = listOf(libroMock)
        whenever(libroRepository.findAll()).thenReturn(listaMock)

        val result = libroService.getLibros(null, null)

        assertEquals(listaMock, result)
    }

    @Test
    fun `buscarLibros devuelve resultados con query`() {
        val listaMock = listOf(libroMock)
        whenever(libroRepository.findByTituloOAutorContainingIgnoreCase("Título")).thenReturn(listaMock)

        val result = libroService.buscarLibros("Título")

        assertEquals(listaMock, result)
    }

    @Test
    fun `buscarLibros devuelve lista vacia si query es null o blank`() {
        val resultNull = libroService.buscarLibros(null)
        val resultBlank = libroService.buscarLibros(" ")

        assertTrue(resultNull.isEmpty())
        assertTrue(resultBlank.isEmpty())
    }
}
