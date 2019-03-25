package br.com.recrutamento.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class CommentDetalhadoDTO (
    val userName: String,
    val body: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val createdAt: LocalDateTime?,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val updatedAt: LocalDateTime?
)