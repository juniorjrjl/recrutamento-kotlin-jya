package br.com.recrutamento.db.util

import br.com.recrutamento.dto.IssueStatusEnum
import org.joda.time.DateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class ConversaoDados {

    companion object {
        fun gerarLocalDateTime(data: DateTime?): LocalDateTime? = if (data != null) Instant.ofEpochMilli(data.millis).atZone(ZoneOffset.UTC).toLocalDateTime() else null
        fun gerarStatusIssue(codigo: Int): IssueStatusEnum = if ((codigo>= 0) && (codigo <=1)) IssueStatusEnum.values()[codigo] else IssueStatusEnum.OPEN
        fun gerarDateTime(data: LocalDateTime): DateTime = DateTime(data.toInstant(ZoneOffset.UTC).toEpochMilli())
    }

}