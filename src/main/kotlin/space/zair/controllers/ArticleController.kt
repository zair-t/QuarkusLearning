package space.zair.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.Uni
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestPath
import space.zair.model.entity.Article
import space.zair.services.ArticleService
import java.lang.Exception
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.*
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Path("articles")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
class ArticleController {
    @Inject
    private lateinit var articleService: ArticleService

    @GET
    fun get() = articleService.getArticles()

    @GET
    @Path("{id}")
    fun getSingle(@RestPath id: Long): Uni<Article?> = articleService.getArticle(id)

    @POST
    @Transactional
    fun create(article: Article): Uni<Response> = articleService.saveArticle(article)

    @PUT
    @Path("{id}")
    @Transactional
    fun update(@RestPath id: Long, article: Article) = articleService.updateArticle(id, article)

    @DELETE
    @Path("{id}")
    @Transactional
    fun delete(@RestPath id: Long) = articleService.deleteArticle(id)

    @Provider
    class ErrorMapper : ExceptionMapper<Exception> {
        @Inject
        lateinit var objectMapper: ObjectMapper

        override fun toResponse(exception: Exception): Response {
            LOGGER.error("Failed to handle request", exception)
            var code = 500
            if (exception is WebApplicationException) {
                code = exception.response.status
            }
            val exceptionJson = objectMapper.createObjectNode()
            exceptionJson.put("exceptionType", exception.javaClass.name)
            exceptionJson.put("code", code)
            if (exception.message != null) {
                exceptionJson.put("error", exception.message)
            }
            return Response.status(code)
                .entity(exceptionJson)
                .build()
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(ArticleController::class.java.name)
    }
}