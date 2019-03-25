package br.com.recrutamento.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CommentCadastroDTO(
    var body: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    @JsonProperty("id")
    val idGitHub: Long
){
    @field:Size(min = 1, max = 39)
    @field:NotBlank
    var userName: String? = null
    var idIssue: Long? = 0L
}