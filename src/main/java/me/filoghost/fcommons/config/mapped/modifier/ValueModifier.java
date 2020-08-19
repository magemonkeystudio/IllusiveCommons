/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.modifier;

import me.filoghost.fcommons.Preconditions;

import java.lang.annotation.Annotation;

public interface ValueModifier<V, A extends Annotation> {

	V transformChecked(A annotation, V value);

	Class<A> getAnnotationType();

	Class<V> getValueType();

	default boolean isApplicable(Annotation annotation, Object value) {
		return getAnnotationType().isInstance(annotation) && getValueType().isInstance(value);
	}

	default Object transform(Annotation annotation, Object fieldValue) {
		Preconditions.checkArgument(isApplicable(annotation, fieldValue), "modifier doesn't match given types");

		return transformChecked(getAnnotationType().cast(annotation), getValueType().cast(fieldValue));
	}
}
