package protocolsupportresourcesgenerator.generators.mappings.entity;

import java.io.FileWriter;
import java.io.IOException;

import protocolsupportresourcesgenerator.generators.mappings.FlatteningResourceUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.Utils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;

public class FlatteningEntityDataGenerator {

	protected static final int table_size = 256;

	public static final IdRemappingRegistry<ArrayBasedIdRemappingTable> REGISTRY = new IdRemappingRegistry<ArrayBasedIdRemappingTable>() {
		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			ArrayBasedIdRemappingTable table = new ArrayBasedIdRemappingTable(table_size);
			for (int i = 0; i < table_size; i++) {
				table.setRemap(i, -1);
			}
			return table;
		}
	};

	static {
		FlatteningResourceUtils.loadMappingToRegistry("entity_type.json", MaterialAPI::getEntityTypeNetworkId, REGISTRY);
	}

	public static void writeMappings() throws IOException {
		try (FileWriter writer = MappingsGeneratorConstants.createFileWriter("flatteningentity.json")) {
			Utils.GSON.toJson(FlatteningResourceUtils.generateJsonMappingsFromRegistry(REGISTRY, table_size), writer);
		}
	}

}
