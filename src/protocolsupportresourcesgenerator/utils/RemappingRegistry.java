package protocolsupportresourcesgenerator.utils;

import java.text.MessageFormat;
import java.util.EnumMap;

import protocolsupportresourcesgenerator.utils.RemappingTable.EnumRemappingTable;
import protocolsupportresourcesgenerator.utils.RemappingTable.GenericRemappingTable;
import protocolsupportresourcesgenerator.utils.RemappingTable.IdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

public abstract class RemappingRegistry<T extends RemappingTable> {

	protected final EnumMap<ProtocolVersion, T> registry = new EnumMap<>(ProtocolVersion.class);

	public RemappingRegistry() {
		clear();
	}

	public void clear() {
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			registry.put(version, createTable());
		}
	}

	public T getTable(ProtocolVersion version) {
		return registry.computeIfAbsent(version, k -> {
			throw new IllegalArgumentException(MessageFormat.format("Missing remapping table for version {0}", k));
		});
	}

	protected abstract T createTable();

	public abstract static class IdRemappingRegistry<T extends IdRemappingTable> extends RemappingRegistry<T> {

		public void registerRemapEntry(int from, int to, ProtocolVersion... versions) {
			for (ProtocolVersion version : versions) {
				getTable(version).setRemap(from, to);
			}
		}

	}

	public abstract static class EnumRemappingRegistry<T extends Enum<T>, R extends EnumRemappingTable<T>> extends RemappingRegistry<R> {

		public void registerRemapEntry(T from, T to, ProtocolVersion... versions) {
			for (ProtocolVersion version : versions) {
				getTable(version).setRemap(from, to);
			}
		}

	}

	public abstract static class GenericRemappingRegistry<T, R extends GenericRemappingTable<T>> extends RemappingRegistry<R> {

		public void registerRemapEntry(T from, T to, ProtocolVersion... versions) {
			for (ProtocolVersion version : versions) {
				getTable(version).setRemap(from, to);
			}
		}

	}

}
