import br.com.recrutamento.config.AppConfig
import br.com.recrutamento.config.InjectConfig
import br.com.recrutamento.controller.EventController
import br.com.recrutamento.db.conif.Conexao
import br.com.recrutamento.db.table.Comments
import br.com.recrutamento.db.table.Issues
import br.com.recrutamento.service.EventService
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.json.JavalinJson
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>){
    Conexao.criarConexao()
    transaction {
        SchemaUtils.create(Comments, Issues)
    }
    EventController.eventService = EventService(InjectConfig.dependenciasEventService())
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