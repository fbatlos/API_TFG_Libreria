package com.es.aplicacion.usuario.service

import com.es.aplicacion.dto.LibroDTO
import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Direccion
import com.es.aplicacion.model.ItemCompra
import com.es.aplicacion.model.Libro
import com.es.aplicacion.model.Usuario
import com.es.aplicacion.repository.AvatarRepository
import com.es.aplicacion.repository.LibroRepository
import com.es.aplicacion.repository.UsuarioRepository
import com.es.aplicacion.service.ExternalApiService
import com.es.aplicacion.service.UsuarioService
import com.es.aplicacion.util.Utils
import org.hamcrest.core.Every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.*

fun mockAuth(username: String = "user"): Authentication {
 return mock {
  on { getName() } doReturn username
 }
}


@ExtendWith(MockitoExtension::class)
class UsuarioServiceTest {

 @Mock
 lateinit var usuarioRepository: UsuarioRepository

 @Mock
 lateinit var libroRepository: LibroRepository

 @Mock
 lateinit var passwordEncoder: PasswordEncoder

 @Mock
 lateinit var apiService: ExternalApiService

 @Mock
 lateinit var avatarRepository: AvatarRepository

 @Mock
 lateinit var utils: Utils


 @InjectMocks
 lateinit var usuarioService: UsuarioService

 val name = "paco"

 @Test
 fun `loadUserByUsername devuelve UserDetails si existe`() {
  val usuario = Usuario(
   _id = null, username = "testuser", password = "pass", email = "test@test.com")
  whenever(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario))

  val userDetails: UserDetails = usuarioService.loadUserByUsername("testuser")

  assertEquals("testuser", userDetails.username)
 }

 @Test
 fun `loadUserByUsername lanza NotFound si no existe`() {
  whenever(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty())

  val ex = assertThrows<NotFound> {
   usuarioService.loadUserByUsername("testuser")
  }

  assertEquals("testuser no existente", ex.message)
 }

 @Test
 fun `insertUser lanza BadRequest si campos vacios`() {
  val dto = UsuarioRegisterDTO("", "", "", "", "")

  val ex = assertThrows<BadRequest> {
   usuarioService.insertUser(dto)
  }

  assertEquals("Uno o más campos estan vacios.", ex.message)
 }

 @Test
 fun `insertUser lanza BadRequest si contraseñas distintas`() {
  val dto = UsuarioRegisterDTO("user", "pass", "email@test.com", "USER", "otra")

  val ex = assertThrows<BadRequest> {
   usuarioService.insertUser(dto)
  }

  assertEquals("Las contraseñas no son iguales.", ex.message)
 }

 @Test
 fun `insertUser lanza BadRequest si email invalido`() {
  val dto = UsuarioRegisterDTO("user", "pass", "invalido", "invalido", "pass")

  val ex = assertThrows<BadRequest> {
   usuarioService.insertUser(dto)
  }

  assertEquals("Formato del email invalido", ex.message)
 }



 @Test
 fun `insertUser lanza Conflict si usuario existe`() {
  val dto = UsuarioRegisterDTO("paco", "email@test.com", "pass", "pass")

  whenever(usuarioRepository.findByUsername("paco")).thenReturn(Optional.of(mock()))


   val ex = assertThrows<Conflict> {
    usuarioService.insertUser(dto)
   }

   assertEquals("Usuario ya esta registrado.", ex.message)

 }

 @Test
 fun `insertUser lanza Conflict si email existe`() {
  val dto = UsuarioRegisterDTO("user",  "email@test.com", "pass", "pass")

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.empty())
  whenever(usuarioRepository.findByEmail("email@test.com")).thenReturn(Optional.of(mock()))

   val ex = assertThrows<Conflict> {
    usuarioService.insertUser(dto)
   }

   assertEquals("Email ya esta registrado.", ex.message)

 }

 @Test
 fun `insertUser guarda y devuelve UsuarioDTO correctamente`() {
  val dto = UsuarioRegisterDTO("user",  "email@test.com", "pass", "pass")

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.empty())
  whenever(usuarioRepository.findByEmail("email@test.com")).thenReturn(Optional.empty())
  whenever(passwordEncoder.encode("pass")).thenReturn("hashedPass")
  whenever(usuarioRepository.save(any())).thenAnswer { it.arguments[0] }

   val result = usuarioService.insertUser(dto)

   assertEquals("user", result.username)
   assertEquals("email@test.com", result.email)
   assertEquals("USER", result.rol)

 }


 @Test
 fun `addFavorito añade libro si no está`() {
  val auth = mockAuth("user")

  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", librosfav = mutableListOf())

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val msg = usuarioService.addFavorito(auth, "libro1")

  assertEquals("Libro añadido con exito.", msg)
  assertTrue(usuario.librosfav.contains("libro1"))
 }

 @Test
 fun `addFavorito devuelve mensaje si ya está en favoritos`() {
  val auth = mockAuth("user")

  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", librosfav = mutableListOf("libro1"))

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val msg = usuarioService.addFavorito(auth, "libro1")

  assertEquals("Libro ya es favorito.", msg)
 }

 @Test
 fun `removeFavorito elimina si existe`() {
  val auth = mockAuth("user")

  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", librosfav = mutableListOf("libro1"))

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val msg = usuarioService.removeFavorito(auth, "libro1")

  assertEquals("Libro eliminado con exito de favoritos.", msg)
  assertFalse(usuario.librosfav.contains("libro1"))
 }

 @Test
 fun `removeFavorito lanza BadRequest si no está en favoritos`() {
  val auth = mockAuth("user")


  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", librosfav = mutableListOf())

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val ex = assertThrows<BadRequest> {
   usuarioService.removeFavorito(auth, "libro1")
  }

  assertEquals("El libro no está en favoritos.", ex.message)
 }

 @Test
 fun `removeItem elimina item de cesta si existe`() {
  val auth = mockAuth("user")

  val libro = LibroDTO(_id = "libro1", precio = 12.99, moneda = "EUR", titulo = "libro1")
  val item = ItemCompra(libro, 1)
  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", cesta = mutableListOf(item))

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val msg = usuarioService.removeItem(auth, "libro1")

  assertEquals("Actualizado con éxito.", msg)
  assertTrue(usuario.cesta.isEmpty())
 }

 @Test
 fun `removeItem lanza NotFound si no está en cesta`() {
  val auth = mockAuth("user")

  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", cesta = mutableListOf())

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val ex = assertThrows<NotFound> {
   usuarioService.removeItem(auth, "libro1")
  }

  assertEquals("El libro no está en la cesta", ex.message)
 }

 @Test
 fun `addItem añade libro a la cesta si no existe`() {
  val auth = mockAuth("user")
  val libro = LibroDTO(_id = "libro1", precio = 10.0, moneda = "EUR", titulo = "Libro1")
  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", cesta = mutableListOf())

  val itemExistente = ItemCompra(LibroDTO("libro1", "Libro1", 10.0, "EUR"), 2)
  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))
  whenever(libroRepository.findById("libro1")).thenReturn(Optional.of(Libro("libro1", "Libro1", listOf("YO"),"locuca",10.0, "EUR")))

  val msg = usuarioService.addItem(auth, itemExistente)

  assertEquals("Añadido con éxito.", msg)
  assertTrue(usuario.cesta.any { it.libro._id == "libro1" && it.cantidad == 2 })
 }

 @Test
 fun `addItem incrementa cantidad si libro ya existe en cesta`() {
  val auth = mockAuth("user")
  val libro = LibroDTO(_id = "libro1", precio = 10.0, moneda = "EUR", titulo = "Libro1")
  val itemExistente = ItemCompra(LibroDTO("libro1", "Libro1", 10.0, "EUR"), 2)
  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", cesta = mutableListOf(ItemCompra(LibroDTO("libro1", "Libro1", 10.0, "EUR"), 1)))

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))
  whenever(libroRepository.findById("libro1")).thenReturn(Optional.of(Libro("libro1", "Libro1", listOf(),"Locuara",10.0, "EUR")))

  val msg = usuarioService.addItem(auth, itemExistente)

  assertEquals("Añadido con éxito.", msg)
  assertTrue(usuario.cesta.any { it.libro._id == "libro1" && it.cantidad == 3 })
 }

 @Test
 fun `removeAllCesta vacia la cesta correctamente`() {
  val auth = mockAuth("user")
  val libro1 = ItemCompra(LibroDTO("libro1", "Libro1", 10.0, "EUR"), 1)
  val libro2 = ItemCompra(LibroDTO("libro2", "Libro2", 15.0, "EUR"), 2)
  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", cesta = mutableListOf(libro1, libro2))

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val msg = usuarioService.removeAllCesta(auth)

  assertEquals("Exito", msg)
  assertTrue(usuario.cesta.isEmpty())
 }

 @Test
 fun `updateItems actualiza cantidades correctamente`() {
  val auth = mockAuth("user")
  val libro1 = LibroDTO(_id = "libro1", precio = 10.0, moneda = "EUR", titulo = "Libro1")
  val libro2 = LibroDTO(_id = "libro2", precio = 15.0, moneda = "EUR", titulo = "Libro2")

  val item1 = ItemCompra(LibroDTO("libro1", "Libro1", 10.0, "EUR"), 1)
  val item2 = ItemCompra(LibroDTO("libro2", "Libro2", 15.0, "EUR"), 2)

  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER", cesta = mutableListOf(item1, item2))

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val nuevosItems = listOf(
   ItemCompra(LibroDTO(_id = "libro1", precio = 10.0, moneda = "EUR", titulo = "Libro1"),1), // supongo que updateItems recibe lista DTO con cantidades?
   ItemCompra(LibroDTO(_id = "libro2", precio = 15.0, moneda = "EUR", titulo = "Libro2"),2)
  )

  val msg = usuarioService.updateItems(auth,nuevosItems)
 }

 @Test
 fun `updateAvatar lanza NotFound si avatar no existe`() {
  val auth = mockAuth("user")
  val usuario = Usuario(null, "user", "pass", "mail@test.com", "USER")
  val avatarId = "avatar123"

  whenever(usuarioRepository.findByUsername("user")).thenReturn(Optional.of(usuario))

  val ex = assertThrows<NotFound> {
   usuarioService.updateAvatar(auth, avatarId)
  }

  assertEquals("El avatar no encontrado", ex.message)
 }
}

