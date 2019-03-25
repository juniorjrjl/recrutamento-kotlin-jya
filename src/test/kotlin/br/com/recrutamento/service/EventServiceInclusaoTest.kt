package br.com.recrutamento.service

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.dto.CommentCadastroDTO
import br.com.recrutamento.dto.IssueCadastroDTO
import br.com.recrutamento.dto.IssueDetalhadaDTO
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

class `verificando eventos de inclusão` {

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
    @CsvFileSource(resources = ["/csv/CommentInsert.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando inclusão de comentarios`(json: String){
        val dtoCadastro = argumentCaptor<CommentCadastroDTO>()
        doNothing().whenever(commentDAOMock).cadastrar(dtoCadastro.capture())
        whenever(issueDAOMock.buscarDetalhes(1L))
            .thenReturn(
                IssueDetalhadaDTO(1L, 1L, "title", "body", IssueStatusEnum.OPEN,
                    LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "username")
            )
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        val dtoCapturado = dtoCadastro.firstValue
        Assertions.assertEquals("aguardando o projeto", dtoCapturado.body)
        Assertions.assertEquals("juniorjrjl", dtoCapturado.userName)
        Assertions.assertEquals(LocalDateTime.of(2018, Month.DECEMBER, 10, 15, 10, 2), dtoCapturado.createdAt)
        Assertions.assertEquals(LocalDateTime.of(2018, Month.DECEMBER, 10, 15, 10, 2), dtoCapturado.updatedAt)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/IssueInsert.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando a inclusão de issue`(json: String){
        val dtoCadastro = argumentCaptor<IssueCadastroDTO>()
        doNothing().whenever(issueDAOMock).cadastrar(dtoCadastro.capture())
        whenever(issueDAOMock.buscarDetalhes(1L)).thenReturn(null)
        Assertions.assertDoesNotThrow { eventService.salvar(json) }
        val dtoCapturado = dtoCadastro.firstValue
        Assertions.assertNull(dtoCapturado.updatedAt)
        Assertions.assertNull(dtoCapturado.closedAt)
        Assertions.assertEquals(LocalDateTime.of(2018, Month.DECEMBER, 8, 23, 10, 17), dtoCapturado.createdAt)
        Assertions.assertEquals("juniorjrjl", dtoCapturado.userName)
        Assertions.assertEquals("nova issue", dtoCapturado.title)
        Assertions.assertEquals("o projeto deve ser iniciado", dtoCapturado.body)
        Assertions.assertEquals(IssueStatusEnum.OPEN, dtoCapturado.state)
        Assertions.assertEquals(1L, dtoCapturado.number)
        Assertions.assertEquals(388968361, dtoCapturado.idGitHub)
    }

}