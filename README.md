# Instruções para rodar o projeto:

Para rodar o projeto em produção siga as seguintes instruções:

* Crie no PostgreSQL um banco de dados chamado "webhock";
* Edite as configurações do banco de dados (usuário, senha, porta, etc.) no arquivo "application.properties" em "src/main/resources";

Testes:

* Os testes estão usando o banco de dados H2 em memória, caso deseje alterar alguma configuração acesse o arquivo "application.properties" em "scr/test/resources".

Observações:

* Caso queira iniciar a aplicação com alguns dados iniciais basta criar uma classe em "src/main/kotlin" na pasta "db/migration";
* O nome do arquivo deve seguir o seguinte formato: V"data hora no formato yyyymmddhhmmss"__"descrição".kt;
* A letra V inicial deve ser maiúscula e devem ser 2 underlines após o número do arquivo de migração;
* Faça a classe implementar a classe "BaseJavaMigration" do package "org.flywaydb.core.api.migration.BaseJavaMigration";
* Inclua os comandos sql que devem ser executados quando a aplicação iniciar;
