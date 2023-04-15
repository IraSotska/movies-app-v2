package com.sotska.util;

import com.sotska.dao.GenreRepository;
import com.sotska.entity.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreCache {

    private final GenreRepository genreRepository;
    private List<Genre> cashedGenres;

    private Date lastUpdate;
    private Long timeToLive;

    public List<Genre> findAll() {
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

    @Value("${cache.genre.time-to-live}")
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
}
