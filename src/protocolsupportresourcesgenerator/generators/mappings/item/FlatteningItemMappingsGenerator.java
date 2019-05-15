package protocolsupportresourcesgenerator.generators.mappings.item;

import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Material;

import protocolsupportresourcesgenerator.generators.mappings.FlatteningResourceUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.Utils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;

public class FlatteningItemMappingsGenerator {

	public static final IdRemappingRegistry<ArrayBasedIdRemappingTable> REGISTRY = new IdRemappingRegistry<ArrayBasedIdRemappingTable>() {
		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			ArrayBasedIdRemappingTable table = new ArrayBasedIdRemappingTable(MinecraftData.ITEM_COUNT);
			for (int i = 0; i < MinecraftData.ITEM_COUNT; i++) {
				table.setRemap(i, -1);
			}
			return table;
		}
	};

	static {
		FlatteningResourceUtils.loadMappingToRegistry("items.json", name -> MaterialAPI.getItemNetworkId(Material.matchMaterial(name)), REGISTRY);
	}

	public static void writeMappings() throws IOException {
		try (FileWriter writer = MappingsGeneratorConstants.createFileWriter("flatteningitem.json")) {
			Utils.GSON.toJson(FlatteningResourceUtils.generateJsonMappingsFromRegistry(REGISTRY, MinecraftData.ITEM_COUNT), writer);
		}
	}

}
