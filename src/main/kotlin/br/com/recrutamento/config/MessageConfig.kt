package br.com.recrutamento.config

import java.util.*

class MessageConfig {

    companion object {
        fun montarMensagensConstraints(campo: String, parametros: Map<String, Any>): String{
            var mensagem = ""
            try {
                val chaveMensagem = parametros["message"].toString().replace("{", "").replace("}", "")
                val bundle = ResourceBundle.getBundle("messages")
                mensagem =  bundle.getString(chaveMensagem)
                if (chaveMensagem.equals("javax.validation.constraints.Size.message")){
                    mensagem = mensagem.replace("{min}", parametros["min"].toString(), true)
                    mensagem = mensagem.replace("{max}", parametros["max"].toString(), true)
                }
                mensagem = mensagem.replace("{field}", bundle.getString(campo), true)
            }catch (e: Exception){

            }
            return mensagem
        }
    }

}