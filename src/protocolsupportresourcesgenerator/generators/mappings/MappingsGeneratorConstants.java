package protocolsupportresourcesgenerator.generators.mappings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocolsupportresourcesgenerator.EntryPoint;

public class MappingsGeneratorConstants {

	public static final File targetFolder = new File(EntryPoint.targetFolder, "mappings");
	static {
		targetFolder.mkdirs();
	}

	public static FileWriter createFileWriter(String filename) throws IOException {
		return new FileWriter(new File(targetFolder, filename));
	}

}
