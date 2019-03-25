package db.migration

import br.com.recrutamento.config.AppConfig
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

class `verificando a migração do flyway` {

    @Test
    fun`rodando a migração para validação`(){
        assertDoesNotThrow{
            val flyway = Flyway.configure().dataSource(
                AppConfig.buscarPropriedade("exposed.url"),
                AppConfig.buscarPropriedade("exposed.user"),
                AppConfig.buscarPropriedade("exposed.password")
            ).load()
            flyway.migrate()
        }
    }

}