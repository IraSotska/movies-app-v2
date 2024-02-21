package com.sotska.repository;

import com.sotska.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends PagingAndSortingRepository<Movie, Long>, CrudRepository<Movie, Long> {

    Page<Movie> findAll(Pageable pageable);

    List<Movie> findByGenres_Id(Long genreId);

    @Query(value = "SELECT movie FROM Movie movie ORDER BY RANDOM() LIMIT 3")
    List<Movie> getRandomMovies();

    @Query(value = "SELECT new Movie(movie.id, movie.nameUkrainian, movie.nameNative, movie.yearOfRelease, " +
            "movie.rating, movie.price, movie.picturePath) FROM Movie movie WHERE movie.id =:movieId")
    Optional<Movie> findById(Long movieId);
}
