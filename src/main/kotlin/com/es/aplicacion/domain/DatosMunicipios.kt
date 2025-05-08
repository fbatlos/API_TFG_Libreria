package com.es.aplicacion.domain

data class DatosMunicipios(
    val next : Boolean? = null,
    val previous: Boolean? = null,
    val current_page: Int? = null,
    val update_date :String,
    val size:String ,
    val data : List<Municipio>
)
