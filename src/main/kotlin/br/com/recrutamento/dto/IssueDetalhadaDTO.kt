package br.com.recrutamento.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

class IssueDetalhadaDTO(
    val id: Long,
    val number: Long,
    val title: String,
    val body: String,
    val state: IssueStatusEnum,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val createdAt: LocalDateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val updatedAt: LocalDateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val closedAt: LocalDateTime?,
    val userName: String
){

    var comments: List<CommentDetalhadoDTO> = emptyList()
}