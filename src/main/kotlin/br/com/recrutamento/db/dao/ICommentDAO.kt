package br.com.recrutamento.db.dao

import br.com.recrutamento.dto.CommentAtualizarDTO
import br.com.recrutamento.dto.CommentCadastroDTO
import br.com.recrutamento.dto.CommentDetalhadoDTO

interface ICommentDAO {

    fun cadastrar(dto: CommentCadastroDTO)

    fun atualizar(dto: CommentAtualizarDTO)

    fun excluirPorIdGitHub(idGitHub: Long)

    fun excluirCommentsIssue(idIssue: Long)

    fun buscarCommentsIssue(idIssue: Long): List<CommentDetalhadoDTO>

}