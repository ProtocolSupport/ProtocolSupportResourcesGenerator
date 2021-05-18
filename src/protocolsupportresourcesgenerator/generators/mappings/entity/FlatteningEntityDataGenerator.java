package protocolsupportresourcesgenerator.generators.mappings.entity;

import java.io.FileWriter;
import java.io.IOException;

import protocolsupportresourcesgenerator.generators.mappings.FlatteningResourceUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.Utils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

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

	protected static String upgradeEntityType(ProtocolVersion version, String name) {
		if (version.isBeforeOrEq(ProtocolVersion.MINECRAFT_1_15_2)) {
			switch (name) {
				case "zombie_pigman":
				case "minecraft:zombie_pigman": {
					return "minecraft:zombified_piglin";
				}
			}
		}
		return name;
	}

	static {
		FlatteningResourceUtils.loadMappingToRegistry(
			"entity_type.json", (version, name) -> MaterialAPI.getEntityTypeNetworkId(upgradeEntityType(version, name)), REGISTRY
		);
	}

	public static void writeMappings() throws IOException {
		try (FileWriter writer = MappingsGeneratorConstants.createFileWriter("flatteningentity.json")) {
			Utils.GSON.toJson(FlatteningResourceUtils.generateJsonMappingsFromRegistry(REGISTRY, table_size), writer);
		}
	}

}
