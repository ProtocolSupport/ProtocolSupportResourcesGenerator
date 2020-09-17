package protocolsupportresourcesgenerator;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.bukkit.craftbukkit.Main;

import protocolsupportresourcesgenerator.generators.mappings.block.FlatteningBlockDataMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.block.LegacyBlockDataMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.block.PreFlatteningBlockIdDataMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.entity.FlatteningEntityDataGenerator;
import protocolsupportresourcesgenerator.generators.mappings.item.FlatteningItemMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.item.LegacyItemTypeMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.item.PreFlatteningItemIdMappingsGenerator;
import protocolsupportresourcesgenerator.generators.mappings.particles.FlatteningParticleMappingsGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.BlockDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.EntityDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.ItemDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.PotionDataGenerator;
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

			Thread.sleep(TimeUnit.SECONDS.toMillis(30));

			PreFlatteningBlockIdDataMappingsGenerator.writeMappings();
			FlatteningBlockDataMappingsGenerator.writeMappings();
			LegacyBlockDataMappingsGenerator.writeMappings();

			PreFlatteningItemIdMappingsGenerator.writeMappings();
			FlatteningItemMappingsGenerator.writeMappings();
			LegacyItemTypeMappingsGenerator.writeMappings();

			FlatteningEntityDataGenerator.writeMappings();

			FlatteningParticleMappingsGenerator.writeMappings();

			BlockDataGenerator.writeData();
			ItemDataGenerator.writeData();
			EntityDataGenerator.writeData();
			SoundDataGenerator.writeData();
			PotionDataGenerator.writeData();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			Runtime.getRuntime().halt(0);
		}
	}

}
