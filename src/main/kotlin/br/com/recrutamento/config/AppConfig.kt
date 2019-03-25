package br.com.recrutamento.config

import java.util.*

class AppConfig {

    companion object {

        private val config: Properties = Properties()
        fun buscarPropriedade(chave: String):String{
            if (config.keys.isEmpty()){
                val input = AppConfig::class.java.getResourceAsStream("/applicattion.properties")
                config.load(input)
            }
            return config.getProperty(chave)
        }
        fun buscarPropriedadeInt(chave: String):Int{
            if (config.keys.isEmpty()){
                val input = AppConfig::class.java.getResourceAsStream("/applicattion.properties")
                config.load(input)
            }
            return config.getProperty(chave).toInt()
        }
    }

}