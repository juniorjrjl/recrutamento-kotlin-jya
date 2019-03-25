package br.com.recrutamento.service

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.dto.CommentAtualizarDTO
import br.com.recrutamento.dto.IssueAtualizacaoDTO
import br.com.recrutamento.dto.IssueStatusEnum
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.time.LocalDateTime
import java.time.Month
import javax.validation.Validation
import javax.validation.Validator

class `verificando eventos de atualização` {

    private val issueDAOMock : IIssueDAO = mock()
    private val commentDAOMock: ICommentDAO = mock()
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    private val kodein : Kodein = Kodein{
        bind<IIssueDAO>() with singleton { issueDAOMock }
        bind<ICommentDAO>() with singleton { commentDAOMock }
        bind<Validator>() with  singleton { validator }
    }
    private val eventService = EventService(kodein)

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/IssueClose.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando fechamento de Issue`(json: String) {
        val dataFechamento = argumentCaptor<LocalDateTime>()
        val status = argumentCaptor<IssueStatusEnum>()
        val idGitHub = argumentCaptor<Long>()
        doNothing().whenever(issueDAOMock).atualizarDataFechamento(
            dataFechamento.capture(),
            status.capture(),
            idGitHub.capture())
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        Assertions.assertEquals(LocalDateTime.of(2018, 12, 11, 18, 39, 51), dataFechamento.firstValue)
        Assertions.assertEquals(IssueStatusEnum.CLOSED, status.firstValue)
        Assertions.assertEquals(1L, idGitHub.firstValue)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/CommentUpdate.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando atualização de comentário`(json: String){
        val dtoAtualizacao = argumentCaptor<CommentAtualizarDTO>()
        doNothing().whenever(commentDAOMock).atualizar(dtoAtualizacao.capture())
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        val dtoCapturado = dtoAtualizacao.firstValue
        Assertions.assertEquals("new body", dtoCapturado.body)
        Assertions.assertEquals(LocalDateTime.of(2018, Month.DECEMBER, 11, 16, 3, 51), dtoCapturado.updatedAt)
        Assertions.assertEquals(1L, dtoCapturado.idGitHub)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/IssueUpdate.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando atualização de issue`(json: String){
        val dtoAtualizacao = argumentCaptor<IssueAtualizacaoDTO>()
        doNothing().whenever(issueDAOMock).atualizar(dtoAtualizacao.capture())
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        val dtoCapturado = dtoAtualizacao.firstValue
        Assertions.assertEquals("nova issue", dtoCapturado.title)
        Assertions.assertEquals(IssueStatusEnum.OPEN, dtoCapturado.state)
        Assertions.assertEquals("edited body", dtoCapturado.body)
        Assertions.assertEquals(LocalDateTime.of(2018, Month.DECEMBER, 11, 16, 35, 20), dtoCapturado.updatedAt)
        Assertions.assertEquals(1L, dtoCapturado.idGitHub)
    }
}