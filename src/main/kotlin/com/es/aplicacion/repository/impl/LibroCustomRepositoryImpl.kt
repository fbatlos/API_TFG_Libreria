package com.es.aplicacion.repository.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class LibroCustomRepositoryImpl:LibroCustomRepository {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
}