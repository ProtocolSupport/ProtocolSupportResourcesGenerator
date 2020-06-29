package protocolsupportresourcesgenerator.generators.mappings.particles;

import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.server.v1_16_R1.IRegistry;
import net.minecraft.server.v1_16_R1.MinecraftKey;
import protocolsupportresourcesgenerator.generators.mappings.FlatteningResourceUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.Utils;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;

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

	static {
		FlatteningResourceUtils.loadMappingToRegistry("particles.json", (version, name) -> IRegistry.PARTICLE_TYPE.a(IRegistry.PARTICLE_TYPE.get(MinecraftKey.a(name))), REGISTRY);
	}

	public static void writeMappings() throws IOException {
		try (FileWriter writer = MappingsGeneratorConstants.createFileWriter("flatteningparticles.json")) {
			Utils.GSON.toJson(FlatteningResourceUtils.generateJsonMappingsFromRegistry(REGISTRY, table_size), writer);
		}
	}

}
