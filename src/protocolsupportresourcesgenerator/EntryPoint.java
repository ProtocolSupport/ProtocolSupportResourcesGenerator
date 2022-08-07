package protocolsupportresourcesgenerator;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;

import net.minecraft.server.MinecraftServer;
import protocolsupportresourcesgenerator.generators.locale.LocaleDataGenerator;
import protocolsupportresourcesgenerator.generators.locale.LocaleListGenerator;
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
import protocolsupportresourcesgenerator.generators.minecraftdata.ParticleDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.PotionDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.SoundDataGenerator;
import protocolsupportresourcesgenerator.generators.minecraftdata.TileEntityDataGenerator;

public class EntryPoint {

	public static final File targetFolder = new File("target");
	static {
		targetFolder.mkdirs();
	}

	public static void main(String[] args) {
		try {
			LocaleDataGenerator.writeData();
			LocaleListGenerator.writeData();

			startServer();

			PreFlatteningBlockIdDataMappingsGenerator.writeMappings();
			FlatteningBlockDataMappingsGenerator.writeMappings();
			LegacyBlockDataMappingsGenerator.writeMappings();

			PreFlatteningItemIdMappingsGenerator.writeMappings();
			FlatteningItemMappingsGenerator.writeMappings();
			LegacyItemTypeMappingsGenerator.writeMappings();

			FlatteningEntityDataGenerator.writeMappings();

			FlatteningParticleMappingsGenerator.writeMappings();

			BlockDataGenerator.writeData();
			TileEntityDataGenerator.writeData();
			ItemDataGenerator.writeData();
			EntityDataGenerator.writeData();
			SoundDataGenerator.writeData();
			PotionDataGenerator.writeData();
			ParticleDataGenerator.writeData();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			Bukkit.shutdown();
		}
	}

	protected static void startServer() {
		try {
			Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.toString().endsWith(".lock")) {
						Files.delete(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Failed to delete .lock files", e);
		}

		System.setProperty("com.mojang.eula.agree", "true");
		System.setProperty("IReallyKnowWhatIAmDoingISwear", "true");
		int port = -1;
		try {
			try (ServerSocket serverSocket = new ServerSocket(0)) {
				port = serverSocket.getLocalPort();
			}
		} catch (IOException e) {
		}
		if (port <= 0) {
			throw new IllegalStateException("Unable to find free port for server");
		}
		Main.main(new String[] {"--host", InetAddress.getLoopbackAddress().getHostAddress(), "--port", Integer.toString(port), "nogui"});
		FutureTask<Void> taskWaitServerStart = new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				Server server = null;
				try {
					while ((server = Bukkit.getServer()) == null) {
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					throw new IllegalStateException("Interrupted while waiting for Bukkit#getServer init", e);
				}
				MinecraftServer minecraftserver = ((CraftServer) server).getServer();
				FutureTask<Void> taskWaitServerTick = new FutureTask<>(new Callable<Void>() {
					@Override
					public Void call() {
						return null;
					}
				});
				minecraftserver.processQueue.add(taskWaitServerTick);
				try {
					for (;;) {
						if (minecraftserver.hasStopped()) {
							throw new IllegalStateException("Server has stopped while starting");
						}
						try {
							taskWaitServerTick.get(1, TimeUnit.SECONDS);
							break;
						} catch (TimeoutException e) {
						}
					}
				} catch (ExecutionException | InterruptedException e) {
					throw new IllegalStateException("Interrupted while waiting for Server tick wait task to complete", e);
				}
			}
		}, null);
		try {
			new Thread(taskWaitServerStart, "Server start wait thread").start();
			taskWaitServerStart.get(10, TimeUnit.MINUTES);
		} catch (Throwable t) {
			throw new IllegalStateException("Failed to start server", t);
		}
	}

}
