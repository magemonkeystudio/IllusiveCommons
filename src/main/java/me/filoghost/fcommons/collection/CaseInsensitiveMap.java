/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.Preconditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A map which converts keys to lowercase before any operation, internally using a delegate map.
 * 
 * Some methods, such as {@link CaseInsensitiveMap#keySet()} and {@link CaseInsensitiveMap#entrySet()},
 * return collections with lowercase strings that are NOT case insensitive.
 * 
 * Some methods, such as {@link CaseInsensitiveMap#forEach(BiConsumer)}, provide lowercase strings.
 */
public class CaseInsensitiveMap<V> implements Map<String, V> {

    private final Map<String, V> delegate;

    public CaseInsensitiveMap() {
        this(new HashMap<>());
    }

    public CaseInsensitiveMap(Map<String, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V put(String key, V value) {
        return delegate.put(getLowercaseKey(key), value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(getLowercaseKey(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(getLowercaseKey(key));
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(getLowercaseKey(key));
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        Map<String, V> lowercaseMap = new LinkedHashMap<>(); // Maintain order in case the given map is sorted
        for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
            lowercaseMap.put(getLowercaseKey(entry.getKey()), entry.getValue());
        }
        delegate.putAll(lowercaseMap);
    }

    @Override
    public void clear() {
        delegate.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return delegate.getOrDefault(getLowercaseKey(key), defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super V> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super V, ? extends V> function) {
        delegate.replaceAll(function);
    }
    
    @Override
    public V putIfAbsent(String key, V value) {
        return delegate.putIfAbsent(getLowercaseKey(key), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(getLowercaseKey(key), value);
    }

    @Override
    public boolean replace(String key, V oldValue, V newValue) {
        return delegate.replace(getLowercaseKey(key), oldValue, newValue);
    }
    
    @Override
    public V replace(String key, V value) {
        return delegate.replace(getLowercaseKey(key), value);
    }

    @Override
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
        return delegate.computeIfAbsent(getLowercaseKey(key), mappingFunction);
    }

    @Override
    public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
        return delegate.computeIfPresent(getLowercaseKey(key), remappingFunction);
    }
    
    @Override
    public V compute(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
        return delegate.compute(getLowercaseKey(key), remappingFunction);
    }

    @Override
    public V merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegate.merge(getLowercaseKey(key), value, remappingFunction);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    private String getLowercaseKey(Object key) {
        Preconditions.notNull(key, "key");
        Preconditions.checkArgument(key instanceof String, "key must be a string");
        return ((String) key).toLowerCase(Locale.ROOT);
    }
    
    private String getLowercaseKey(String key) {
        Preconditions.notNull(key, "key");
        return key.toLowerCase(Locale.ROOT);
    }

}
