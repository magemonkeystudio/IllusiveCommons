/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A class similar to a set with case-insensitive elements, which internally uses a delegate set.
 * Null elements are not permitted.
 */
public class CaseInsensitiveSet implements Iterable<CaseInsensitiveString> {

    private final Set<CaseInsensitiveString> delegate;
    
    public static CaseInsensitiveSet create() {
        return new CaseInsensitiveSet(new HashSet<>());
    }

    public static CaseInsensitiveSet createConcurrent() {
        return new CaseInsensitiveSet(ConcurrentHashMap.newKeySet());
    }

    public static CaseInsensitiveSet createLinked() {
        return new CaseInsensitiveSet(new LinkedHashSet<>());
    }

    public static CaseInsensitiveSet createSynchronizedLinked() {
        return new CaseInsensitiveSet(Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    private CaseInsensitiveSet(@NotNull Set<CaseInsensitiveString> delegate) {
        this.delegate = delegate;
    }
    
    public Set<CaseInsensitiveString> asSet() {
        return delegate;
    }
    
    public boolean add(@NotNull String element) {
        return delegate.add(transformElement(element));
    }

    public boolean remove(@NotNull String element) {
        return delegate.remove(transformElement(element));
    }
    
    public boolean contains(@NotNull String element) {
        return delegate.contains(transformElement(element));
    }

    public boolean removeIf(@NotNull Predicate<String> filter) {
        return delegate.removeIf(element -> filter.test(element.getOriginalString()));
    }

    public void addAll(@NotNull Collection<? extends String> collection) {
        Collection<CaseInsensitiveString> transformedCollection = new LinkedList<>(); // Preserve iteration order
        for (String element : collection) {
            transformedCollection.add(transformElement(element));
        }
        delegate.addAll(transformedCollection);
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
    
    @Override
    public Iterator<CaseInsensitiveString> iterator() {
        return delegate.iterator();
    }

    @Override
    public void forEach(@NotNull Consumer<? super CaseInsensitiveString> action) {
        delegate.forEach(action);
    }

    @Override
    public Spliterator<CaseInsensitiveString> spliterator() {
        return delegate.spliterator();
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

        return ((CaseInsensitiveSet) other).delegate.equals(delegate);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    private CaseInsensitiveString transformElement(String element) {
        return new CaseInsensitiveString(element);
    }

}
