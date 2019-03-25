package br.com.recrutamento.db.dao

import br.com.recrutamento.dto.IssueAtualizacaoDTO
import br.com.recrutamento.dto.IssueCadastroDTO
import br.com.recrutamento.dto.IssueDetalhadaDTO
import br.com.recrutamento.dto.IssueStatusEnum
import java.time.LocalDateTime

interface IIssueDAO {

    fun cadastrar(dto: IssueCadastroDTO)

    fun atualizar(dto: IssueAtualizacaoDTO)

    fun atualizarDataFechamento(closedAt: LocalDateTime?, status: IssueStatusEnum, idGitHub: Long)

    fun atualizarDataAtualizacao(updatedAt: LocalDateTime, status: IssueStatusEnum, idGitHub: Long)

    fun excluirPorIdGitHub(idGitHub: Long)

    fun buscarDetalhes(number: Long): IssueDetalhadaDTO?

    fun buscarIdIssue(idGitHub: Long): Long?

}