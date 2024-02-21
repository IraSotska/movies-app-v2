package com.sotska.repository;

import com.sotska.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByIdIn(Iterable<Long> ids);

    @Query(value = "SELECT country FROM Country country WHERE country.id IN " +
            "(SELECT movieCountry.countryId FROM MovieCountry movieCountry WHERE movieCountry.movieId =:movieId)")
    List<Country> findByMovieId(Long movieId);
}
