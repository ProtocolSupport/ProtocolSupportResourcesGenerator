package protocolsupportresourcesgenerator.generators.locale;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class LocaleListGenerator {

	public static void writeData() throws IOException {
		Files.write(
			LocaleGeneratorConstants.TARGET_DIRECTORY_PATH.resolve("languages"),
			Files.list(LocaleGeneratorConstants.TARGET_DIRECTORY_PATH)
			.map(langPath -> langPath.getFileName().toString().split("\\.")[0])
			.collect(Collectors.toList()),
			LocaleGeneratorConstants.WRITER_OPTIONS
		);
	}

}
