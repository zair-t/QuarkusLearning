package space.zair.services

import io.smallrye.mutiny.Uni
import space.zair.model.entity.Article
import javax.ws.rs.core.Response

interface ArticleService {
    fun getArticles(): Uni<List<Article>>
    fun getArticle(id: Long): Uni<Article?>
    fun saveArticle(article: Article): Uni<Response>
    fun updateArticle(id: Long, article: Article): Uni<Response>
    fun deleteArticle(id: Long): Uni<Response>
}