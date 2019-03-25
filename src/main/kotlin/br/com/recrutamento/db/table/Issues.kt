package br.com.recrutamento.db.table

import org.jetbrains.exposed.sql.Table

object Issues: Table() {

    val id = long("id").autoIncrement().primaryKey()
    val number = long("number").nullable()
    val title = varchar("title", 255, null).nullable()
    val state = integer("state").nullable()
    val body = varchar("body", 255, null).nullable()
    val createdAt = datetime("created_at").nullable()
    val updatedAt = datetime("updated_at").nullable()
    val closedAt = datetime("closed_at").nullable()
    val userName = varchar("user_name", 39, null)
    val idGitHub = long("id_git_hub").uniqueIndex("uk_id_github_issues").nullable()

}