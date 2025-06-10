package com.es.aplicacion.valoracion

import com.es.aplicacion.dto.LibroDTO
import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.*
import com.es.aplicacion.repository.CompraRepository
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.UsuarioRepository
import com.es.aplicacion.repository.ValoracionRepository
import com.es.aplicacion.service.ValoracionService
import org.springframework.security.core.Authentication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.*


@ExtendWith(MockitoExtension::class)
class ValoracionServiceTest {

    @Mock
    lateinit var valoracionRepository: ValoracionRepository
    @Mock lateinit var libroRepository: LibroRepository
    @Mock lateinit var usuarioRepository: UsuarioRepository
    @Mock lateinit var compraRepository: CompraRepository
    val authentication = mock<Authentication>()

    @InjectMocks
    lateinit var valoracionService: ValoracionService


    val libroId = "libro1"
    val usuarioName = "user1"
    val valoracionId = "val1"

    val libroMock = Libro(_id = libroId, valoracionMedia = 0.0)
    val usuarioMock = Usuario(
        _id = null,
        username = "user1",
        email = "user1@gmail.com",
        password = "pass",
        roles = "ROLE_ADMIN"
    )

    val usuarioMockUser = Usuario(
        _id = null,
        username = "user2",
        email = "user2@gmail.com",
        password = "pass"
    )

    val valoracionMock = Valoracion(
        _id = valoracionId,
        usuarioName = usuarioName,
        libroid = libroId,
        valoracion = 5,
        comentario = "Excelente"
    )

    @Test
    fun `gatValoraciones retorna lista si libro existe`() {
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.of(libroMock))
        whenever(valoracionRepository.findValoracionesByLibroId(libroId)).thenReturn(Optional.of(listOf(valoracionMock)))

        val result = valoracionService.gatValoraciones(libroId)

        assertNotNull(result)
        assertEquals(1, result!!.size)
        assertEquals(valoracionMock, result[0])
    }

    @Test
    fun `gatValoraciones lanza NotFound si libro no existe`() {
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.empty())

        val ex = assertThrows<NotFound> {
            valoracionService.gatValoraciones(libroId)
        }

        assertEquals("Libro no encontrado.", ex.message)
    }

    @Test
    fun `addValoracion exitoso`() {
        // Añadimos un libro a la cesta simulando que ha sido comprado
        val itemCompra = ItemCompra(LibroDTO(libroMock._id,libroId,9.99,"EUR"), cantidad = 1)
        usuarioMock.cesta.add(itemCompra)
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(usuarioMock))
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.of(libroMock))
        whenever(valoracionRepository.findByLibroidAndUsuarioName(libroId, usuarioName)).thenReturn(Optional.empty())

        val msg = valoracionService.addValoracion(valoracionMock)

        assertEquals("Valoración añadida con éxito", msg)
        verify(valoracionRepository).save(valoracionMock)
        verify(libroRepository).save(libroMock)
    }


    @Test
    fun `addValoracion lanza BadRequest si usuario no existe`() {
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.empty())

        val ex = assertThrows<BadRequest> {
            valoracionService.addValoracion(valoracionMock)
        }

        assertEquals("Id Usuario no registrada.", ex.message)
    }

    @Test
    fun `addValoracion lanza BadRequest si libro no existe`() {
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(usuarioMock))
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.empty())

        val ex = assertThrows<BadRequest> {
            valoracionService.addValoracion(valoracionMock)
        }

        assertEquals("Libro no registrado.", ex.message)
    }

    @Test
    fun `addValoracion lanza BadRequest si valoracion o comentario invalidos`() {
        val invalido = valoracionMock.copy(valoracion = -1)
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(usuarioMock))
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.of(libroMock))

        val ex = assertThrows<BadRequest> {
            valoracionService.addValoracion(invalido)
        }
        assertEquals("Un valor no es válido.", ex.message)

        val invalidoComentario = valoracionMock.copy(comentario = "")
        val ex2 = assertThrows<BadRequest> {
            valoracionService.addValoracion(invalidoComentario)
        }
        assertEquals("Un valor no es válido.", ex2.message)
    }

    @Test
    fun `addValoracion lanza Conflict si ya valorado`() {
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(usuarioMock))
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.of(libroMock))
        whenever(compraRepository.findByUsuarioName(usuarioName)).thenReturn(emptyList())
        whenever(valoracionRepository.findByLibroidAndUsuarioName(libroId, usuarioName)).thenReturn(Optional.of(valoracionMock))

        val ex = assertThrows<Conflict> {
            valoracionService.addValoracion(valoracionMock)
        }
        assertEquals("Ya has dado tu valoración a este libro.", ex.message)
    }

    @Test
    fun `deleteValoracion exitoso con admin`() {
        whenever(valoracionRepository.findById(valoracionId)).thenReturn(Optional.of(valoracionMock))
        whenever(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock))
        whenever(authentication.name).thenReturn("admin")
        // 👇 esta línea es la clave
        whenever(authentication.authorities).thenReturn(listOf(SimpleGrantedAuthority("ROLE_ADMIN")))

        doNothing().whenever(valoracionRepository).deleteById(valoracionId)
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.of(libroMock))

        val msg = valoracionService.deleteValoracion(valoracionId, authentication)

        assertEquals("Valoración eliminada con éxito", msg)
        verify(valoracionRepository).deleteById(valoracionId)
        verify(libroRepository).save(libroMock)
    }


    @Test
    fun `deleteValoracion exitoso con usuario propietario`() {
        whenever(valoracionRepository.findById(valoracionId)).thenReturn(Optional.of(valoracionMock))
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(usuarioMock))
        whenever(authentication.name).thenReturn(usuarioName)
        doNothing().whenever(valoracionRepository).deleteById(valoracionId)
        whenever(libroRepository.findById(libroId)).thenReturn(Optional.of(libroMock))

        val msg = valoracionService.deleteValoracion(valoracionId, authentication)

        assertEquals("Valoración eliminada con éxito", msg)
        verify(valoracionRepository).deleteById(valoracionId)
        verify(libroRepository).save(libroMock)
    }

    @Test
    fun `deleteValoracion lanza UnauthorizedException si no admin ni propietario`() {
        whenever(valoracionRepository.findById(valoracionId)).thenReturn(Optional.of(valoracionMock))
        whenever(usuarioRepository.findByUsername("user2")).thenReturn(Optional.of(usuarioMockUser))
        whenever(authentication.name).thenReturn("user2")
        whenever(authentication.authorities).thenReturn(emptyList())

        val ex = assertThrows<UnauthorizedException> {
            valoracionService.deleteValoracion(valoracionId, authentication)
        }
        assertEquals("Usuario no autorizado.", ex.message)
    }

    @Test
    fun `getMisValoraciones retorna lista`() {
        val authentication = mock<Authentication>()
        whenever(authentication.name).thenReturn(usuarioName)

        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.of(usuarioMock))
        whenever(valoracionRepository.findValoracionByUsuarioName(usuarioName)).thenReturn(Optional.of(listOf(valoracionMock)))

        val result = valoracionService.getMisValoraciones(authentication)

        assertEquals(1, result.size)
        assertEquals(valoracionMock, result[0])
    }


    @Test
    fun `getMisValoraciones lanza NotFound si usuario no existe`() {
        whenever(authentication.name).thenReturn(usuarioName) // ✅ <- aquí
        whenever(usuarioRepository.findByUsername(usuarioName)).thenReturn(Optional.empty())

        val ex = assertThrows<NotFound> {
            valoracionService.getMisValoraciones(authentication)
        }

        assertEquals("Usuario no encontrado.", ex.message)
    }

}
