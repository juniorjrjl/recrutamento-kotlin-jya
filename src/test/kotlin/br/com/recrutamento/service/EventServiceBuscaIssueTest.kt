package br.com.recrutamento.service

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.dto.*
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import javax.validation.Validation
import javax.validation.Validator

class `testes do classe Eventservice` {

    private val issueDAOMock : IIssueDAO = mock()
    private val commentDAOMock: ICommentDAO = mock()
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    private val kodein : Kodein = Kodein{
        bind<IIssueDAO>() with singleton { issueDAOMock }
        bind<ICommentDAO>() with singleton { commentDAOMock }
        bind<Validator>() with  singleton { validator }
    }
    private val eventService = EventService(kodein)

    @Test
    fun `retornando Issue nula`(){
        whenever(issueDAOMock.buscarDetalhes(any())).thenReturn(null)
        assertDoesNotThrow{ assertNull(eventService.buscarIssue(0)) }
    }

    @Test
    fun `retornar Issue sem comentários`(){
        val dtoIssue = IssueDetalhadaDTO(1, 1, "title", "body", IssueStatusEnum.OPEN,
            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "userName")
        val dtoComments = listOf(CommentDetalhadoDTO("userName1", "body1", LocalDateTime.now(), LocalDateTime.now().plusMonths(3)))
        whenever(issueDAOMock.buscarDetalhes(any())).thenReturn(dtoIssue)
        whenever(commentDAOMock.buscarCommentsIssue(1)).thenReturn(dtoComments)
        val dtoRetorno = eventService.buscarIssue(1)
        assertNotNull(dtoRetorno)
        assertEquals(dtoComments, dtoRetorno!!.comments)
        assertEquals(dtoIssue, dtoRetorno)
    }

}