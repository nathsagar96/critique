package com.critique.repositories;

import com.critique.entities.Photo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PhotoRepository extends ElasticsearchRepository<Photo, String> {}
