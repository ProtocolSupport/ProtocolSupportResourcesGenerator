package protocolsupportresourcesgenerator.generators.locale;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import protocolsupportresourcesgenerator.EntryPoint;

public class LocaleGeneratorConstants {

	public static final Path TARGET_DIRECTORY_PATH = EntryPoint.targetFolder.toPath().resolve("i18n");

	public static final StandardOpenOption[] WRITER_OPTIONS = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

	public static BufferedWriter createBufferedWriter(String filename) throws IOException {
		return Files.newBufferedWriter(TARGET_DIRECTORY_PATH.resolve(filename), WRITER_OPTIONS);
	}

	static {
		try {
			Files.createDirectories(TARGET_DIRECTORY_PATH);
		} catch (IOException e) {
		}
	}

}
