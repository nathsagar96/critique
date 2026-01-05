package com.critique.services;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.critique.dtos.requests.RestaurantSearchRequest;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.RestaurantSummaryResponse;
import com.critique.entities.Restaurant;
import com.critique.mappers.RestaurantMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final RestaurantMapper restaurantMapper;

    public PageResponse<RestaurantSummaryResponse> searchRestaurants(RestaurantSearchRequest searchRequest) {
        log.debug("Searching restaurants with parameters: {}", searchRequest);

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // Text search with fuzzy matching
        if (searchRequest.q() != null && !searchRequest.q().isBlank()) {
            mustQueries.add(Query.of(q -> q.multiMatch(mm -> mm.query(searchRequest.q())
                    .fields("name^3", "description^2", "cuisineType")
                    .fuzziness("AUTO")
                    .prefixLength(2)
                    .maxExpansions(50))));
        }

        // Geospatial search
        if (searchRequest.latitude() != null && searchRequest.longitude() != null && searchRequest.radius() != null) {

            filterQueries.add(Query.of(q -> q.geoDistance(gd -> gd.field("address.location")
                    .distance(searchRequest.radius() + "km")
                    .location(gl ->
                            gl.latlon(ll -> ll.lat(searchRequest.latitude()).lon(searchRequest.longitude()))))));
        }

        // Cuisine type filter
        if (searchRequest.cuisineType() != null && !searchRequest.cuisineType().isBlank()) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("cuisineType").value(searchRequest.cuisineType()))));
        }

        // Minimum rating filter
        if (searchRequest.minRating() != null) {
            filterQueries.add(Query.of(
                    q -> q.range(r -> r.number(n -> n.field("averageRating").gte(searchRequest.minRating())))));
        }

        // Build the main bool query
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        if (!mustQueries.isEmpty()) {
            boolBuilder.must(mustQueries);
        }

        if (!filterQueries.isEmpty()) {
            boolBuilder.filter(filterQueries);
        }

        // If no queries at all, match all
        Query mainQuery;
        if (mustQueries.isEmpty() && filterQueries.isEmpty()) {
            mainQuery = Query.of(q -> q.matchAll(ma -> ma));
        } else {
            mainQuery = Query.of(q -> q.bool(boolBuilder.build()));
        }

        // Build native query with pagination
        Pageable pageable = PageRequest.of(searchRequest.page() - 1, searchRequest.size());

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(mainQuery)
                .withPageable(pageable)
                .build();

        // Execute search
        SearchHits<Restaurant> searchHits = elasticsearchOperations.search(nativeQuery, Restaurant.class);

        List<Restaurant> restaurants =
                searchHits.stream().map(SearchHit::getContent).toList();

        List<RestaurantSummaryResponse> responses = restaurantMapper.toSummaryResponseList(restaurants);

        long totalElements = searchHits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalElements / searchRequest.size());

        return new PageResponse<>(responses, searchRequest.page(), searchRequest.size(), totalElements, totalPages);
    }
}
