/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import com.google.common.collect.ImmutableList;
import me.filoghost.fcommons.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class CollectionUtils {

    @Contract("null -> null; !null -> !null")
    public static <E> List<E> copy(@Nullable List<E> list) {
        if (list == null) {
            return null;
        }
        
        return new ArrayList<>(list);
    }

    @Contract("null -> null; !null -> !null")
    public static <K, V> Map<K, V> copy(@Nullable Map<K, V> map) {
        if (map == null) {
            return null;
        }
        
        return new HashMap<>(map);
    }
    
    @Contract("null -> null; !null -> !null")
    public static <E> ImmutableList<E> immutableCopy(@Nullable List<E> list) {
        if (list == null) {
            return null;
        }
        
        return ImmutableList.copyOf(list);
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <A, B> List<B> transform(@Nullable List<A> list, @NotNull Function<A, B> transformFunction) {
        if (list == null) {
            return null;
        }
        
        List<B> result = new ArrayList<>(list.size());
        for (A element : list) {
            result.add(transformFunction.apply(element));
        }
        return result;
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <A, B> ImmutableList<B> transformImmutable(@Nullable List<A> list, @NotNull Function<A, B> transformFunction) {
        if (list == null) {
            return null;
        }
        
        ImmutableList.Builder<B> builder = ImmutableList.builder();
        for (A element : list) {
            builder.add(transformFunction.apply(element));
        }
        return builder.build();
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static <E> List<E> replaceNulls(@Nullable List<E> list, @NotNull E replacement) {
        Preconditions.notNull(replacement, "replacement");
        return transform(list, element -> element != null ? element : replacement);
    }

}
