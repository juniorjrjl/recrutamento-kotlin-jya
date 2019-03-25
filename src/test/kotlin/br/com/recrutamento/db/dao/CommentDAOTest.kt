package br.com.recrutamento.db.dao

import br.com.recrutamento.db.conif.Conexao
import br.com.recrutamento.db.dao.impl.CommentDAOImpl
import br.com.recrutamento.db.dao.impl.IssueDAOImpl
import br.com.recrutamento.db.table.Comments
import br.com.recrutamento.db.table.Issues
import br.com.recrutamento.dto.CommentAtualizarDTO
import br.com.recrutamento.dto.CommentCadastroDTO
import br.com.recrutamento.dto.IssueCadastroDTO
import br.com.recrutamento.dto.IssueStatusEnum
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class `operações de banco com o Comment` {

    val comentarioDAOImpl: CommentDAOImpl = CommentDAOImpl()
    val issueDAOImpl: IssueDAOImpl = IssueDAOImpl()

    @BeforeEach
    fun `setUp`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Issues, Comments)
            val createAt = LocalDateTime.now()
            val updatedAt = createAt.plusDays(1)
            val closedAt = createAt.plusMonths(1)
            val dtoCadastro = IssueCadastroDTO(1L,"title_test", "body_test", IssueStatusEnum.CLOSED,
                createAt, updatedAt, closedAt, 1L)
            dtoCadastro.userName = "user_test"
            issueDAOImpl.cadastrar(dtoCadastro)
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
    fun `verificando cadastro de comentários`(){
        val createAt = LocalDateTime.now()
        val updatedAt = createAt.plusDays(1)
        val dtoCadastro = CommentCadastroDTO("body", createAt, updatedAt, 1L)
        dtoCadastro.userName = "username"
        dtoCadastro.idIssue = 1L
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            comentarioDAOImpl.cadastrar(dtoCadastro)
            val dtoRetorno = comentarioDAOImpl.buscarCommentsIssue(1L)
            assertEquals(dtoCadastro.userName, dtoRetorno[0].userName)
            assertEquals(dtoCadastro.body, dtoRetorno[0].body)
            assertEquals(dtoCadastro.createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno[0].createdAt!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
            assertEquals(dtoCadastro.updatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno[0].updatedAt!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))

        }
    }

    @Test
    fun `verificando se a listagem de Comments está ordenada pela data`(){
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            for(i in 1..10){
                val createAt = if (i%2 ==0) LocalDateTime.now().plusDays(i.toLong()) else LocalDateTime.now().plusDays((i * -1).toLong())
                val dtoCadastro = CommentCadastroDTO("body", createAt, LocalDateTime.now(), i.toLong())
                dtoCadastro.userName = "userName"
                dtoCadastro.idIssue = 1
                comentarioDAOImpl.cadastrar(dtoCadastro)
            }
            val dtoRetorno = comentarioDAOImpl.buscarCommentsIssue(1L)
            val listaOrdenataDataCriacaao = dtoRetorno.sortedBy { it.createdAt }.reversed()
            assertEquals(listaOrdenataDataCriacaao, dtoRetorno)
        }
    }

    @Test
    fun `verificando atualização comentários`(){
        val body = "novo corpo"
        val updatedAt = LocalDateTime.now().plusHours(13)
        val dtoCadastro = CommentCadastroDTO("body", LocalDateTime.now(), updatedAt, 1L)
        dtoCadastro.userName = "username"
        dtoCadastro.idIssue = 1L
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            comentarioDAOImpl.cadastrar(dtoCadastro)
            comentarioDAOImpl.atualizar(CommentAtualizarDTO(body, updatedAt, 1L))
            val dtoRetorno = comentarioDAOImpl.buscarCommentsIssue(1L)
            assertEquals(body, dtoRetorno[0].body)
            assertEquals(updatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")), dtoRetorno[0].updatedAt!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
        }
    }

    @Test
    fun `verificando exclusão por IdGitHub`(){
        val updatedAt = LocalDateTime.now().plusHours(13)
        val dtoCadastro = CommentCadastroDTO("body", LocalDateTime.now(), updatedAt, 1L)
        dtoCadastro.userName = "username"
        dtoCadastro.idIssue = 1L
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            comentarioDAOImpl.cadastrar(dtoCadastro)
            comentarioDAOImpl.excluirPorIdGitHub(1L)
            assertTrue(comentarioDAOImpl.buscarCommentsIssue(1L).isEmpty())
        }
    }

    @Test
    fun `verificando exclusão por IdIssue`(){
        val updatedAt = LocalDateTime.now().plusHours(13)
        Conexao.criarConexao()
        transaction {
            addLogger(StdOutSqlLogger)
            for(i in 1 ..15){
                val dtoCadastro = CommentCadastroDTO("body", LocalDateTime.now(), updatedAt, i.toLong())
                dtoCadastro.userName = "username"
                dtoCadastro.idIssue = 1L
                comentarioDAOImpl.cadastrar(dtoCadastro)
            }
            comentarioDAOImpl.excluirCommentsIssue(1L)
            assertTrue(comentarioDAOImpl.buscarCommentsIssue(1L).isEmpty())
        }
    }

}