package com.es.aplicacion.service

import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.BadRequest
import com.es.aplicacion.error.exception.Conflict
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Usuario
import com.es.aplicacion.repository.UsuarioRepository
import com.es.aplicacion.util.Utils
import org.apache.coyote.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UsuarioService : UserDetailsService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    private lateinit var apiService: ExternalApiService


    override fun loadUserByUsername(username: String?): UserDetails {
        var usuario: Usuario = usuarioRepository
            .findByUsername(username!!)
            .orElseThrow {
                UnauthorizedException("$username no existente")
            }

        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    fun insertUser(usuarioInsertadoDTO: UsuarioRegisterDTO) : UsuarioDTO {

        // TODO: Implementar este metodo

        if (usuarioInsertadoDTO.username.isBlank() || usuarioInsertadoDTO.password.isBlank() || usuarioInsertadoDTO.email.isBlank()) {
            //Hacer la clase de error.
            throw BadRequest("Uno o más campos estan vacios.")
        }

        if (usuarioInsertadoDTO.password != usuarioInsertadoDTO.passwordRepeat){
            throw BadRequest("Las contraseñas no son iguales.")
        }

        if(!Utils.ValidaEmail(usuarioInsertadoDTO.email)){
            throw BadRequest("Formato del email invalido")
        }

        val provincias = apiService.obtenerDatosDesdeApi()?.data ?: throw BadRequest("Provincias no obtenidas.")

        val provinciaUser = provincias.filter { it.PRO == usuarioInsertadoDTO.direccion.provincia.uppercase() }.firstOrNull() ?: throw BadRequest("Provincia no encontrada")

        val municipios = apiService.obtenerMunicipioDatosDesdeApi(provinciaUser.CPRO)?.data ?: throw BadRequest("Municipios no obtenidos.")

        val municipioUser = municipios.filter { it.DMUN50 == usuarioInsertadoDTO.direccion.municipio.uppercase() }.firstOrNull() ?: throw BadRequest("Municipio no encontrado")

        if (!usuarioRepository.findByUsername(usuarioInsertadoDTO.username).isEmpty) { throw Conflict("${usuarioInsertadoDTO.username} ya esta registrado.")}

        if (!usuarioRepository.findByEmail(usuarioInsertadoDTO.email).isEmpty) { throw Conflict("${usuarioInsertadoDTO.email} ya esta registrado.")
        }

        if (usuarioInsertadoDTO.rol == null){
            usuarioRepository.save(Usuario(null,usuarioInsertadoDTO.username,passwordEncoder.encode(usuarioInsertadoDTO.password),usuarioInsertadoDTO.email, direccion = usuarioInsertadoDTO.direccion))
        }else{
            usuarioRepository.save(Usuario(null,usuarioInsertadoDTO.username,passwordEncoder.encode(usuarioInsertadoDTO.password),usuarioInsertadoDTO.email,usuarioInsertadoDTO.rol,usuarioInsertadoDTO.direccion))
        }
        //Comprobar email
        val usuario = UsuarioDTO(
            username = usuarioInsertadoDTO.username,
            email = usuarioInsertadoDTO.email,
            rol = usuarioInsertadoDTO.rol
        )
        return usuario
    }
}