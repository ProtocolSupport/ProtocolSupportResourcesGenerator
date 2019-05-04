package protocolsupportmappingsgenerator.generators.block;

import java.io.File;

import protocolsupportmappingsgenerator.EntryPoint;

public class BlockGeneratorConstants {

	public static final File targetFolder = new File(EntryPoint.targetFolder, "mappings");
	static {
		targetFolder.mkdirs();
	}

}
