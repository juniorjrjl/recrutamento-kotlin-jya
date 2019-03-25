import br.com.recrutamento.config.AppConfig
import br.com.recrutamento.config.InjectConfig
import br.com.recrutamento.controller.EventController
import br.com.recrutamento.service.EventService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.json.JavalinJackson
import org.flywaydb.core.Flyway

fun main(args: Array<String>){
    val flyway = Flyway.configure().dataSource(AppConfig.buscarPropriedade("exposed.url"), AppConfig.buscarPropriedade("exposed.user"), AppConfig.buscarPropriedade("exposed.password")).load()
    flyway.migrate()
    EventController.eventService = EventService(InjectConfig.dependenciasEventService())
    JavalinJackson.configure(jacksonObjectMapper().findAndRegisterModules())
    val app = Javalin.create().start(AppConfig.buscarPropriedadeInt("javalin.port"))
    app.routes{
        path("events/:number/events"){
            get(EventController::getIssue)
        }
        path("webhook"){
            post(EventController::salvar)
        }
    }
}