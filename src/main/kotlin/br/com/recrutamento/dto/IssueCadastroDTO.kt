package br.com.recrutamento.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class IssueCadastroDTO (
    var number: Long,
    var title: String,
    var body: String,
    var state: IssueStatusEnum,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var closedAt: LocalDateTime?,
    @JsonProperty("id")
    var idGitHub: Long
){
    @field:Size(min = 1, max = 39)
    @field:NotBlank
    var userName: String? = null
}