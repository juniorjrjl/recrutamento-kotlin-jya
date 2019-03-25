package br.com.recrutamento.controller

import br.com.recrutamento.config.AppConfig
import br.com.recrutamento.controller.EventController.eventService
import br.com.recrutamento.dto.*
import br.com.recrutamento.exception.EventServiceException
import br.com.recrutamento.service.EventService
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.nhaarman.mockitokotlin2.*
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.json.JavalinJackson
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.w3c.dom.events.EventException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class `testando os endpoints de event` {

    private val app = Javalin.create().start(9090)

    private val eventServiceMock: EventService = mock()

    private val bucarIssues = "http://localhost:9090/events/:number/events"
    private val enviarEvento = "http://localhost:9090/webhook"

    private val kodein : Kodein = Kodein{
        bind<EventService>() with singleton { eventServiceMock }
    }

    @BeforeEach
    fun `setUp`(){
        JavalinJackson.configure(jacksonObjectMapper().findAndRegisterModules())
        EventController.eventService = eventServiceMock
        app.routes{
            ApiBuilder.path("events/:number/events") {
                ApiBuilder.get(EventController::getIssue)
            }
            ApiBuilder.path("webhook") {
                ApiBuilder.post(EventController::salvar)
            }
        }
        app
    }

    @AfterEach
    fun `tearDown`(){
        app.stop()
    }

    inline fun <reified T: Any>String.deserialize(): T  = jacksonObjectMapper().readValue(this)

    @Test
    fun `testar retorno de issue inexistente`(){
        whenever(eventServiceMock.buscarIssue(5)).thenReturn(null)
        val response  = khttp.get(bucarIssues.replace(":number", "5"))
        val dto = response.text
        assertEquals( HttpStatus.OK_200, response.statusCode)
        assertTrue(dto.isEmpty())
    }

    @Test
    fun `testar retorno statuscode 500`(){
        given(eventServiceMock.buscarIssue(10)).willAnswer{ throw Exception()}
        val response  = khttp.get(bucarIssues.replace(":number", "10"))
        val dto = response.text.deserialize<ErroResponse>()
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR_500, response.statusCode)
        assertEquals(("Ocorreu um erro inesperado, entre em contato com o administrador."), dto.mensagemErro)
    }

    @Test
    fun `testar retorno Issue`(){
        val createdAt = LocalDateTime.now()
        val updatedAt = createdAt.plusMonths(2)
        val closedAt = updatedAt.plusMonths(2)
        val createdAtComment = LocalDateTime.now()
        val updatedAtComment = createdAt.plusMonths(2)
        val dtoRetorno = IssueDetalhadaDTO(1,1,"title", "body", IssueStatusEnum.OPEN, createdAt, updatedAt, closedAt, "userName")
        dtoRetorno.comments = listOf(CommentDetalhadoDTO("userName", "body", createdAtComment, updatedAtComment))
        whenever(eventServiceMock.buscarIssue(5)).thenReturn(dtoRetorno)
        val response  = khttp.get(bucarIssues.replace(":number", "5"))
        val dto = response.text
        assertEquals( HttpStatus.OK_200, response.statusCode)
        assertTrue(dto.contains(createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))))
        assertTrue(dto.contains(updatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))))
        assertTrue(dto.contains(closedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))))
        assertTrue(dto.contains(createdAtComment.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))))
        assertTrue(dto.contains(updatedAtComment.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))))
    }

    @Test
    fun `testar internalServerError evento`(){
        given(eventServiceMock.salvar("")).willAnswer{ throw Exception()}
        val response  = khttp.post(enviarEvento)
        val dto = response.text.deserialize<ErroResponse>()
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR_500, response.statusCode)
        assertEquals(("Ocorreu um erro inesperado, entre em contato com o administrador."), dto.mensagemErro)
    }

    @Test
    fun `testar eventServiceException`(){
        given(eventServiceMock.salvar("")).willAnswer{ throw EventServiceException("simulando erro") }
        val response  = khttp.post(enviarEvento)
        val dto = response.text.deserialize<ErroResponse>()
        assertEquals( HttpStatus.BAD_REQUEST_400, response.statusCode)
        assertEquals(("simulando erro"), dto.mensagemErro)
    }

    @Test
    fun `testar sucesso processamento`(){
        val json = argumentCaptor<String>()
        doNothing().whenever(eventServiceMock).salvar(json.capture())
        val response  = khttp.post(enviarEvento, json = mapOf("teste" to "teste"))
        val dto = response.text
        assertEquals( HttpStatus.NO_CONTENT_204, response.statusCode)
        assertTrue(dto.isEmpty())
        assertEquals("""{"teste":"teste"}""", json.firstValue)
    }

}