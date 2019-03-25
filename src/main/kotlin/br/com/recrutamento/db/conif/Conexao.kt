package br.com.recrutamento.db.conif

import br.com.recrutamento.config.AppConfig
import org.jetbrains.exposed.sql.Database

class Conexao {

    companion object {
        fun criarConexao(): Database{
            return Database.connect(
                AppConfig.buscarPropriedade("exposed.url"),
                AppConfig.buscarPropriedade("exposed.driver"), AppConfig.buscarPropriedade("exposed.user"), AppConfig.buscarPropriedade("exposed.password"))
        }
    }

}