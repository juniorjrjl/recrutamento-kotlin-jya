package br.com.recrutamento.db.dao

import br.com.recrutamento.db.conif.Conexao
import br.com.recrutamento.db.dao.impl.IssueDAOImpl
import br.com.recrutamento.db.table.Comments
import br.com.recrutamento.db.table.Issues
import br.com.recrutamento.dto.IssueAtualizacaoDTO
import br.com.recrutamento.dto.IssueCadastroDTO
import br.com.recrutamento.dto.IssueStatusEnum
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class `operações de banco da Issue` {

    private val daoImpl: IssueDAOImpl = IssueDAOImpl()

    @BeforeEach
    fun `setUp`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Issues, Comments)
        }
    }

    @AfterEach
    fun `tearDown`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(Issues, Comments)
        }
    }

    @Test
    fun `quando informar nome de usuário vazio disparar exeption`(){
        val createAt = LocalDateTime.now()
        val updatedAt = createAt.plusDays(1)
        val closedAt = createAt.plusMonths(1)
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            val dtoCadastro = IssueCadastroDTO(1L,"title_test", "body_test", IssueStatusEnum.CLOSED,
                createAt, updatedAt, closedAt,1L)
            dtoCadastro.userName = "user_test"
            daoImpl.cadastrar(dtoCadastro)
            val dtoRetorno = daoImpl.buscarDetalhes(1L)!!
            assertEquals(dtoCadastro.number, dtoRetorno.number)
            assertEquals(dtoCadastro.title, dtoRetorno.title)
            assertEquals(dtoCadastro.body, dtoRetorno.body)
            assertEquals(dtoCadastro.state, dtoRetorno.state)
            assertEquals(dtoCadastro.createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.createdAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
            assertEquals(dtoCadastro.updatedAt!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.updatedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
            assertEquals(dtoCadastro.closedAt!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.closedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
            assertEquals(dtoCadastro.userName!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.userName.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
        }

    }

    @Test
    fun `verificando atualização de Issue`(){
        val title = "new title"
        val body = "new body"
        val updatedAt = LocalDateTime.now().plusYears(50)
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            val dtoCadastro = IssueCadastroDTO(1L,"title_test", "body_test", IssueStatusEnum.CLOSED,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),1L)
            dtoCadastro.userName = "user_test"
            daoImpl.cadastrar(dtoCadastro)
            daoImpl.atualizar(IssueAtualizacaoDTO(1L, title, body, IssueStatusEnum.OPEN, updatedAt))
            val dtoRetorno = daoImpl.buscarDetalhes(1L)!!
            assertEquals(title, dtoRetorno.title)
            assertEquals(body, dtoRetorno.body)
            assertEquals(IssueStatusEnum.OPEN, dtoRetorno.state)
            assertEquals(updatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.updatedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
        }
    }

    @Test
    fun `atualizando data de fechamento`(){
        val closedAt = LocalDateTime.now().plusMonths(2)
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            val dtoCadastro = IssueCadastroDTO(1L,"title_test", "body_test", IssueStatusEnum.OPEN,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),1L)
            dtoCadastro.userName = "user_test"
            daoImpl.cadastrar(dtoCadastro)
            daoImpl.atualizarDataFechamento(closedAt, IssueStatusEnum.CLOSED, 1L)
            val dtoRetorno = daoImpl.buscarDetalhes(1L)!!
            assertEquals(IssueStatusEnum.CLOSED, dtoRetorno.state)
            assertEquals(closedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.closedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
        }
    }

    @Test
    fun `atualizando data de atualizacao`(){
        val updatedAt = LocalDateTime.now().plusDays(5)
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            val dtoCadastro = IssueCadastroDTO(1L,"title_test", "body_test", IssueStatusEnum.OPEN,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 1L)
            dtoCadastro.userName = "user_test"
            daoImpl.cadastrar(dtoCadastro)
            daoImpl.atualizarDataAtualizacao(updatedAt, IssueStatusEnum.CLOSED, 1L)
            val dtoRetorno = daoImpl.buscarDetalhes(1L)!!
            assertEquals(IssueStatusEnum.CLOSED, dtoRetorno.state)
            assertEquals(updatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno.updatedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
        }
    }

    @Test
    fun `excluindo Issue por IdGitHub`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            val dtoCadastro = IssueCadastroDTO(
                1L, "title_test", "body_test", IssueStatusEnum.OPEN,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),  1L
            )
            dtoCadastro.userName = "user_test"
            daoImpl.cadastrar(dtoCadastro)
            daoImpl.excluirPorIdGitHub(1L)
            assertNull(daoImpl.buscarDetalhes(1))
        }
    }

    @Test
    fun `buscar id Issue cadastradao`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            val dtoCadastro = IssueCadastroDTO(
                1L, "title_test", "body_test", IssueStatusEnum.OPEN,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),  1L
            )
            dtoCadastro.userName = "user_test"
            daoImpl.cadastrar(dtoCadastro)
            val idIssue = daoImpl.buscarIdIssue(dtoCadastro.idGitHub)
            assertNotNull(idIssue)
        }
    }

    @Test
    fun `quando IdGitHub inexistente retornar null`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            assertNull(daoImpl.buscarIdIssue(10L))
        }
    }

}