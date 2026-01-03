package com.critique.repositories;

import com.critique.entities.Restaurant;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RestaurantRepository extends ElasticsearchRepository<Restaurant, UUID> {}
