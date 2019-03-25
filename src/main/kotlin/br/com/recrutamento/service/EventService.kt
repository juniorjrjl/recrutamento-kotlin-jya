package br.com.recrutamento.service

import br.com.recrutamento.config.MessageConfig
import br.com.recrutamento.db.conif.Conexao
import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.dto.*
import br.com.recrutamento.exception.EventServiceException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import javax.validation.ConstraintViolation
import javax.validation.Validator

class EventService (private val kodein: Kodein){

    private val INICIO_ISSUE = """"issue":"""
    private val FINAL_ISSUE = """"repository":{"""
    private val INICIO_COMMENT = """"comment":"""

    private val CRIACAO_ISSUE = """{"action":"created","""
    private val EDICAO_ISSUE = """{"action":"edited","""
    private val ABERTURA_ISSUE = """{"action":"opened","""
    private val EVENTO_EXCLUSAO = """{"action":"deleted","""
    private val FECHAMENTO_ISSUE = """{"action":"closed","""
    private val REABERTURA_ISSUE = """{"action":"reopened","""

    private val issueDAO: IIssueDAO = kodein.instance()

    private val commentDAO: ICommentDAO = kodein.instance()

    private val validator: Validator = kodein.instance()

    private val mapper = jacksonObjectMapper()

    init {
        mapper.findAndRegisterModules()
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        mapper.propertyNamingStrategy =PropertyNamingStrategy.SNAKE_CASE
        mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun salvar(jsonEvento: String?){
        if (jsonEvento.isNullOrBlank() || (!jsonValido(jsonEvento))) {
            throw EventServiceException("O json informado está inválido.")
        }
        if (jsonEvento.contains(INICIO_ISSUE)){
            if ((jsonEvento.startsWith(CRIACAO_ISSUE)) || (jsonEvento.startsWith(ABERTURA_ISSUE))){
                salvarIssue(jsonEvento)
            }else if (jsonEvento.startsWith(EDICAO_ISSUE)){
                if (jsonEvento.contains(INICIO_COMMENT)){
                    val dto = montarComment(jsonEvento)
                    commentDAO.atualizar(CommentAtualizarDTO(dto!!.body, dto.updatedAt, dto.idGitHub))
                }else{
                    val dto = montarIssue(jsonEvento)
                    Conexao.criarConexao()
                    transaction {
                        issueDAO.atualizar(IssueAtualizacaoDTO(dto!!.idGitHub, dto.title, dto.body, dto.state, dto.updatedAt!!)
                        )
                    }
                }
            }else if(jsonEvento.startsWith(EVENTO_EXCLUSAO)){
                if (jsonEvento.contains(INICIO_COMMENT)){
                    val dto = montarComment(jsonEvento)
                    Conexao.criarConexao()
                    transaction {
                        commentDAO.excluirPorIdGitHub(dto!!.idGitHub)
                    }
                }else{
                    val dto = montarIssue(jsonEvento)
                    Conexao.criarConexao()
                    transaction {
                        issueDAO.excluirPorIdGitHub(dto!!.idGitHub)
                        commentDAO.excluirCommentsIssue(issueDAO.buscarIdIssue(dto.idGitHub) ?: 0L)
                    }
                }
            }else if ((jsonEvento.startsWith(FECHAMENTO_ISSUE)) || (jsonEvento.startsWith(REABERTURA_ISSUE))){
                val dto = montarIssue(jsonEvento)
                Conexao.criarConexao()
                transaction {
                    issueDAO.atualizarDataFechamento(dto!!.closedAt!!, dto.state, dto.idGitHub)
                }
            }else{
                throw EventServiceException("Evento não implementado.")
            }
        }else{
            throw EventServiceException("Evento não implementado.")
        }
    }

    private fun salvarIssue(json: String){
        val dtoGerado = montarIssue(json)
        val dtoCadastrado = issueDAO.buscarDetalhes(dtoGerado!!.number)
        if (dtoCadastrado != null){
            Conexao.criarConexao()
            transaction {
                issueDAO.atualizarDataAtualizacao(dtoGerado.updatedAt!!, dtoGerado.state, dtoGerado.idGitHub)
                salvarComment(json, dtoCadastrado.id)
            }
        }else{
            val violacoes : Set<ConstraintViolation<IssueCadastroDTO>> = validator.validate(dtoGerado)
            val mensagens = StringBuilder()
            if (violacoes.any()){
                violacoes.forEach{
                    mensagens.append(MessageConfig.montarMensagensConstraints(dtoGerado.javaClass.simpleName + "."
                            + it.propertyPath.toString(), (it.constraintDescriptor as ConstraintDescriptorImpl).annotationDescriptor.attributes))
                    mensagens.append(";")
                    mensagens.append("\r\n")
                }
                mensagens.setLength( mensagens.length -1)
                throw EventServiceException("Erro ao salvar a Issue: $mensagens")
            }
            Conexao.criarConexao()
            transaction {
                issueDAO.cadastrar(dtoGerado)
            }
        }
    }

    private fun salvarComment(json: String, idIssue: Long){
        val dtoGerado = montarComment(json)
        dtoGerado!!.idIssue = idIssue
        val violacoes : Set<ConstraintViolation<CommentCadastroDTO>> = validator.validate(dtoGerado)
        val mensagens = StringBuilder()
        if (violacoes.any()){
            violacoes.forEach{
                mensagens.append(MessageConfig.montarMensagensConstraints(dtoGerado.javaClass.simpleName + "."
                        + it.propertyPath.toString(), (it.constraintDescriptor as ConstraintDescriptorImpl).annotationDescriptor.attributes))
                mensagens.append(";")
                mensagens.append("\r\n")
            }
            mensagens.setLength( mensagens.length -1)
            throw EventServiceException("Erro ao salvar o Comentário: $mensagens")
        }
        Conexao.criarConexao()
        transaction {
            commentDAO.cadastrar(dtoGerado)
        }
    }

    private fun montarIssue(json: String): IssueCadastroDTO?{
        var jsonIssue = buscarJsonInterno(json, INICIO_ISSUE, FINAL_ISSUE)
        val jsonUsuario = jsonIssue.substring(jsonIssue.indexOf(""""user":""") + """"user":""".length, jsonIssue.indexOf("""},"labels":""") + 1)
        jsonIssue = jsonIssue.replace(""""user":$jsonUsuario,""" , "")
        val dto : IssueCadastroDTO?
        try{
            dto = mapper.readValue<IssueCadastroDTO>(jsonIssue)
            val mapaUsuario :Map<String, String> = mapper.readValue(jsonUsuario, object: TypeReference<Map<String, String>>(){})
            dto.userName = mapaUsuario["login"]
        }catch (e: Exception){
            throw e
        }
        return dto
    }

    private fun montarComment(json: String): CommentCadastroDTO?{
        var jsonComment = buscarJsonInterno(json, INICIO_COMMENT, FINAL_ISSUE)
        val jsonUsuario = jsonComment.substring(jsonComment.indexOf(""""user":""") + """"user":""".length, jsonComment.indexOf("""},"created_at":""") + 1)
        jsonComment = jsonComment.replace(""""user":$jsonUsuario,""" , "")
        val dto: CommentCadastroDTO?
        try{
            dto = mapper.readValue(jsonComment, CommentCadastroDTO::class.java)
            val mapaUsuario :Map<String, String> = mapper.readValue(jsonUsuario, object: TypeReference<Map<String, String>>(){})
            dto.userName = mapaUsuario["login"]
        }catch (e: Exception){
            throw e
        }
        return dto
    }

    private fun buscarJsonInterno(json: String, inicio: String, fim: String): String{
        var indicieInicial = json.indexOf(inicio)
        var indicieFinal = json.indexOf(fim)
        if ((indicieFinal < 0) || (indicieFinal < 0)){
            throw EventServiceException("O json informado não é conhecido pela aplicação.")
        }
        indicieInicial += inicio.length
        indicieFinal += -1
        return json.substring(json.indexOf(inicio) + inicio.length, json.indexOf(fim) -1)
    }

    private fun jsonValido(json: String): Boolean{
        var valido = true
        try{
            mapper.readTree(json)
        }catch (ex: Exception){
            valido = false
        }
        return valido
    }

    fun buscarIssue(number: Long): IssueDetalhadaDTO?{
        var dto: IssueDetalhadaDTO? = null
        try{
            Conexao.criarConexao()
            transaction {
                dto = issueDAO.buscarDetalhes(number)
                if (dto != null){
                    dto!!.comments = commentDAO.buscarCommentsIssue(dto!!.id)
                }
            }
        }catch (e: Exception){
            throw e
        }
        return dto
    }

}