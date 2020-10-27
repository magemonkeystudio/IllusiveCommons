/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.modifier;

import java.lang.annotation.Annotation;

public interface ValueModifier<V, A extends Annotation> {

    V transform(A annotation, V value);

    Class<A> getAnnotationType();

    Class<V> getValueType();

    default boolean isApplicable(Annotation annotation, Object value) {
        return getAnnotationType().isInstance(annotation) && getValueType().isInstance(value);
    }

}
