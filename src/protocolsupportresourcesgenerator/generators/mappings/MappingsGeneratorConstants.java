package protocolsupportresourcesgenerator.generators.mappings;

import java.io.File;

import protocolsupportresourcesgenerator.EntryPoint;

public class MappingsGeneratorConstants {

	public static final File targetFolder = new File(EntryPoint.targetFolder, "mappings");
	static {
		targetFolder.mkdirs();
	}

}
