package db.migration

import org.apache.commons.lang3.exception.ExceptionUtils
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.internal.jdbc.JdbcTemplate
import org.slf4j.LoggerFactory

class V201903251426__CriacaoTabelasProjeto: BaseJavaMigration() {

    private val log = LoggerFactory.getLogger(V201903251426__CriacaoTabelasProjeto::class.java)

    val TABELA_ISSUES = "CREATE TABLE issues (\r\n" +
                        "    id BIGSERIAL PRIMARY KEY,\r\n" +
                        "    number BIGINT NULL,\r\n" +
                        "    title VARCHAR(255) NULL,\r\n" +
                        "    state SMALLINT NULL CHECK(state >= 0 and state <= 1),\r\n" +
                        "    body VARCHAR(255) NULL,\r\n" +
                        "    created_at TIMESTAMP NULL,\r\n" +
                        "    updated_at TIMESTAMP NULL,\r\n" +
                        "    closed_at TIMESTAMP NULL,\r\n" +
                        "    user_name VARCHAR(39) NOT NULL,\r\n" +
                        "    id_git_hub BIGINT NULL\r\n" +
                        ");\r\n"

    val TABELA_COMENTARIOS = "CREATE TABLE \"comments\" (\r\n" +
                             "    id BIGSERIAL PRIMARY KEY,\r\n" +
                             "    user_name VARCHAR(39) NOT NULL,\r\n" +
                             "    body VARCHAR(255) NULL,\r\n" +
                             "    created_at TIMESTAMP NULL,\r\n" +
                             "    updated_at TIMESTAMP NULL,\r\n" +
                             "    id_issue BIGINT NULL,\r\n" +
                             "    id_git_hub BIGINT NULL\n" +
                             ");\r\n"

    val UNIQUE_KEY_ISSUES = "ALTER TABLE issues ADD CONSTRAINT uk_id_github_issues UNIQUE (id_git_hub)"

    val UNIQUE_KEY_COMENTARIOS = """ALTER TABLE "comments" ADD CONSTRAINT fk_comments_id_issue_id FOREIGN KEY (id_issue) REFERENCES "comments"(id) ON UPDATE CASCADE"""

    override fun migrate(context: Context?) {
        try {
            val template = JdbcTemplate(context!!.connection)
            template.execute(TABELA_ISSUES)
            log.info("criando tabela de issues")
            template.execute(TABELA_COMENTARIOS)
            log.info("criando tabela de comentarios")
            template.execute(UNIQUE_KEY_ISSUES)
            log.info("criando UK issues")
            template.execute(UNIQUE_KEY_COMENTARIOS)
            log.info("criando UK comentarios")
        }catch (e: Exception){
            log.error("erro ao rodar migracao")
            log.error(ExceptionUtils.getStackTrace(e))
        }
    }
}