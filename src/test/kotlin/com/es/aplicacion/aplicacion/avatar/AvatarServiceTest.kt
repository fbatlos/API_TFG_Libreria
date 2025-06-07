package com.es.aplicacion.avatar

import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Avatar
import com.es.aplicacion.repository.AvatarRepository
import com.es.aplicacion.service.AvatarService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class AvatarServiceTest {

    @Mock
    lateinit var avatarRepository: AvatarRepository

    @InjectMocks
    lateinit var avatarService: AvatarService

    @Test
    fun `getAllAvatares devuelve lista de avatares`() {
        val avatars = listOf(
            Avatar(_id = "1", filename = "avatar1.png", mimeType = "image/png", data = ByteArray(0)),
            Avatar(_id = "2", filename = "avatar2.jpg", mimeType = "image/jpeg", data = ByteArray(0))
        )
        whenever(avatarRepository.findAll()).thenReturn(avatars)

        val result = avatarService.getAllAvatares()

        assertEquals(2, result.size)
        assertEquals("1", result[0]._id)
        /*
       El ByteArray es dinamico solo funciona a veces.
       assertEquals("[B@34819867", result.data.toString())
        */
    }

    @Test
    fun `getAvatar devuelve avatar si existe`() {
        val avatar =   Avatar(_id = "1", filename = "avatar1.png", mimeType = "image/png", data = ByteArray(0))
        whenever(avatarRepository.findById("1")).thenReturn(Optional.of(avatar))

        val result = avatarService.getAvatar("1")

        assertEquals("1", result._id)
        /*
        El ByteArray es dinamico solo funciona a veces.
        assertEquals("[B@34819867", result.data.toString())
         */
    }

    @Test
    fun `getAvatar lanza NotFound si no existe`() {
        whenever(avatarRepository.findById("1")).thenReturn(Optional.empty())

        val ex = assertThrows<NotFound> {
            avatarService.getAvatar("1")
        }

        assertEquals("No se ha encontrado el avatar.", ex.message)
    }
}
