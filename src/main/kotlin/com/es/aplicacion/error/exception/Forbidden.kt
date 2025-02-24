package com.es.aplicacion.error.exception

class Forbidden(message: String) : Exception("Forbidden (403). $message") {
}