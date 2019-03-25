package br.com.recrutamento.db.dao.impl

import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.db.table.Issues
import br.com.recrutamento.db.util.ConversaoDados
import br.com.recrutamento.dto.IssueAtualizacaoDTO
import br.com.recrutamento.dto.IssueCadastroDTO
import br.com.recrutamento.dto.IssueDetalhadaDTO
import br.com.recrutamento.dto.IssueStatusEnum
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import java.time.LocalDateTime

class IssueDAOImpl  : IIssueDAO {

    override fun cadastrar(dto: IssueCadastroDTO){
        Issues.insert {
            it[number] = dto.number
            it[title] = dto.title
            it[body] = dto.body
            it[state] = dto.state.id
            it[createdAt] = ConversaoDados.gerarDateTime(dto.createdAt)
            it[updatedAt] = ConversaoDados.gerarDateTime(dto.updatedAt)
            it[closedAt] = ConversaoDados.gerarDateTime(dto.closedAt)
            it[userName] = dto.userName?:""
            it[idGitHub] = dto.idGitHub
        }
    }

    override fun atualizar(dto: IssueAtualizacaoDTO){
        Issues.update({Issues.idGitHub eq dto.idGitHub}){
            it[Issues.title] = dto.title
            it[Issues.body] = dto.body
            it[Issues.state] = dto.state.id
            it[Issues.updatedAt] = ConversaoDados.gerarDateTime(dto.updatedAt)
        }
    }

    override fun atualizarDataFechamento(closedAt: LocalDateTime?, status: IssueStatusEnum, idGitHub: Long){
        Issues.update({Issues.idGitHub eq idGitHub}){
            it[Issues.closedAt] = ConversaoDados.gerarDateTime(closedAt)
            it[Issues.state] = status.id
        }
    }

    override fun atualizarDataAtualizacao(updatedAt: LocalDateTime, status: IssueStatusEnum, idGitHub: Long){
        Issues.update({Issues.idGitHub eq idGitHub}){
            it[Issues.updatedAt] = ConversaoDados.gerarDateTime(updatedAt)
            it[Issues.state] = status.id
        }
    }

    override fun excluirPorIdGitHub(idGitHub: Long){
        Issues.deleteWhere { Issues.idGitHub eq idGitHub}
    }

    override fun buscarDetalhes(number: Long): IssueDetalhadaDTO?{
        var dto : IssueDetalhadaDTO? = null
        try {
            val retorno: Pair<Pair<Pair<Pair<Pair<Pair<Pair<Pair<Long, Long?>, String?>, String?>, Int?>, DateTime?>, DateTime?>, DateTime?>, String?>
            retorno = Issues.slice(
                Issues.id,
                Issues.number,
                Issues.title,
                Issues.body,
                Issues.state,
                Issues.createdAt,
                Issues.updatedAt,
                Issues.closedAt,
                Issues.userName
            )
                .select { Issues.number eq number }
                .map {
                    it[Issues.id] to
                    it[Issues.number] to
                    it[Issues.title] to
                    it[Issues.body] to
                    it[Issues.state] to
                    it[Issues.createdAt] to
                    it[Issues.updatedAt] to
                    it[Issues.closedAt] to
                    it[Issues.userName]
                }.single()
            dto = IssueDetalhadaDTO(
                retorno.first.first.first.first.first.first.first.first,
                retorno.first.first.first.first.first.first.first.second ?: 0,
                retorno.first.first.first.first.first.first.second ?: "",
                retorno.first.first.first.first.first.second ?: "",
                ConversaoDados.gerarStatusIssue(retorno.first.first.first.first.second ?: 1),
                ConversaoDados.gerarLocalDateTime(retorno.first.first.first.second),
                ConversaoDados.gerarLocalDateTime(retorno.first.first.second),
                ConversaoDados.gerarLocalDateTime(retorno.first.second),
                retorno.second
            )
        }catch (ex: NoSuchElementException){ }
        return dto
    }

    override fun buscarIdIssue(idGitHub: Long): Long? {
        var retorno: Long? = null
        try {
            retorno = Issues.slice(Issues.id)
                .select { Issues.idGitHub eq idGitHub }
                .map { it[Issues.id] }
                .single()
        }catch (ex: NoSuchElementException){ }
        return retorno
    }

}