package protocolsupportresourcesgenerator;

import java.io.File;

import org.bukkit.craftbukkit.Main;

import protocolsupportresourcesgenerator.generators.mappings.block.FlatteningBlockDataMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.block.LegacyBlockDataMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.block.PreFlatteningBlockIdDataMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.item.FlatteningItemMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.item.LegacyItemTypeMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.item.PreFlatteningItemIdMappingsGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.BlockDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.SoundDataGenerator;

public class EntryPoint {

	public static final File targetFolder = new File("target");
	static {
		targetFolder.mkdirs();
	}

	public static void main(String[] args) {
		try {
			System.setProperty("com.mojang.eula.agree", "true");
			Main.main(new String[0]);

			PreFlatteningBlockIdDataMappingsGenerator.writeMappings();
			FlatteningBlockDataMappingsGenerator.writeMappings();
			LegacyBlockDataMappingsGenerator.writeMappings();

			PreFlatteningItemIdMappingsGenerator.writeMappings();
			FlatteningItemMappingsGenerator.writeMappings();
			LegacyItemTypeMappingsGenerator.writeMappings();

			BlockDataGenerator.writeData();
			SoundDataGenerator.writeData();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			Runtime.getRuntime().halt(0);
		}
	}

}
