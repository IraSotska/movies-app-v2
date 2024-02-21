package com.sotska.repository;

import com.sotska.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = "SELECT review FROM Review review WHERE review.id IN " +
            "(SELECT movieReview.reviewId FROM MovieReview movieReview WHERE movieReview.movieId =:movieId)")
    List<Review> findByMovieId(Long movieId);
}
