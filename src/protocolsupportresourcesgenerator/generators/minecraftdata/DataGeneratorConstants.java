package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;

import protocolsupportresourcesgenerator.EntryPoint;

public class DataGeneratorConstants {

	public static final File targetFolder = new File(EntryPoint.targetFolder, "data");
	static {
		targetFolder.mkdirs();
	}

}
