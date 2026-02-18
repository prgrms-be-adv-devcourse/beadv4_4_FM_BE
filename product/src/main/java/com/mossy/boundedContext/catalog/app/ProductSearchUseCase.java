package com.mossy.boundedContext.catalog.app;

import co.elastic.clients.elasticsearch._types.ScriptLanguage;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.mossy.boundedContext.catalog.query.CatalogDocument;
import com.mossy.boundedContext.catalog.domain.enums.ProductSearchOrder;
import com.mossy.boundedContext.catalog.in.dto.command.ProductSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchUseCase {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<CatalogDocument> search(ProductSearchCondition condition, Pageable pageable) {
        NativeQueryBuilder builder = NativeQuery.builder();
        Query baseQuery = createFilterQuery(condition);

        ProductSearchOrder effectiveOrder = condition.getEffectiveOrder();

        if (effectiveOrder.isPopular()) {
            applyPopularScoreQuery(builder, baseQuery);
        } else {
            builder.withQuery(baseQuery);
            applyFieldSort(builder, effectiveOrder);
        }

        builder.withPageable(pageable);

        SearchHits<CatalogDocument> searchHits = elasticsearchOperations.search(
                builder.build(), CatalogDocument.class
        );

        List<CatalogDocument> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

    private Query createFilterQuery(ProductSearchCondition cond) {
        return Query.of(q -> q.bool(b -> {
            if (StringUtils.hasText(cond.keyword())) {
                b.must(m -> m.multiMatch(mm -> mm
                        .query(cond.keyword())
                        .fields("name^3", "brand^2", "description")
                ));
            } else {
                b.must(m -> m.matchAll(ma -> ma));
            }

            if (cond.categoryId() != null) {
                b.filter(f -> f.term(t -> t
                        .field("categoryId")
                        .value(cond.categoryId())
                ));
            }

            return b;
        }));
    }

    private void applyPopularScoreQuery(NativeQueryBuilder builder, Query baseQuery) {
        builder.withQuery(q -> q.functionScore(fs -> fs
                .query(baseQuery)
                .functions(f -> f.scriptScore(ss -> ss
                        .script(s -> s.lang(ScriptLanguage.Painless)
                                .source("""
                                    (doc['salesCount'].size() == 0 ? 0 : doc['salesCount'].value) * 10 + 
                                    (doc['reviewCount'].size() == 0 ? 0 : doc['reviewCount'].value)
                                    """)
                        ))
                )
                .boostMode(FunctionBoostMode.Multiply)
        ));
    }

    private void applyFieldSort(NativeQueryBuilder builder, ProductSearchOrder order) {
        switch (order) {
            case LATEST -> builder.withSort(s -> s.field(f -> f
                    .field("createdDate").order(SortOrder.Desc)));
            case PRICE_ASC -> builder.withSort(s -> s.field(f -> f
                    .field("price").order(SortOrder.Asc)));
            case PRICE_DESC -> builder.withSort(s -> s.field(f -> f
                    .field("price").order(SortOrder.Desc)));
        }
    }
}