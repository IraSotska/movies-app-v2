package com.sotska.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class SoftReferenceCache<K, V> {

    private final Function<K, V> dataProvider;
    private final Map<K, SoftReference<V>> cachedEntities = new ConcurrentHashMap<>();

    @SneakyThrows
    public V getById(K id) {
        return cachedEntities
                .compute(id, (k, v) -> {
                    if (v == null || v.get() == null) {
                        return new SoftReference<>(dataProvider.apply(id));
                    }
                    return v;
                })
                .get();
    }

    public void update(K key, V value) {
        cachedEntities.put(key, new SoftReference<>(value));
    }
}

