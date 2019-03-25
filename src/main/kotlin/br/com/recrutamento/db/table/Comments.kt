package br.com.recrutamento.db.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Comments : Table() {
    val id = long("id").autoIncrement().primaryKey()
    val userName = varchar("user_name", 39, null)
    val body = varchar("body", 255, null).nullable()
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
    val idIssue = long("id_issue").references(Comments.id, ReferenceOption.NO_ACTION, ReferenceOption.CASCADE).nullable()
    val idGitHub = long("id_git_hub").uniqueIndex("uk_id_github_comments").nullable()
}