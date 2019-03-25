package br.com.recrutamento.config

import br.com.recrutamento.db.dao.ICommentDAO
import br.com.recrutamento.db.dao.IIssueDAO
import br.com.recrutamento.db.dao.impl.CommentDAOImpl
import br.com.recrutamento.db.dao.impl.IssueDAOImpl
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import javax.validation.Validation
import javax.validation.Validator

class InjectConfig {

    companion object {
        fun dependenciasEventService(): Kodein = Kodein{
                bind<IIssueDAO>() with singleton { IssueDAOImpl() }
                bind<ICommentDAO>() with singleton { CommentDAOImpl() }
                bind<Validator>() with  singleton { Validation.buildDefaultValidatorFactory().validator }
            }
    }

}