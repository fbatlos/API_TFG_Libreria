package com.es.aplicacion.service

import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.NotFound
import com.es.aplicacion.model.Direccion
import com.es.aplicacion.model.Usuario
import com.es.aplicacion.repository.UsuarioRepository
import com.es.aplicacion.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService : UserDetailsService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    private lateinit var apiService: ExternalApiService


    override fun loadUserByUsername(username: String?): UserDetails {
        if (username.isNullOrBlank()) {
            throw BadRequest("El nombre de usuario no puede estar vacío.")
        }

        var usuario: Usuario = usuarioRepository
            .findByUsername(username)
            .orElseThrow {
                NotFound("$username no existente")
            }

        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    fun insertUser(usuarioInsertadoDTO: UsuarioRegisterDTO) : UsuarioDTO {

        if (usuarioInsertadoDTO.username.isBlank() || usuarioInsertadoDTO.password.isBlank() || usuarioInsertadoDTO.email.isBlank()) {
            throw BadRequest("Uno o más campos estan vacios.")
        }

        if (usuarioInsertadoDTO.password != usuarioInsertadoDTO.passwordRepeat){
            throw BadRequest("Las contraseñas no son iguales.")
        }

        if(!Utils.ValidaEmail(usuarioInsertadoDTO.email)){
            throw BadRequest("Formato del email invalido")
        }

        //Comprobamos provincias
        val provinciaUser = (apiService.obtenerDatosDesdeApi()?.data ?: throw BadRequest("Provincias no obtenidas.")).filter { it.PRO == usuarioInsertadoDTO.direccion.provincia.uppercase() }.firstOrNull() ?: throw BadRequest("Provincia no encontrada")

        //Comprobamos los municipios
        (apiService.obtenerMunicipioDatosDesdeApi(provinciaUser.CPRO)?.data ?: throw BadRequest("Municipios no obtenidos.")).filter { it.DMUN50 == usuarioInsertadoDTO.direccion.municipio.uppercase() }.firstOrNull() ?: throw BadRequest("Municipio no encontrado")

        //Comprobamos que el usuario no existe
        if (!usuarioRepository.findByUsername(usuarioInsertadoDTO.username).isEmpty) { throw Conflict("${usuarioInsertadoDTO.username} ya esta registrado.")}
        //Comprobamos que el email no existe
        if (!usuarioRepository.findByEmail(usuarioInsertadoDTO.email).isEmpty) { throw Conflict("${usuarioInsertadoDTO.email} ya esta registrado.") }

        usuarioRepository.save(
            Usuario(
                null,
                username = usuarioInsertadoDTO.username,
                password = passwordEncoder.encode(usuarioInsertadoDTO.password),
                email = usuarioInsertadoDTO.email,
                roles = usuarioInsertadoDTO.rol,
                direccion = mutableListOf(usuarioInsertadoDTO.direccion),
                librosfav = mutableListOf()
            )
        )

        val usuario = UsuarioDTO(
            username = usuarioInsertadoDTO.username,
            email = usuarioInsertadoDTO.email,
            rol = usuarioInsertadoDTO.rol
        )
        return usuario
    }

    fun addDireccion(direccion: Direccion, authentication: Authentication): String {

        val usuario = usuarioRepository.findByUsername(authentication.name).orElseThrow{NotFound("El usuario no encontrado")}

        usuario.direccion.forEach {
            if(it.equals(direccion) == true){throw BadRequest("Direccion ya existe en tus direcciones.")}
        }

        usuario.direccion.add(direccion)

        usuarioRepository.save(usuario)

        return "Se añadío con exito."
    }

    fun addFavorito(auth: Authentication, libroId: String):String {
        val usuario = usuarioRepository.findByUsername(auth.name)
            .orElseThrow { NotFound("Usuario ${auth.name} no existe") }

        if (!usuario.librosfav.contains(libroId)) {
            usuario.librosfav.add(libroId)
            usuarioRepository.save(usuario)
            return "Libro añadido con exito."
        }

        return "Libro ya es favorito."
    }

    fun removeFavorito(auth: Authentication, libroId: String):String {
        val usuario = usuarioRepository.findByUsername(auth.name)
        .orElseThrow { NotFound("Usuario ${auth.name} no existe") }

        if (usuario.librosfav.contains(libroId)) {
            usuario.librosfav.remove(libroId)
            usuarioRepository.save(usuario)
            return "Libro eliminado con exito de favoritos."
        }
        throw BadRequest("El libro no está en favoritos.")
    }
}