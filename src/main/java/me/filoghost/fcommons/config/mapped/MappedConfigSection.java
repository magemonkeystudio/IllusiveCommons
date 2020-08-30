package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigLoadException;

public interface MappedConfigSection {

	default void postLoad() throws ConfigLoadException {}

}
