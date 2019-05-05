package protocolsupportresourcesgenerator.generators.mappings;

import java.util.function.BiConsumer;
import java.util.function.IntFunction;

import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;

public class LegacyTypeUtils {

	public static void chainRemapTable(ArrayBasedIdRemappingTable table, int maxId) {
		for (int originalId = 0; originalId < maxId; originalId++) {
			int originalRemap = table.getRemap(originalId);
			if (originalRemap != originalId) {
				int currentRemap = originalRemap;
				int nextRemap = -1;
				while ((nextRemap = table.getRemap(currentRemap)) != currentRemap) {
					currentRemap = nextRemap;
				}
				if (currentRemap != originalRemap) {
					table.setRemap(originalId, currentRemap);
				}
			}
		}
	}

	public static void checkTable(
		ArrayBasedIdRemappingTable table,
		int maxId,
		IntFunction<Boolean> mappingExistsFunc,
		BiConsumer<Integer, Integer> remappedExists, BiConsumer<Integer, Integer> remappedDoesntExist
	) {
		for (int originalId = 0; originalId < maxId; originalId++) {
			int remappedId = table.getRemap(originalId);
			if ((originalId != remappedId) && mappingExistsFunc.apply(originalId)) {
				remappedExists.accept(originalId, remappedId);
			}
			if (!mappingExistsFunc.apply(remappedId)) {
				remappedDoesntExist.accept(originalId, remappedId);
			}
		}
	}

}
