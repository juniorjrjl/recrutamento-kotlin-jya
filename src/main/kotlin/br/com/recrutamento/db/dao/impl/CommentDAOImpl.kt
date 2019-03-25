package br.com.recrutamento.db.dao.impl

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.table.Comments
import br.com.recrutamento.db.util.ConversaoDados
import br.com.recrutamento.dto.CommentAtualizarDTO
import br.com.recrutamento.dto.CommentCadastroDTO
import br.com.recrutamento.dto.CommentDetalhadoDTO
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import java.time.ZoneOffset

class CommentDAOImpl : ICommentDAO {

    override fun cadastrar(dto: CommentCadastroDTO){
        Comments.insert {
            it[Comments.userName] = dto.userName?:""
            it[Comments.body] = dto.body
            it[Comments.createdAt] = ConversaoDados.gerarDateTime(dto.createdAt)
            it[Comments.updatedAt] = ConversaoDados.gerarDateTime(dto.updatedAt)
            it[Comments.idIssue] = dto.idIssue
            it[Comments.idGitHub] = dto.idGitHub
        }
    }

    override fun atualizar(dto: CommentAtualizarDTO){
        Comments.update({Comments.idGitHub eq dto.idGitHub}){
            it[Comments.body] = dto.body
            it[Comments.updatedAt] = DateTime(dto.updatedAt.toInstant(ZoneOffset.UTC).toEpochMilli())
        }
    }

    override fun excluirPorIdGitHub(idGitHub: Long) { Comments.deleteWhere { Comments.idGitHub eq idGitHub } }

    override fun excluirCommentsIssue(idIssue: Long) { Comments.deleteWhere { Comments.idIssue eq idIssue } }

    override fun buscarCommentsIssue(idIssue: Long): List<CommentDetalhadoDTO>{
        var dto: MutableList<CommentDetalhadoDTO> = mutableListOf()
        val retorno = Comments.slice(Comments.userName, Comments.body, Comments.createdAt, Comments.updatedAt)
            .select { Comments.idIssue eq idIssue }
            .orderBy(Comments.createdAt to SortOrder.DESC)
        .map { it[Comments.userName] to it[Comments.body] to it[Comments.createdAt] to it[Comments.updatedAt]
            }
        if (retorno.isEmpty()){
            dto = mutableListOf<CommentDetalhadoDTO>()
        }else{
            retorno.forEach {
                dto.add(CommentDetalhadoDTO(it.first.first.first,
                    it.first.first.second ?: "",
                    ConversaoDados.gerarLocalDateTime(it.first.second),
                    ConversaoDados.gerarLocalDateTime(it.second)))
            }
        }
        return dto.toList()
    }

}