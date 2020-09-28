/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */

package me.filoghost.fcommons.config.valuetype;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.ConfigValueType;

import java.util.ArrayList;
import java.util.List;

public class WrappedListConfigValueType extends ConfigValueType<List<ConfigValue>> {

	public WrappedListConfigValueType() {
		super(ConfigErrors.valueNotList);
	}

	@Override
	protected boolean isValidConfigValue(Object value) {
		return value instanceof List;
	}

	@Override
	protected List<ConfigValue> fromConfigValue(Object value) {
		List<ConfigValue> result = new ArrayList<>();

		for (Object element : (List<?>) value) {
			result.add(wrapRawValue(element));
		}

		return result;
	}

	@Override
	protected Object toConfigValue(List<ConfigValue> value) {
		List<Object> result = new ArrayList<>();

		for (ConfigValue element : value) {
			result.add(unwrapRawValue(element));
		}

		return result;
	}

}
