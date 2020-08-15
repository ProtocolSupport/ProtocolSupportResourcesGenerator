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
						register(materialFrom, materialTo, version);
					}
				});
			});


			register(Material.CHEST, Material.CHEST, ProtocolVersionsHelper.ALL_PC);
			register(Material.TRAPPED_CHEST, Material.TRAPPED_CHEST, ProtocolVersionsHelper.ALL_PC);

			register(Material.PIGLIN_BRUTE_SPAWN_EGG, Material.PIGLIN_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_16_1);

			register(Material.NETHERITE_INGOT, Material.IRON_INGOT, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_SCRAP, Material.IRON_NUGGET, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_SHOVEL, Material.DIAMOND_SHOVEL, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_AXE, Material.DIAMOND_AXE, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_HOE, Material.DIAMOND_HOE, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_HELMET, Material.DIAMOND_HELMET, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_LEGGINGS, Material.DIAMOND_LEGGINGS, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.NETHERITE_BOOTS, Material.DIAMOND_BOOTS, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.PIGLIN_SPAWN_EGG, Material.VILLAGER_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.HOGLIN_SPAWN_EGG, Material.SPIDER_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.ZOGLIN_SPAWN_EGG, Material.CAVE_SPIDER_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.STRIDER_SPAWN_EGG, Material.PIG_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.WARPED_FUNGUS_ON_A_STICK, Material.CARROT_ON_A_STICK, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.MUSIC_DISC_PIGSTEP, Material.MUSIC_DISC_BLOCKS, ProtocolVersionsHelper.DOWN_1_15_2);
			register(Material.PIGLIN_BANNER_PATTERN, Material.SKULL_BANNER_PATTERN, ProtocolVersionsHelper.DOWN_1_15_2);

			register(Material.HONEYCOMB, Material.HAY_BLOCK, ProtocolVersionsHelper.DOWN_1_14_4);
			register(Material.HONEY_BOTTLE, Material.MUSHROOM_STEW, ProtocolVersionsHelper.DOWN_1_14_4);
			register(Material.BEE_SPAWN_EGG, Material.PARROT_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_14_4);

			register(Material.WHITE_DYE, Material.BONE_MEAL, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.BROWN_DYE, Material.COCOA_BEANS, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.BLACK_DYE, Material.INK_SAC, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.BLUE_DYE, Material.LAPIS_LAZULI, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.CAT_SPAWN_EGG, Material.OCELOT_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.FOX_SPAWN_EGG, Material.OCELOT_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.PANDA_SPAWN_EGG, Material.POLAR_BEAR_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.PILLAGER_SPAWN_EGG, Material.WITCH_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.RAVAGER_SPAWN_EGG, Material.CAVE_SPIDER_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.TRADER_LLAMA_SPAWN_EGG, Material.LLAMA_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.WANDERING_TRADER_SPAWN_EGG, Material.VILLAGER_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.LEATHER_HORSE_ARMOR, Material.IRON_HORSE_ARMOR, ProtocolVersionsHelper.DOWN_1_13_2);
			register(
				Arrays.asList(
					Material.FLOWER_BANNER_PATTERN,
					Material.CREEPER_BANNER_PATTERN,
					Material.SKULL_BANNER_PATTERN,
					Material.MOJANG_BANNER_PATTERN,
					Material.GLOBE_BANNER_PATTERN
				),
				Material.PAPER,
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			register(Material.CROSSBOW, Material.BOW, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.SWEET_BERRIES, Material.POTATO, ProtocolVersionsHelper.DOWN_1_13_2);
			register(Material.SUSPICIOUS_STEW, Material.MUSHROOM_STEW, ProtocolVersionsHelper.DOWN_1_13_2);

			register(Material.SMOOTH_QUARTZ, Material.QUARTZ_BLOCK, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.SMOOTH_RED_SANDSTONE, Material.CUT_RED_SANDSTONE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.SMOOTH_SANDSTONE, Material.CUT_SANDSTONE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.SMOOTH_STONE, Material.STONE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Arrays.asList(Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD), Material.ACACIA_LOG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Arrays.asList(Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD), Material.DARK_OAK_LOG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Arrays.asList(Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD), Material.BIRCH_LOG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Arrays.asList(Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD), Material.JUNGLE_LOG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Arrays.asList(Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD), Material.SPRUCE_LOG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Arrays.asList(Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD), Material.OAK_LOG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.DRIED_KELP, Material.POTATO, ProtocolVersionsHelper.DOWN_1_12_2);
			register(
				Arrays.asList(Material.COD_BUCKET, Material.PUFFERFISH_BUCKET, Material.SALMON_BUCKET, Material.TROPICAL_FISH_BUCKET),
				Material.WATER_BUCKET,
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			register(
				Arrays.asList(Material.COD_SPAWN_EGG, Material.PUFFERFISH_SPAWN_EGG, Material.SALMON_SPAWN_EGG, Material.TROPICAL_FISH_SPAWN_EGG),
				Material.BAT_SPAWN_EGG,
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			register(Material.DOLPHIN_SPAWN_EGG, Material.SQUID_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.DROWNED_SPAWN_EGG, Material.ZOMBIE_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.PHANTOM_SPAWN_EGG, Material.BLAZE_SPAWN_EGG, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.HEART_OF_THE_SEA, Material.LIGHT_BLUE_DYE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.NAUTILUS_SHELL, Material.LIGHT_GRAY_DYE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.PHANTOM_MEMBRANE, Material.GRAY_DYE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.SCUTE, Material.LIME_DYE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.TURTLE_HELMET, Material.CHAINMAIL_HELMET, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.TRIDENT, Material.DIAMOND_HOE, ProtocolVersionsHelper.DOWN_1_12_2);
			register(Material.DEBUG_STICK, Material.STICK, ProtocolVersionsHelper.DOWN_1_12_2);

			register(Material.WHITE_BED, Material.WHITE_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.ORANGE_BED, Material.ORANGE_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.MAGENTA_BED, Material.MAGENTA_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.LIGHT_BLUE_BED, Material.LIGHT_BLUE_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.YELLOW_BED, Material.YELLOW_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.LIME_BED, Material.LIME_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.PINK_BED, Material.PINK_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.GRAY_BED, Material.GRAY_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.LIGHT_GRAY_BED, Material.LIGHT_GRAY_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.CYAN_BED, Material.CYAN_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.BLUE_BED, Material.BLUE_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.PURPLE_BED, Material.PURPLE_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.GREEN_BED, Material.GREEN_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.BROWN_BED, Material.BROWN_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.RED_BED, Material.RED_BED, ProtocolVersionsHelper.ALL_1_12);
			register(Material.BLACK_BED, Material.BLACK_BED, ProtocolVersionsHelper.ALL_1_12);

			register(Material.KNOWLEDGE_BOOK, Material.BOOK, ProtocolVersionsHelper.DOWN_1_11_1);
			register(Material.IRON_NUGGET, Material.GOLD_NUGGET, ProtocolVersionsHelper.DOWN_1_11);

			register(Material.SHULKER_SHELL, Material.COBBLESTONE, ProtocolVersionsHelper.DOWN_1_10);
			register(Material.TOTEM_OF_UNDYING, Material.COBBLESTONE, ProtocolVersionsHelper.DOWN_1_10);

			register(Material.BEETROOT, Material.BROWN_MUSHROOM, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.BEETROOT_SOUP, Material.MUSHROOM_STEW, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.BEETROOT_SEEDS, Material.WHEAT_SEEDS, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.CHORUS_FRUIT, Material.POTATO, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.POPPED_CHORUS_FRUIT, Material.BAKED_POTATO, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.DRAGON_BREATH, Material.POTION, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.SPLASH_POTION, Material.POTION, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.LINGERING_POTION, Material.POTION, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.ELYTRA, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.END_CRYSTAL, Material.STONE, ProtocolVersionsHelper.DOWN_1_8);
			register(Material.SHIELD, Material.WOODEN_SWORD, ProtocolVersionsHelper.DOWN_1_8);
			register(
				Arrays.asList(Material.SPECTRAL_ARROW, Material.TIPPED_ARROW),
				Material.ARROW,
				ProtocolVersionsHelper.DOWN_1_8
			);
			register(
				Arrays.asList(
					Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT,
					Material.JUNGLE_BOAT, Material.SPRUCE_BOAT
				),
				Material.OAK_BOAT,
				ProtocolVersionsHelper.DOWN_1_8
			);

			register(Material.RABBIT, Material.CHICKEN, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.COOKED_RABBIT, Material.COOKED_CHICKEN, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.RABBIT_STEW, Material.MUSHROOM_STEW, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.MUTTON, Material.CHICKEN, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.COOKED_MUTTON, Material.COOKED_CHICKEN, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.PRISMARINE_SHARD, Material.STONE, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.PRISMARINE_CRYSTALS, Material.STONE, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.RABBIT_FOOT, Material.STONE, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.RABBIT_HIDE, Material.STONE, ProtocolVersionsHelper.DOWN_1_7_10);
			register(Material.ARMOR_STAND, Material.STONE, ProtocolVersionsHelper.DOWN_1_7_10);

			register(
				Arrays.asList(
					Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD,
					Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD
				),
				Material.OAK_LOG,
				ProtocolVersionsHelper.DOWN_1_6_4
			);

			register(Material.IRON_HORSE_ARMOR, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.DOWN_1_5_2);
			register(Material.GOLDEN_HORSE_ARMOR, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.DOWN_1_5_2);
			register(Material.DIAMOND_HORSE_ARMOR, Material.LEATHER_CHESTPLATE, ProtocolVersionsHelper.DOWN_1_5_2);
			register(Material.LEAD, Material.STONE, ProtocolVersionsHelper.DOWN_1_5_2);
			register(Material.NAME_TAG, Material.STONE, ProtocolVersionsHelper.DOWN_1_5_2);


			register(Material.QUARTZ, Material.FEATHER, ProtocolVersionsHelper.DOWN_1_4_7);
			register(Material.TNT_MINECART, Material.MINECART, ProtocolVersionsHelper.DOWN_1_4_7);
			register(Material.HOPPER_MINECART, Material.MINECART, ProtocolVersionsHelper.DOWN_1_4_7);
			register(Material.NETHER_BRICK, Material.CLAY_BALL, ProtocolVersionsHelper.DOWN_1_4_7);
			register(Material.TRAPPED_CHEST, Material.CHEST, ProtocolVersionsHelper.DOWN_1_4_7);

			for (ProtocolVersion version : ProtocolVersionsHelper.DOWN_1_14_4) {
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

		protected void register(List<Material> from, Material to, ProtocolVersion... versions) {
			from.forEach(material -> register(material, to, versions));
		}

		boolean ignoreDuplicateRemaps = false;
		protected void withIgnoringDuplicateRemaps(Runnable run) {
			ignoreDuplicateRemaps = true;
			run.run();
			ignoreDuplicateRemaps = false;
		}

		protected void register(Material from, Material to, ProtocolVersion... versions) {
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
			register(MaterialAPI.getItemNetworkId(from), MaterialAPI.getItemNetworkId(to), versions);
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
