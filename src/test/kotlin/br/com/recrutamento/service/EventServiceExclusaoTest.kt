package br.com.recrutamento.service

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
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
import javax.validation.Validation
import javax.validation.Validator

class `verificando eventos de exclusão` {

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
    @CsvFileSource(resources = ["/csv/CommentDelete.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando exclusão de comentario`(json: String){
        val idGitHub = argumentCaptor<Long>()
        doNothing().whenever(commentDAOMock).excluirPorIdGitHub(idGitHub.capture())
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        Assertions.assertEquals(1L, idGitHub.firstValue)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/IssueDelete.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando exclusão de issue`(json: String){
        val idGitHub = argumentCaptor<Long>()
        val idIssue = argumentCaptor<Long>()
        doNothing().whenever(issueDAOMock).excluirPorIdGitHub(idGitHub.capture())
        whenever(issueDAOMock.buscarIdIssue(1)).thenReturn(10)
        doNothing().whenever(commentDAOMock).excluirCommentsIssue(idIssue.capture())
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        Assertions.assertEquals(1, idGitHub.firstValue)
        Assertions.assertEquals(10, idIssue.firstValue)
    }

}