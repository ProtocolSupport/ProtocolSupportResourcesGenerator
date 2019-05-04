package protocolsupportresourcesgenerator;

import java.io.File;

import org.bukkit.craftbukkit.Main;

import protocolsupportresourcesgenerator.generators.block.LegacyBlockDataMappingsGenerator;

public class EntryPoint {

	public static final File targetFolder = new File("target");
	static {
		targetFolder.mkdirs();
	}

	public static void main(String[] args) {
		try {
			System.setProperty("com.mojang.eula.agree", "true");
			Main.main(new String[0]);
			LegacyBlockDataMappingsGenerator.writeMappings();
		} finally {
			System.exit(0);
		}
	}

}
