package br.com.recrutamento.dto

import java.time.LocalDateTime

data class IssueAtualizacaoDTO (
    val idGitHub: Long,
    val title: String,
    val body: String,
    val state: IssueStatusEnum,
    val updatedAt: LocalDateTime
)