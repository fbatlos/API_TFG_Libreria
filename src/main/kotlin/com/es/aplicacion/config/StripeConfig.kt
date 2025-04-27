package com.es.aplicacion.config

import com.stripe.Stripe
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "stripe")
class StripeProperties {
    lateinit var secretKey: String
    lateinit var publicKey: String
}


@Configuration
class StripeConfig(private val stripeProperties: StripeProperties) {

    @Bean
    fun stripeClient(): String? {
        // Configura la clave secreta para Stripe
        Stripe.apiKey = stripeProperties.secretKey
        return Stripe.apiKey
    }
}