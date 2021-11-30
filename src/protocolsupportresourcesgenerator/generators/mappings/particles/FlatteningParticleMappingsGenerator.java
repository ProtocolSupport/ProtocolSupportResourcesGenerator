package protocolsupportresourcesgenerator.generators.mappings.particles;

import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.core.IRegistry;
import protocolsupportresourcesgenerator.generators.mappings.FlatteningResourceUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.Utils;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftRegistryUtils;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

public class FlatteningParticleMappingsGenerator {

	protected static final int table_size = 128;

	public static final IdRemappingRegistry<ArrayBasedIdRemappingTable> REGISTRY = new IdRemappingRegistry<ArrayBasedIdRemappingTable>() {
		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			ArrayBasedIdRemappingTable table = new ArrayBasedIdRemappingTable(MinecraftData.ITEM_COUNT);
			for (int i = 0; i < table_size; i++) {
				table.setRemap(i, -1);
			}
			return table;
		}
	};

	protected static String upgradeParticleType(ProtocolVersion version, String type) {
		if (version.isBeforeOrEq(ProtocolVersion.MINECRAFT_1_17_1)) {
			switch (type) {
				case "barrier":
				case "minecraft:barrier":
				case "light":
				case "minecraft:light": {
					return "minecraft:block_marker";
				}
			}
		}
		return type;
	}

	static {
		FlatteningResourceUtils.loadMappingToRegistry(
			"particles.json", (version, name) -> MinecraftRegistryUtils.getIdByKey(IRegistry.ac, upgradeParticleType(version, name)), REGISTRY
		);
	}

	public static void writeMappings() throws IOException {
		try (FileWriter writer = MappingsGeneratorConstants.createFileWriter("flatteningparticles.json")) {
			Utils.GSON.toJson(FlatteningResourceUtils.generateJsonMappingsFromRegistry(REGISTRY, table_size), writer);
		}
	}

}
