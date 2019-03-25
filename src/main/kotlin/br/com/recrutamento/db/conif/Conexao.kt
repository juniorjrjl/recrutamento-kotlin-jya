package br.com.recrutamento.db.conif

import br.com.recrutamento.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class Conexao {

    companion object {

        private fun hikari(): HikariDataSource {
            val config = HikariConfig()
            config.driverClassName = AppConfig.buscarPropriedade("exposed.driver")
            config.jdbcUrl = AppConfig.buscarPropriedade("exposed.url")
            config.maximumPoolSize = AppConfig.buscarPropriedadeInt("exposed.pool_size")
            config.isAutoCommit = false
            config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            config.username = AppConfig.buscarPropriedade("exposed.user")
            config.password = AppConfig.buscarPropriedade("exposed.password")
            config.validate()
            return HikariDataSource(config)
        }

        fun criarConexao(): Database{
            return Database.connect(hikari())
        }
    }

}