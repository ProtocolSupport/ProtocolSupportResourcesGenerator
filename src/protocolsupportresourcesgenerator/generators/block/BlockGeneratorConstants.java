package protocolsupportresourcesgenerator.generators.block;

import java.io.File;

import protocolsupportresourcesgenerator.EntryPoint;

public class BlockGeneratorConstants {

	public static final File targetFolder = new File(EntryPoint.targetFolder, "mappings");
	static {
		targetFolder.mkdirs();
	}

}
