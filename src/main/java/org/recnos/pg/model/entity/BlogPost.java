package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "blog_posts", schema = "public", indexes = {
        @Index(name = "idx_blog_posts_slug", columnList = "slug"),
        @Index(name = "idx_blog_posts_status", columnList = "status"),
        @Index(name = "idx_blog_posts_published", columnList = "published_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "blog_posts_slug_key", columnNames = {"slug"})
})
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Admin author;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "excerpt", length = Integer.MAX_VALUE)
    private String excerpt;

    @Column(name = "featured_image_url", length = 500)
    private String featuredImageUrl;

    @ColumnDefault("'draft'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "scheduled_for")
    private Instant scheduledFor;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", length = Integer.MAX_VALUE)
    private String metaDescription;

    @Column(name = "meta_keywords", length = Integer.MAX_VALUE)
    private String metaKeywords;

    @Column(name = "categories")
    private List<String> categories;

    @Column(name = "tags")
    private List<String> tags;

    @ColumnDefault("0")
    @Column(name = "view_count")
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(name = "share_count")
    private Integer shareCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}