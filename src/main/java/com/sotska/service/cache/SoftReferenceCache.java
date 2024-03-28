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
        if (cachedEntities.containsKey(id) && cachedEntities.get(id).get() == null) {
            cachedEntities.remove(id);
            log.info("Entity with id: " + id + " was removed from cache.");
        }
        return cachedEntities.computeIfAbsent(id, (id2) -> new SoftReference<>(dataProvider.apply(id2))).get();
    }

    public void update(K key, V value) {
        if (!cachedEntities.containsKey(key)) {
            return;
        }
        cachedEntities.put(key, new SoftReference<>(value));
    }
}

