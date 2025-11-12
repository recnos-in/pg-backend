package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "search_queries", schema = "public", indexes = {
        @Index(name = "idx_search_queries_user", columnList = "user_id"),
        @Index(name = "idx_search_queries_date", columnList = "created_at")
})
public class SearchQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "query", nullable = false, length = Integer.MAX_VALUE)
    private String query;

    @Column(name = "query_type", length = 50)
    private String queryType;

    @Column(name = "filters")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> filters;

    @Column(name = "results_count")
    private Integer resultsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clicked_pg_id")
    private Pg clickedPg;

    @Column(name = "click_position")
    private Integer clickPosition;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

}