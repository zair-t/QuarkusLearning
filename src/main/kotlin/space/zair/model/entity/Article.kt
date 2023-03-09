package space.zair.model.entity

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity
import javax.persistence.Cacheable
import javax.persistence.Column
import javax.persistence.Entity

@Entity
@Cacheable
class Article: PanacheEntity {
    companion object: PanacheCompanion<Article>

    @Column
    lateinit var title: String

    @Column
    lateinit var author: String

    @Column
    lateinit var body: String

    constructor()

    constructor(title: String, author: String, body: String) {
        this.title = title
        this.author = author
        this.body = body
    }
}