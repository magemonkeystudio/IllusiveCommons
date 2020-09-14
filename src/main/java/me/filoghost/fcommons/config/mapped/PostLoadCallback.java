package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigPostLoadException;

public interface PostLoadCallback {

	void postLoad() throws ConfigPostLoadException;

}
