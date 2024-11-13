package com.modsen.rating.client

import com.modsen.rating.util.ExceptionMessages
import feign.Response
import feign.codec.ErrorDecoder
import jakarta.persistence.EntityNotFoundException
import java.io.IOException
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RetrieveMessageErrorDecoder : ErrorDecoder {

    private val log = LoggerFactory.getLogger(com.modsen.rating.client.RetrieveMessageErrorDecoder::class.java)
    private val defaultErrorDecoder = ErrorDecoder.Default()

    override fun decode(methodKey: String, response: Response): Exception {
        var errorMessage = ExceptionMessages.UNKNOWN_ERROR

        response.body()?.let { responseBody ->
            try {
                errorMessage = responseBody.asInputStream().bufferedReader().use { it.readText() }
            } catch (e: IOException) {
                log.error("Error reading response body: {}", e.message)
                return Exception(ExceptionMessages.UNABLE_TO_READ_ERROR_RESPONSE)
            }
        }

        return when (response.status()) {
            400 -> BadRequestException(errorMessage)
            404 -> EntityNotFoundException(errorMessage)
            else -> defaultErrorDecoder.decode(methodKey, response)
        }
    }

}