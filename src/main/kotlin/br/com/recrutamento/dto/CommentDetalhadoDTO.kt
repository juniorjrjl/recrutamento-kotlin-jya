package br.com.recrutamento.dto

import java.time.LocalDateTime

data class CommentDetalhadoDTO (
    val userName: String,
    val body: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)