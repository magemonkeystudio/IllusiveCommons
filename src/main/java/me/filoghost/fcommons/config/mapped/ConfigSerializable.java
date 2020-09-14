package me.filoghost.fcommons.config.mapped;

import me.filoghost.fcommons.config.exception.ConfigValueException;

public interface ConfigSerializable<T> {

	void deserialize(T configValue) throws ConfigValueException;

	T serialize();

}
