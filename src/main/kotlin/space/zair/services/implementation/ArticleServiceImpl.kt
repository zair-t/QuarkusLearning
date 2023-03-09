package space.zair.services.implementation

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.Uni
import space.zair.model.entity.Article
import space.zair.services.ArticleService
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

@ApplicationScoped
class ArticleServiceImpl : ArticleService {
    override fun getArticles(): Uni<List<Article>> = Article.listAll(Sort.by("title"))

    override fun getArticle(id: Long): Uni<Article?> = Article.findById(id)

    override fun saveArticle(article: Article): Uni<Response> =
        if (article.id != null)
            throw WebApplicationException("Id was invalidly set on request.", 422)
        else {
            Panache.withTransaction(article::persist)
                .replaceWith(Response.ok(article).status(Response.Status.CREATED)::build)
        }

    override fun updateArticle(id: Long, article: Article): Uni<Response> =
        Panache
            .withTransaction {
                Article.findById(id)
                    .onItem().ifNotNull().invoke { entity ->
                        entity?.title = article.title
                        entity?.author = article.author
                        entity?.body = article.body
                    }
            }
            .onItem().ifNotNull().transform { entity -> Response.ok(entity).build() }
            .onItem().ifNull().continueWith(Response.ok().status(Response.Status.NOT_FOUND)::build)


    override fun deleteArticle(id: Long): Uni<Response> =
        Panache.withTransaction {
            Article.deleteById(id)
        }.map { deleted ->
            if (deleted)
                Response.ok().status(Response.Status.NO_CONTENT).build()
            else
                Response.ok().status(Response.Status.NOT_FOUND).build()
        }
}