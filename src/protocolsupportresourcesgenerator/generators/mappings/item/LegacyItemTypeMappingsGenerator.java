package protocolsupportresourcesgenerator.generators.mappings.item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

import org.bukkit.Material;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.LegacyTypeUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.generators.mappings.block.LegacyBlockDataMappingsGenerator;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;
import protocolsupportresourcesgenerator.version.ProtocolVersionsHelper;

public class LegacyItemTypeMappingsGenerator {

	public static final ItemIdRemappingRegistry REGISTRY = new ItemIdRemappingRegistry();

	public static class ItemIdRemappingRegistry extends IdRemappingRegistry<ArrayBasedIdRemappingTable> {

		public ItemIdRemappingRegistry() {
			applyDefaultRemaps();
		}

		@SuppressWarnings("deprecation")
		public void applyDefaultRemaps() {
			//TODO: actually adapt the legacyblocktype registry instead of extracting data from it
			Arrays.stream(Material.values())
			.filter(m -> !m.isLegacy())
			.filter(m -> m.isItem() && m.isBlock())
			.forEach(materialFrom -> {
				int networkIdFrom = MaterialAPI.getBlockDataNetworkId(materialFrom.createBlockData());
				Arrays.stream(ProtocolVersion.getAllSupported())
				.forEach(version -> {
					Material materialTo = MaterialAPI.getBlockDataByNetworkId(LegacyBlockDataMappingsGenerator.REGISTRY.getTable(version).getRemap(networkIdFrom)).getMaterial();
					if (!materialFrom.equals(materialTo) && !materialTo.equals(Material.AIR) && materialFrom.isItem() && materialTo.isItem()) {
						registerRemapEntry(materialFrom, materialTo, version);
					}
				});
			});


			registerRemapEntry(Material.CHEST, Material.CHEST, ProtocolVersionsHelper.ALL_PC);
			registerRemapEntry(Material.TRAPPED_CHEST, Material.TRAPPED_CHEST, ProtocolVersionsHelper.ALL_PC);

			registerRemapEntry(Material.HONEYCOMB, Material.HAY_BLOCK, ProtocolVersionsHelper.BEFORE_1_15);
			registerRemapEntry(Material.HONEY_BOTTLE, Material.MUSHROOM_STEW, ProtocolVersionsHelper.BEFORE_1_15);
			registerRemapEntry(Material.BEE_SPAWN_EGG, Material.PARROT_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_15);

			registerRemapEntry(Material.WHITE_DYE, Material.BONE_MEAL, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.BROWN_DYE, Material.COCOA_BEANS, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.BLACK_DYE, Material.INK_SAC, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.BLUE_DYE, Material.LAPIS_LAZULI, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.CAT_SPAWN_EGG, Material.OCELOT_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.FOX_SPAWN_EGG, Material.OCELOT_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.PANDA_SPAWN_EGG, Material.POLAR_BEAR_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.PILLAGER_SPAWN_EGG, Material.WITCH_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.RAVAGER_SPAWN_EGG, Material.CAVE_SPIDER_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.TRADER_LLAMA_SPAWN_EGG, Material.LLAMA_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.WANDERING_TRADER_SPAWN_EGG, Material.VILLAGER_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.LEATHER_HORSE_ARMOR, Material.IRON_HORSE_ARMOR, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(
				Arrays.asList(
					Material.FLOWER_BANNER_PATTERN,
					Material.CREEPER_BANNER_PATTERN,
					Material.SKULL_BANNER_PATTERN,
					Material.MOJANG_BANNER_PATTERN,
					Material.GLOBE_BANNER_PATTERN
				),
				Material.PAPER,
				ProtocolVersionsHelper.BEFORE_1_14
			);
			registerRemapEntry(Material.CROSSBOW, Material.BOW, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.SWEET_BERRIES, Material.POTATO, ProtocolVersionsHelper.BEFORE_1_14);
			registerRemapEntry(Material.SUSPICIOUS_STEW, Material.MUSHROOM_STEW, ProtocolVersionsHelper.BEFORE_1_14);

			registerRemapEntry(Material.SMOOTH_QUARTZ, Material.QUARTZ_BLOCK, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.SMOOTH_RED_SANDSTONE, Material.CUT_RED_SANDSTONE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.SMOOTH_SANDSTONE, Material.CUT_SANDSTONE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.SMOOTH_STONE, Material.STONE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Arrays.asList(Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD), Material.ACACIA_LOG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Arrays.asList(Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD), Material.DARK_OAK_LOG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Arrays.asList(Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD), Material.BIRCH_LOG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Arrays.asList(Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD), Material.JUNGLE_LOG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Arrays.asList(Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD), Material.SPRUCE_LOG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Arrays.asList(Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD), Material.OAK_LOG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.DRIED_KELP, Material.POTATO, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(
				Arrays.asList(Material.COD_BUCKET, Material.PUFFERFISH_BUCKET, Material.SALMON_BUCKET, Material.TROPICAL_FISH_BUCKET),
				Material.WATER_BUCKET,
				ProtocolVersionsHelper.BEFORE_1_13
			);
			registerRemapEntry(
				Arrays.asList(Material.COD_SPAWN_EGG, Material.PUFFERFISH_SPAWN_EGG, Material.SALMON_SPAWN_EGG, Material.TROPICAL_FISH_SPAWN_EGG),
				Material.BAT_SPAWN_EGG,
				ProtocolVersionsHelper.BEFORE_1_13
			);
			registerRemapEntry(Material.DOLPHIN_SPAWN_EGG, Material.SQUID_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.DROWNED_SPAWN_EGG, Material.ZOMBIE_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.PHANTOM_SPAWN_EGG, Material.BLAZE_SPAWN_EGG, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.HEART_OF_THE_SEA, Material.LIGHT_BLUE_DYE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.NAUTILUS_SHELL, Material.LIGHT_GRAY_DYE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.PHANTOM_MEMBRANE, Material.GRAY_DYE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.SCUTE, Material.LIME_DYE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.TURTLE_HELMET, Material.CHAINMAIL_HELMET, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.TRIDENT, Material.DIAMOND_HOE, ProtocolVersionsHelper.BEFORE_1_13);
			registerRemapEntry(Material.DEBUG_STICK, Material.STICK, ProtocolVersionsHelper.BEFORE_1_13);

			// Bed items remap (in order to avoid the white bed blockdata remap)
			registerRemapEntry(Material.ORANGE_BED, Material.ORANGE_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.MAGENTA_BED, Material.MAGENTA_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.LIGHT_BLUE_BED, Material.LIGHT_BLUE_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.YELLOW_BED, Material.YELLOW_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.LIME_BED, Material.LIME_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.PINK_BED, Material.PINK_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.GRAY_BED, Material.GRAY_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.LIGHT_GRAY_BED, Material.LIGHT_GRAY_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.CYAN_BED, Material.CYAN_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.BLUE_BED, Material.BLUE_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.PURPLE_BED, Material.PURPLE_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.GREEN_BED, Material.GREEN_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.BROWN_BED, Material.BROWN_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.RED_BED, Material.RED_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));
			registerRemapEntry(Material.BLACK_BED, Material.BLACK_BED, ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2));

			registerRemapEntry(Material.KNOWLEDGE_BOOK, Material.BOOK, ProtocolVersionsHelper.BEFORE_1_12);
			registerRemapEntry(Material.IRON_NUGGET, Material.GOLD_NUGGET, ProtocolVersionsHelper.BEFORE_1_11_1);
			registerRemapEntry(Material.SHULKER_SHELL, Material.COBBLESTONE, ProtocolVersionsHelper.BEFORE_1_11);
			registerRemapEntry(Material.TOTEM_OF_UNDYING, Material.COBBLESTONE, ProtocolVersionsHelper.BEFORE_1_11);

			registerRemapEntry(Material.BEETROOT, Material.BROWN_MUSHROOM, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.BEETROOT_SOUP, Material.MUSHROOM_STEW, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.BEETROOT_SEEDS, Material.WHEAT_SEEDS, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.CHORUS_FRUIT, Material.POTATO, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.POPPED_CHORUS_FRUIT, Material.BAKED_POTATO, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.DRAGON_BREATH, Material.POTION, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.SPLASH_POTION, Material.POTION, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.LINGERING_POTION, Material.POTION, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.ELYTRA, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.END_CRYSTAL, Material.STONE, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(Material.SHIELD, Material.WOODEN_SWORD, ProtocolVersionsHelper.BEFORE_1_9);
			registerRemapEntry(
				Arrays.asList(Material.SPECTRAL_ARROW, Material.TIPPED_ARROW),
				Material.ARROW,
				ProtocolVersionsHelper.BEFORE_1_9
			);
			registerRemapEntry(
				Arrays.asList(
					Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT,
					Material.JUNGLE_BOAT, Material.SPRUCE_BOAT
				),
				Material.OAK_BOAT,
				ProtocolVersionsHelper.BEFORE_1_9
			);

			registerRemapEntry(Material.RABBIT, Material.CHICKEN, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.COOKED_RABBIT, Material.COOKED_CHICKEN, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.RABBIT_STEW, Material.MUSHROOM_STEW, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.MUTTON, Material.CHICKEN, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.COOKED_MUTTON, Material.COOKED_CHICKEN, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.PRISMARINE_SHARD, Material.STONE, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.PRISMARINE_CRYSTALS, Material.STONE, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.RABBIT_FOOT, Material.STONE, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.RABBIT_HIDE, Material.STONE, ProtocolVersionsHelper.BEFORE_1_8);
			registerRemapEntry(Material.ARMOR_STAND, Material.STONE, ProtocolVersionsHelper.BEFORE_1_8);

			registerRemapEntry(
				Arrays.asList(
					Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD,
					Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD
				),
				Material.OAK_LOG,
				ProtocolVersionsHelper.BEFORE_1_7
			);

			registerRemapEntry(Material.IRON_HORSE_ARMOR, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.BEFORE_1_6);
			registerRemapEntry(Material.GOLDEN_HORSE_ARMOR, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.BEFORE_1_6);
			registerRemapEntry(Material.DIAMOND_HORSE_ARMOR, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.BEFORE_1_6);
			registerRemapEntry(Material.LEAD, Material.STONE, ProtocolVersionsHelper.BEFORE_1_6);
			registerRemapEntry(Material.NAME_TAG, Material.STONE, ProtocolVersionsHelper.BEFORE_1_6);


			registerRemapEntry(Material.QUARTZ, Material.FEATHER, ProtocolVersionsHelper.BEFORE_1_5);
			registerRemapEntry(Material.TNT_MINECART, Material.MINECART, ProtocolVersionsHelper.BEFORE_1_5);
			registerRemapEntry(Material.HOPPER_MINECART, Material.MINECART, ProtocolVersionsHelper.BEFORE_1_5);
			registerRemapEntry(Material.NETHER_BRICK, Material.CLAY_BALL, ProtocolVersionsHelper.BEFORE_1_5);
			registerRemapEntry(Material.TRAPPED_CHEST, Material.CHEST, ProtocolVersionsHelper.BEFORE_1_5);

			for (ProtocolVersion version : ProtocolVersionsHelper.BEFORE_1_15) {
				ArrayBasedIdRemappingTable table = getTable(version);

				LegacyTypeUtils.chainRemapTable(table, MinecraftData.ITEM_COUNT);

				IntFunction<Boolean> itemTypeExistsFunc = null;
				if (version.isAfterOrEq(ProtocolVersion.MINECRAFT_1_13)) {
					itemTypeExistsFunc = id -> FlatteningItemMappingsGenerator.REGISTRY.getTable(version).getRemap(id) != -1;
				} else if (version == ProtocolVersion.MINECRAFT_1_12) {
					itemTypeExistsFunc = id -> PreFlatteningItemIdMappingsGenerator.getCombinedId(id) != -1;
				}

				if (itemTypeExistsFunc != null) {
					LegacyTypeUtils.checkTable(
						table, MinecraftData.ITEM_COUNT, itemTypeExistsFunc,
						(originalId, remappedId) -> System.err.println(MessageFormat.format(
							"[Warning] Version {0}: item {1} is mapped to {2}, but it exists at this version",
							version,
							MaterialAPI.getItemByNetworkId(originalId),
							MaterialAPI.getItemByNetworkId(remappedId)
						)),
						(originalId, remappedId) -> System.err.println(MessageFormat.format(
							"[Error] Version {0}: item {1} is mapped to {2}, which doesn''t exist at this version",
							version,
							MaterialAPI.getItemByNetworkId(originalId),
							MaterialAPI.getItemByNetworkId(remappedId)
						))
					);
				}
			}
		}

		protected void registerRemapEntry(List<Material> from, Material to, ProtocolVersion... versions) {
			from.forEach(material -> registerRemapEntry(material, to, versions));
		}

		boolean ignoreDuplicateRemaps = false;
		protected void withIgnoringDuplicateRemaps(Runnable run) {
			ignoreDuplicateRemaps = true;
			run.run();
			ignoreDuplicateRemaps = false;
		}

		protected void registerRemapEntry(Material from, Material to, ProtocolVersion... versions) {
			int fromId = MaterialAPI.getItemNetworkId(from);
			int toId = MaterialAPI.getItemNetworkId(to);
			if (!ignoreDuplicateRemaps) {
				for (ProtocolVersion version : versions) {
					int remappedId = getTable(version).getRemap(fromId);
					if (remappedId != fromId) {
						System.err.println(MessageFormat.format(
							"[Warning] Version {0}: item {1} remap is already set to {2} (Now set to {3})",
							version,
							MaterialAPI.getItemByNetworkId(fromId),
							MaterialAPI.getItemByNetworkId(remappedId),
							MaterialAPI.getItemByNetworkId(toId)
						));
					}
				}
			}
			registerRemapEntry(MaterialAPI.getItemNetworkId(from), MaterialAPI.getItemNetworkId(to), versions);
		}

		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			return new ArrayBasedIdRemappingTable(MinecraftData.ITEM_COUNT);
		}

	}


	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			ArrayBasedIdRemappingTable table = REGISTRY.getTable(version);
			JsonObject versionObject = new JsonObject();
			for (int originalId = 0; originalId < MinecraftData.ITEM_COUNT; originalId++) {
				int remappedId = table.getRemap(originalId);
				if (originalId != remappedId) {
					versionObject.addProperty(String.valueOf(originalId), remappedId);
				}
			}
			rootObject.add(version.toString(), versionObject);
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "legacyitemtype.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
