package com.es.aplicacion.model

import org.bson.codecs.pojo.annotations.BsonId
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.mongodb.core.mapping.Document

@Document("Usuarios")
@Schema(description = "Entidad que representa un usuario del sistema")
data class Usuario(
    @BsonId
    @Schema(description = "Identificador único del usuario", example = "64aef3f4b1234c5d67890abc", nullable = true)
    val _id: String?,

    @Schema(description = "Nombre de usuario", example = "juan123")
    val username: String,

    @Schema(description = "Contraseña cifrada del usuario", example = "encryptedPassword123")
    val password: String,

    @Schema(description = "Correo electrónico del usuario", example = "juan@example.com")
    val email: String,

    @Schema(description = "Rol del usuario en el sistema", example = "USER", nullable = true)
    val roles: String? = "USER",

    @Schema(description = "Lista de direcciones del usuario")
    var direccion: MutableList<Direccion> = mutableListOf(),

    @Schema(description = "Lista de IDs de libros favoritos")
    val librosfav: MutableList<String> = mutableListOf(),

    @Schema(description = "Lista de items en la cesta de compra")
    var cesta: MutableList<ItemCompra> = mutableListOf(),

    @Schema(description = "ID del avatar del usuario", example = "68402c80be43b505fe8a2c78", nullable = true)
    var avatar: String? = "68402c80be43b505fe8a2c78"
)
