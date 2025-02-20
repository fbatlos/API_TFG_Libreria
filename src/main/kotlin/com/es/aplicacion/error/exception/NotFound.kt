package com.es.aplicacion.error.exception

class NotFound(message: String) : Exception("Not found (404). $message") {
}