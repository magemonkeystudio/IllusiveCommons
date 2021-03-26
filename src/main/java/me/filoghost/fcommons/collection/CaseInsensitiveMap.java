/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A class similar to a map with case-insensitive keys, which internally uses a delegate map.
 * Null keys are not permitted.
 */
public class CaseInsensitiveMap<V> {

    private final Map<CaseInsensitiveString, V> delegate;
    
    public static <V> CaseInsensitiveMap<V> create() {
        return new CaseInsensitiveMap<>(new HashMap<>());
    }

    public static <V> CaseInsensitiveMap<V> createConcurrent() {
        return new CaseInsensitiveMap<>(new ConcurrentHashMap<>());
    }

    public static <V> CaseInsensitiveMap<V> createLinked() {
        return new CaseInsensitiveMap<>(new LinkedHashMap<>());
    }

    public static <V> CaseInsensitiveMap<V> createSynchronizedLinked() {
        return new CaseInsensitiveMap<>(Collections.synchronizedMap(new LinkedHashMap<>()));
    }

    private CaseInsensitiveMap(@NotNull Map<CaseInsensitiveString, V> delegate) {
        this.delegate = delegate;
    }
    
    public Map<CaseInsensitiveString, V> asMap() {
        return delegate;
    }
    
    public V put(@NotNull String key, V value) {
        return delegate.put(transformKey(key), value);
    }

    public V remove(@NotNull String key) {
        return delegate.remove(transformKey(key));
    }

    public V get(@NotNull String key) {
        return delegate.get(transformKey(key));
    }

    public V getOrDefault(@NotNull String key, V defaultValue) {
        return delegate.getOrDefault(transformKey(key), defaultValue);
    }

    public boolean containsKey(@NotNull String key) {
        return delegate.containsKey(transformKey(key));
    }

    public boolean containsValue(V value) {
        return delegate.containsValue(value);
    }

    public boolean remove(@NotNull String key, V value) {
        return delegate.remove(transformKey(key), value);
    }

    public boolean removeIf(@NotNull String key, @NotNull Predicate<V> filter) {
        AtomicBoolean removed = new AtomicBoolean(false);
        
        delegate.computeIfPresent(transformKey(key), (k, value) -> {
            if (filter.test(value)) {
                removed.set(true);
                return null;
            } else {
                return value;
            }
        });
        
        return removed.get();
    }

    public boolean removeAllIf(@NotNull BiPredicate<CaseInsensitiveString, V> filter) {
        return delegate.entrySet().removeIf(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    public boolean removeKeysIf(@NotNull Predicate<CaseInsensitiveString> filter) {
        return delegate.keySet().removeIf(filter);
    }

    public boolean removeValuesIf(@NotNull Predicate<V> filter) {
        return delegate.values().removeIf(filter);
    }
    
    public void putAll(@NotNull Map<? extends String, ? extends V> map) {
        Map<CaseInsensitiveString, V> transformedMap = new LinkedHashMap<>(); // Preserve iteration order
        for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
            transformedMap.put(transformKey(entry.getKey()), entry.getValue());
        }
        delegate.putAll(transformedMap); // Single operation to preserve atomicity (if present)
    }

    public V putIfAbsent(@NotNull String key, V value) {
        return delegate.putIfAbsent(transformKey(key), value);
    }

    public V computeIfAbsent(@NotNull String key, Supplier<V> valueSupplier) {
        return delegate.computeIfAbsent(transformKey(key), k -> valueSupplier.get());
    }

    public V merge(@NotNull String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegate.merge(transformKey(key), value, remappingFunction);
    }

    public void clear() {
        delegate.clear();
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public void forEach(@NotNull BiConsumer<CaseInsensitiveString, V> action) {
        delegate.forEach(action);
    }

    public void forEachKey(@NotNull Consumer<CaseInsensitiveString> action) {
        delegate.forEach((key, value) -> action.accept(key));
    }

    public void forEachValue(@NotNull Consumer<V> action) {
        delegate.forEach((key, value) -> action.accept(value));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return ((CaseInsensitiveMap<?>) other).delegate.equals(delegate);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    private CaseInsensitiveString transformKey(String key) {
        return new CaseInsensitiveString(key);
    }

}
