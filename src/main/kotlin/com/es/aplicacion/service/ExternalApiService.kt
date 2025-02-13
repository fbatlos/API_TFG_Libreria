package com.es.aplicacion.service

import com.es.aplicacion.domain.DatosMunicipios
import com.es.mongocomp.domain.DatosProvincias
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ExternalApiService(private val webClient: WebClient.Builder) {

    fun obtenerDatosDesdeApi(): DatosProvincias? {
        return webClient.build()
            .get()
            .uri("https://apiv1.geoapi.es/provincias?type=JSON&key=13f670e3d73798eb074bc9fa0c8514fe4b5ddea5f06226bab27afc8a3687d153")
            .retrieve()
            .bodyToMono(DatosProvincias::class.java)
            .block() // ⚠️ Esto bloquea el hilo, usar `subscribe()` en código reactivo
    }

    fun obtenerMunicipioDatosDesdeApi(cpro:String): DatosMunicipios? {
        return webClient.build()
            .get()
            .uri("https://apiv1.geoapi.es/municipios?CPRO=${cpro}&type=JSON&key=13f670e3d73798eb074bc9fa0c8514fe4b5ddea5f06226bab27afc8a3687d153")
            .retrieve()
            .bodyToMono(DatosMunicipios::class.java)
            .block() // ⚠️ Esto bloquea el hilo, usar `subscribe()` en código reactivo
    }
}