import br.com.recrutamento.config.AppConfig
import br.com.recrutamento.config.InjectConfig
import br.com.recrutamento.config.ServerConfig
import br.com.recrutamento.controller.EventController
import br.com.recrutamento.service.EventService
import org.flywaydb.core.Flyway

fun main(args: Array<String>){
    try {
        val flyway = Flyway.configure().dataSource(
            AppConfig.buscarPropriedade("exposed.url"),
            AppConfig.buscarPropriedade("exposed.user"),
            AppConfig.buscarPropriedade("exposed.password")
        ).load()
        flyway.migrate()
        EventController.eventService = EventService(InjectConfig.dependenciasEventService())
        ServerConfig.iniciarServidor()
    }catch (e: Exception){
        ServerConfig.pararServidor()
        e.printStackTrace()
    }
}