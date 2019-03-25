package br.com.recrutamento.controller

import br.com.recrutamento.dto.ErroResponse
import br.com.recrutamento.exception.EventServiceException
import br.com.recrutamento.service.EventService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus


object EventController {

    lateinit var eventService: EventService

    fun getIssue(ctx: Context){
        try {
            val dto = eventService.buscarIssue(ctx.pathParam("number").toLong())
            if (dto != null) {
                ctx.status(HttpStatus.OK_200).json(dto)
            }else{
                ctx.status(HttpStatus.OK_200)
            }
        }catch (e: Exception){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500).json(ErroResponse("Ocorreu um erro inesperado, entre em contato com o administrador."))
        }
    }

    fun salvar(ctx: Context){
        try{
            eventService.salvar(ctx.body())
            ctx.status(HttpStatus.NO_CONTENT_204)
        }catch (e: EventServiceException){
            ctx.status(HttpStatus.BAD_REQUEST_400).json(ErroResponse(e.message!!))
        }catch (e: Exception){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500).json(ErroResponse("Ocorreu um erro inesperado, entre em contato com o administrador."))
        }
    }

}