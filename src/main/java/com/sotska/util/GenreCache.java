package com.sotska.util;

import com.sotska.dao.GenreRepository;
import com.sotska.entity.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreCache {

    @Value("${cache.genre.time-to-live}")
    private Long timeToLive;

    private final GenreRepository genreRepository;
    private List<Genre> cashedGenres;

    private Date lastUpdate;

    public List<Genre> getAllGenres() {
        var now = System.currentTimeMillis();
        var expireDate = new Date(now - timeToLive);

        if ((cashedGenres == null) || (expireDate.after(lastUpdate))) {
            synchronized (this) {
                if ((cashedGenres == null) || (expireDate.after(lastUpdate))) {
                    updateData(now);
                    return cashedGenres;
                }
            }
        }
        return cashedGenres;
    }

    private void updateData(long now) {
        lastUpdate = new Date(now);
        cashedGenres = genreRepository.findAll();
    }
}
