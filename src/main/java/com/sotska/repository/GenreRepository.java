package com.sotska.repository;

import com.sotska.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAll();

    @Query(value = "SELECT genre FROM Genre genre WHERE genre.id IN " +
            "(SELECT movieGenre.genreId FROM MovieGenre movieGenre WHERE movieGenre.movieId =:movieId)")
    List<Genre> findByMovieId(Long movieId);

    List<Genre> findAllByIdIn(Iterable<Long> ids);
}
