package com.es.aplicacion.error

import com.es.aplicacion.error.exception.*
import jakarta.servlet.http.HttpServletRequest
import org.apache.coyote.BadRequestException
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.naming.AuthenticationException

@ControllerAdvice
class APIExceptionHandler {

    @ExceptionHandler(AuthenticationException::class, UnauthorizedException::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun handleAuthentication(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(Exception::class, NullPointerException::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGeneric(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(BadRequestException::class,BadRequest::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleBadRequest(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(NotFound::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleNotFoundException(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(Conflict::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleConflict(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }

    @ExceptionHandler(Forbidden::class) // Las "clases" (excepciones) que se quieren controlar
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    fun handleForbidden(request: HttpServletRequest, e: Exception) : ErrorRespuesta {
        e.printStackTrace()
        return ErrorRespuesta(e.message!!, request.requestURI)
    }
}