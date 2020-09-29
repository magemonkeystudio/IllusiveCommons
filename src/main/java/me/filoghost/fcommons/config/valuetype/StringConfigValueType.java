/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */

package me.filoghost.fcommons.config.valuetype;

import me.filoghost.fcommons.config.ConfigErrors;
import me.filoghost.fcommons.config.ConfigValueType;

public class StringConfigValueType extends ConfigValueType<String> {

	public StringConfigValueType(String name) {
		super(name, ConfigErrors.valueNotString);
	}

	@Override
	protected boolean isValidConfigValue(Object value) {
		return value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character;
	}

	@Override
	protected String fromConfigValue(Object value) {
    	return value.toString();
	}

	@Override
	protected Object toConfigValue(String value) {
    	return value;
	}

}
