package com.sotska.repository;

import com.sotska.entity.MovieCreate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCreateRepository extends CrudRepository<MovieCreate, Long> {
}
