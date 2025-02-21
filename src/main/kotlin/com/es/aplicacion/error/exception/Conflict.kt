package com.es.aplicacion.error.exception

class Conflict(message: String) : Exception("Conflict (409). $message") {
}