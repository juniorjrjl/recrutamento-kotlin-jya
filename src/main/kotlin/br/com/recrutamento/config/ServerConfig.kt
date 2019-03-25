package br.com.recrutamento.config

import br.com.recrutamento.controller.EventController
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.json.JavalinJackson
import org.slf4j.LoggerFactory

class ServerConfig {

    companion object {

        private var app: Javalin? = null

        fun iniciarServidor(){
            app = Javalin.create()
            val log = LoggerFactory.getLogger(ServerConfig::class.java)
            JavalinJackson.configure(jacksonObjectMapper().findAndRegisterModules())
            app!!.routes{
                path("github/") {
                    path("webhook") {
                        post(EventController::salvar)
                    }
                    path("issues/:number/events") {
                        get(EventController::getIssue)
                    }
                }
            }
            app!!.start(AppConfig.buscarPropriedadeInt("javalin.port"))
            log.info("as seguintes rotas foram geradas")
            app!!.handlerMetaInfo.forEach{log.info(it.path)}
        }
        fun pararServidor(){
            app!!.stop()
        }
    }

}