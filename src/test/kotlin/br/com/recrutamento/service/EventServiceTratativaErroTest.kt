package br.com.recrutamento.service

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.dto.IssueDetalhadaDTO
import br.com.recrutamento.dto.IssueStatusEnum
import br.com.recrutamento.exception.EventServiceException
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.lang.Exception
import java.time.LocalDateTime
import java.util.stream.Stream
import javax.validation.Validation
import javax.validation.Validator

class EventServiceTratativaErroTest {

    private val issueDAOMock : IIssueDAO = mock()
    private val commentDAOMock: ICommentDAO = mock()
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    private val kodein : Kodein = Kodein{
        bind<IIssueDAO>() with singleton { issueDAOMock }
        bind<ICommentDAO>() with singleton { commentDAOMock }
        bind<Validator>() with  singleton { validator }
    }

    private val eventService = EventService(kodein)

    companion object {
        @JvmStatic
        fun jsonsInvalidos(): Stream<String> = Stream.of("", "    ", "{", """"issue":""", "{}")
    }

    @ParameterizedTest
    @MethodSource("jsonsInvalidos")
    fun `verificando envio de jsons inválidos`(json: String){
        assertThrows<EventServiceException> { eventService.salvar(json) }
    }

    @Test
    fun `verificando envio de json nulo`(){
        assertThrows<EventServiceException> { eventService.salvar(null) }
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, -1L])
    fun `simulando lancamento de exception na busca de Issues`(idIssue: Long){
        given(issueDAOMock.buscarDetalhes(0)).willAnswer{throw Exception() }
        whenever(issueDAOMock.buscarDetalhes(- 1))
            .thenReturn(
                IssueDetalhadaDTO(-1, -1, "title", "body", IssueStatusEnum.OPEN,
                    LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "userName")
            )
        given(commentDAOMock.buscarCommentsIssue(any())).willAnswer{throw Exception() }
        assertThrows<Exception> { eventService.buscarIssue(idIssue) }
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/IssueUserNameViolation.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando violação de constraint da issue`(json: String){
        assertThrows<EventServiceException> { eventService.salvar(json) }
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/csv/CommentUserNameViolation.csv"], delimiter = '#', numLinesToSkip = 1)
    fun `verificando violação de constraint do comentário`(json: String){
        whenever(issueDAOMock.buscarDetalhes(1L))
            .thenReturn(
                IssueDetalhadaDTO(1L, 1L, "title", "body", IssueStatusEnum.OPEN,
                    LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "username")
            )
        assertThrows<EventServiceException> { eventService.salvar(json) }
    }
}