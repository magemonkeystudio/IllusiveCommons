/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.config.mapped.modifier;

import me.filoghost.fcommons.Colors;

public class ChatColorsModifier implements ValueModifier<String, ChatColors> {

    @Override
    public String transform(ChatColors annotation, String value) {
        return Colors.addColors(value);
    }

    @Override
    public Class<ChatColors> getAnnotationType() {
        return ChatColors.class;
    }

    @Override
    public Class<String> getValueType() {
        return String.class;
    }

}
