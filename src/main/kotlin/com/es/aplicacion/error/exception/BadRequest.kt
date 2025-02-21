package com.es.aplicacion.error.exception

class BadRequest(message: String) : Exception("Bad request (400). $message") {
}