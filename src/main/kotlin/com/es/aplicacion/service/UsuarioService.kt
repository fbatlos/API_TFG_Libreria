package com.es.aplicacion.service

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
import com.es.aplicacion.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val libroRepository: LibroRepository,
    private val passwordEncoder: PasswordEncoder,
    private val apiService: ExternalApiService,
    private val avatarRepository: AvatarRepository
) : UserDetailsService {


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

        //Comprobamos que el usuario no existe
        if (!usuarioRepository.findByUsername(usuarioInsertadoDTO.username).isEmpty) { throw Conflict("Usuario ya esta registrado.")}
        //Comprobamos que el email no existe
        if (!usuarioRepository.findByEmail(usuarioInsertadoDTO.email).isEmpty) { throw Conflict("Email ya esta registrado.") }

        usuarioRepository.save(
            Usuario(
                null,
                username = usuarioInsertadoDTO.username,
                password = passwordEncoder.encode(usuarioInsertadoDTO.password),
                email = usuarioInsertadoDTO.email,
                roles = usuarioInsertadoDTO.rol
            )
        )

        val usuario = UsuarioDTO(
            username = usuarioInsertadoDTO.username,
            email = usuarioInsertadoDTO.email,
            librosfav = mutableListOf(),
            rol = usuarioInsertadoDTO.rol,
            avatar = "68402c80be43b505fe8a2c78"
        )
        return usuario
    }

    fun getUsuario(auth: Authentication): UsuarioDTO {
        val usuario = usuarioRepository.findByUsername(auth.name).orElseThrow { NotFound("Usuario no encontrado.") }

        return UsuarioDTO(
            username = usuario.username,
            email = usuario.email,
            direccion = usuario.direccion,
            librosfav = usuario.librosfav,
            rol = usuario.roles,
            avatar = usuario.avatar!!
        )
    }

    fun addDireccion(direccion: Direccion, authentication: Authentication): String {

        val usuario = usuarioRepository.findByUsername(authentication.name).orElseThrow{NotFound("El usuario no encontrado")}

        usuario.direccion.forEach {
            if(it.equals(direccion) == true){throw BadRequest("Direccion ya existe en tus direcciones.")}
        }

        //Comprobamos provincias
        val provinciaUser = (apiService.obtenerDatosDesdeApi()?.data ?: throw BadRequest("Provincias no obtenidas.")).filter { it.PRO == direccion.provincia.uppercase() }.firstOrNull() ?: throw BadRequest("Provincia no encontrada")

        println(provinciaUser)

        //Comprobamos los municipios
        (apiService.obtenerMunicipioDatosDesdeApi(provinciaUser.CPRO)?.data ?: throw BadRequest("Municipios no obtenidos.")).filter { it.DMUN50 == direccion.municipio.uppercase() }.firstOrNull() ?: throw BadRequest("Municipio no encontrado")

        usuario.direccion.add(direccion)

        usuarioRepository.save(usuario)

        return "Se añadío con exito."
    }

    fun deleteDireccion(direccion: Direccion, authentication: Authentication) {
        val usuario = usuarioRepository.findByUsername(authentication.name).orElseThrow{NotFound("Usuario no encontrado")}
        if (usuario.direccion.contains(direccion)){
            usuario.direccion.remove(direccion)
            usuarioRepository.save(usuario)
        }else{
            throw BadRequest("Direccion no es correcta")
        }
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

    fun getFavoritos(auth: Authentication):MutableList<String> {
        val usuario = usuarioRepository.findByUsername(auth.name)
        .orElseThrow { NotFound("Usuario ${auth.name} no existe") }

        return usuario.librosfav
    }

    fun getCesta(auth: Authentication):MutableList<ItemCompra> {
        return usuarioRepository.findByUsername(auth.name).orElseThrow { NotFound("Usuario ${auth.name} no existe") }.cesta
    }

    fun addItem(auth: Authentication, itemCompra: ItemCompra): String {
        val libro = itemCompra.libro._id?.let {
            libroRepository.findById(it).orElseThrow { NotFound("El libro no existe") }
        } ?: throw BadRequest("El libro no tiene id")

        val usuario = usuarioRepository.findByUsername(auth.name)
            .orElseThrow { NotFound("El usuario no encontrado") }

        val existingItem = usuario.cesta.find { it.libro._id == libro._id }

        if (existingItem != null) {
            existingItem.cantidad += itemCompra.cantidad
        } else {
            usuario.cesta.add(itemCompra)
        }

        usuarioRepository.save(usuario)

        return "Añadido con éxito."
    }


    fun removeItem(auth: Authentication, libroId: String): String {
        val usuario = usuarioRepository.findByUsername(auth.name)
            .orElseThrow { NotFound("El usuario no encontrado") }

        val item = usuario.cesta.find { it.libro._id == libroId }
            ?: throw NotFound("El libro no está en la cesta")

        usuario.cesta.remove(item)
        usuarioRepository.save(usuario)

        return "Actualizado con éxito."
    }

    fun removeAllCesta(auth: Authentication): String {
        val usuario = usuarioRepository.findByUsername(auth.name).orElseThrow { NotFound("El usuario no encontrado") }
        usuario.cesta.clear()
        usuarioRepository.save(usuario)
        return "Exito"
    }

    fun updateItems(auth: Authentication, items:List<ItemCompra>): String {
        val usuario = usuarioRepository.findByUsername(auth.name).orElseThrow { NotFound("El usuario no encontrado") }

        usuario.cesta = items.toMutableList()
        usuarioRepository.save(usuario)
        return "Actualizado con exito."
    }

    fun updateAvatar(auth: Authentication, avatarId: String):String {
        val usuario = usuarioRepository.findByUsername(auth.name).orElseThrow { NotFound("El usuario no encontrado") }
        val avatar = avatarRepository.findById(avatarId).orElseThrow { NotFound("El avatar no encontrado") }

        usuario.avatar = avatar._id
        usuarioRepository.save(usuario)
        return "Actualizado con exito."
    }
}