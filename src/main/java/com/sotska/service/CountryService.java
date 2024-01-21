package com.sotska.service;

import com.sotska.entity.Country;
import com.sotska.exception.MoviesException;
import com.sotska.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sotska.exception.MoviesException.ExceptionType.CHILD_ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> checkIfExistAndGetByIds(List<Long> ids) {
        var countries = countryRepository.findAllByIdIn(ids);
        if (ids.size() != countries.size()) {
            throw new MoviesException(CHILD_ENTITY_NOT_FOUND, "Genre not found by one of ids: " + ids);
        }
        return countries;
    }
}
