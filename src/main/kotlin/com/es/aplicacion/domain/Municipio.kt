package com.es.aplicacion.domain

data class Municipio(
    var CMUM:String,
    val CPRO: String,
    val CUN :String,
    val DMUN50: String
){
    private var contador: Int = 0
    init {
        CMUM = (++contador).toString()
    }
}
