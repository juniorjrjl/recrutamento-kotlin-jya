package br.com.recrutamento.dto

import java.time.LocalDateTime

data class CommentAtualizarDTO(
    val body: String,
    val updatedAt: LocalDateTime,
    val idGitHub: Long
)