package protocolsupportresourcesgenerator.generators.mappings.block;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bell;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Fire;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Observer;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.Scaffolding;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.block.data.type.Wall;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.craftbukkit.v1_16_R1.block.data.CraftBlockData;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.server.v1_16_R1.BlockBed;
import net.minecraft.server.v1_16_R1.BlockBell;
import net.minecraft.server.v1_16_R1.BlockProperties;
import protocolsupportresourcesgenerator.generators.mappings.LegacyTypeUtils;
import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;
import protocolsupportresourcesgenerator.version.ProtocolVersionsHelper;

public class LegacyBlockDataMappingsGenerator {

	public static final BlockIdRemappingRegistry REGISTRY = new BlockIdRemappingRegistry();

	public static class BlockIdRemappingRegistry extends IdRemappingRegistry<ArrayBasedIdRemappingTable> {

		public BlockIdRemappingRegistry() {
			applyDefaultRemaps();
		}

		protected static final BlockFace[] blockface_nsew = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

		public boolean isWallUp(Wall wall) {
			return ((CraftBlockData) wall).getState().get(BlockProperties.G);
		}

		protected Wall setWallUp(Wall wall, boolean up) {
			((CraftBlockData) wall).set(BlockProperties.G, up);
			return wall;
		}

		public boolean isBellPowered(Bell bell) {
			return ((CraftBlockData) bell).getState().get(BlockBell.c);
		}

		public Bell setBellPowered(Bell bell, boolean powered) {
			((CraftBlockData) bell).set(BlockBell.c, powered);
			return bell;
		}

		public Bed setBedOccupied(Bed bed, boolean occupied) {
			((CraftBlockData) bed).set(BlockBed.OCCUPIED, occupied);
			return bed;
		}

		protected Gate toPreFlatteningGate(Gate from, Gate to) {
			clonePowerable(from, to);
			to.setInWall(false);
			to.setFacing(from.getFacing());
			to.setOpen(from.isOpen());
			return to;
		}

		protected Switch toPreFlatteningSwitch(Switch from, Switch to, boolean hasWest) {
			clonePowerable(from, to);
			to.setAttachedFace(from.getAttachedFace());
			if ((from.getAttachedFace() == AttachedFace.CEILING) || (from.getAttachedFace() == AttachedFace.FLOOR)) {
				if (hasWest) {
					switch (from.getFacing()) {
						case NORTH:
						case SOUTH: {
							to.setFacing(BlockFace.NORTH);
							break;
						}
						case WEST:
						case EAST: {
							to.setFacing(BlockFace.WEST);
							break;
						}
						default: {
							break;
						}
					}
				} else {
					to.setFacing(BlockFace.NORTH);
				}
			} else {
				to.setFacing(from.getFacing());
			}
			return to;
		}

		protected Door toPreFlatteningDoor(Door from, Door to) {
			if (from.getHalf() == Half.TOP) {
				to.setHalf(Half.TOP);
				to.setHinge(from.getHinge());
				to.setPowered(from.isPowered());
				to.setFacing(BlockFace.EAST);
				to.setOpen(false);
			} else if (from.getHalf() == Half.BOTTOM) {
				to.setHalf(Half.BOTTOM);
				to.setHinge(Hinge.RIGHT);
				to.setPowered(false);
				to.setFacing(from.getFacing());
				to.setOpen(from.isOpen());
			}
			return to;
		}

		protected Slab toPreFlatteningSlab(Slab from, Slab to) {
			to.setWaterlogged(false);
			to.setType(from.getType());
			return to;
		}

		protected Stairs toPreFlatteningStairs(Stairs from, Stairs to) {
			to.setShape(Shape.STRAIGHT);
			to.setWaterlogged(false);
			to.setFacing(from.getFacing());
			to.setHalf(from.getHalf());
			return to;
		}

		protected TrapDoor toPreFlatteningTrapDoor(TrapDoor from, TrapDoor to) {
			to.setWaterlogged(false);
			to.setPowered(false);
			to.setFacing(from.getFacing());
			to.setOpen(from.isOpen());
			to.setHalf(from.getHalf());
			return to;
		}

		protected Wall to15Wall(Wall from, Wall to) {
			for (BlockFace face : blockface_nsew) {
				to.setHeight(face, from.getHeight(face) == Wall.Height.NONE ? Wall.Height.NONE : Wall.Height.LOW);
			}
			cloneWaterlogged(from, to);
			setWallUp(to, isWallUp(from));
			return to;
		}

		protected final Predicate<Wall> is15Wall = wall -> {
			for (BlockFace face : blockface_nsew) {
				Wall.Height height = wall.getHeight(face);
				if ((height != Wall.Height.NONE) && (height != Wall.Height.LOW)) {
					return false;
				}
			}
			return true;
		};

		protected Ageable cloneAgeable(Ageable from, Ageable to) {
			to.setAge((from.getAge() * to.getMaximumAge()) / from.getMaximumAge());
			return to;
		}

		protected Levelled cloneLevelled(Levelled from, Levelled to) {
			to.setLevel((from.getLevel() * to.getMaximumLevel()) / from.getMaximumLevel());
			return to;
		}

		protected Powerable clonePowerable(Powerable from, Powerable to) {
			to.setPowered(from.isPowered());
			return to;
		}

		protected Rotatable cloneRotatable(Rotatable from, Rotatable to) {
			to.setRotation(from.getRotation());
			return to;
		}

		protected Directional cloneDirectional(Directional from, Directional to) {
			BlockFace face = from.getFacing();
			if (to.getFaces().contains(face)) {
				to.setFacing(face);
			} else {
				to.setFacing(to.getFaces().iterator().next());
			}
			return to;
		}

		protected Orientable cloneOrientable(Orientable from, Orientable to) {
			to.setAxis(from.getAxis());
			return to;
		}

		protected MultipleFacing cloneMultipleFacting(MultipleFacing from, MultipleFacing to) {
			for (BlockFace face : to.getAllowedFaces()) {
				to.setFace(face, from.hasFace(face));
			}
			return to;
		}

		protected Openable cloneOpenable(Openable from, Openable to) {
			to.setOpen(from.isOpen());
			return to;
		}

		protected Bisected cloneBisected(Bisected from, Bisected to) {
			to.setHalf(from.getHalf());
			return to;
		}

		protected Waterlogged cloneWaterlogged(Waterlogged from, Waterlogged to) {
			to.setWaterlogged(from.isWaterlogged());
			return to;
		}

		protected Wall cloneWall(Wall from, Wall to) {
			for (BlockFace face : blockface_nsew) {
				to.setHeight(face, from.getHeight(face));
			}
			cloneWaterlogged(from, to);
			setWallUp(to, isWallUp(from));
			return to;
		}

		protected Slab cloneSlab(Slab from, Slab to) {
			cloneWaterlogged(from, to);
			to.setType(from.getType());
			return to;
		}

		protected Stairs cloneStairs(Stairs from, Stairs to) {
			cloneDirectional(from, to);
			cloneBisected(from, to);
			cloneWaterlogged(from, to);
			to.setShape(from.getShape());
			return to;
		}

		protected Switch cloneSwitch(Switch from, Switch to) {
			clonePowerable(from, to);
			cloneDirectional(from, to);
			to.setAttachedFace(from.getAttachedFace());
			return to;
		}

		protected Fence cloneFence(Fence from, Fence to) {
			cloneMultipleFacting(from, to);
			cloneWaterlogged(from, to);
			return to;
		}

		protected Gate cloneGate(Gate from, Gate to) {
			cloneDirectional(from, to);
			cloneOpenable(from, to);
			clonePowerable(from, to);
			to.setInWall(from.isInWall());
			return to;
		}

		protected TrapDoor cloneTrapDoor(TrapDoor from, TrapDoor to) {
			cloneDirectional(from, to);
			cloneBisected(from, to);
			cloneOpenable(from, to);
			clonePowerable(from, to);
			cloneWaterlogged(from, to);
			return to;
		}

		protected Door cloneDoor(Door from, Door to) {
			cloneDirectional(from, to);
			cloneBisected(from, to);
			cloneOpenable(from, to);
			clonePowerable(from, to);
			to.setHinge(from.getHinge());
			return to;
		}

		protected WallSign cloneWallSign(WallSign from, WallSign to) {
			cloneDirectional(from, to);
			cloneWaterlogged(from, to);
			return to;
		}

		protected Campfire cloneCampfire(Campfire from, Campfire to) {
			cloneDirectional(from, to);
			cloneWaterlogged(from, to);
			to.setLit(from.isLit());
			to.setSignalFire(from.isSignalFire());
			return to;
		}

		protected Sign cloneSign(Sign from, Sign to) {
			cloneRotatable(from, to);
			cloneWaterlogged(from, to);
			return to;
		}


		public void applyDefaultRemaps() {
			clear();

			this.registerAllStates(Material.BASALT, Material.ANDESITE.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.POLISHED_BASALT, Material.POLISHED_ANDESITE.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(
				Arrays.asList(Material.GILDED_BLACKSTONE, Material.NETHER_GOLD_ORE),
				Material.GOLD_ORE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(Material.BLACKSTONE, Material.COBBLESTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.POLISHED_BLACKSTONE, Material.POLISHED_ANDESITE.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.POLISHED_BLACKSTONE_BRICKS, Material.STONE_BRICKS.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(
				Arrays.asList(Material.CHISELED_POLISHED_BLACKSTONE, Material.CHISELED_NETHER_BRICKS),
				Material.CHISELED_STONE_BRICKS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_NETHER_BRICKS),
				Material.CRACKED_STONE_BRICKS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Wall>registerAllStates(
				Arrays.asList(Material.BLACKSTONE_WALL, Material.POLISHED_BLACKSTONE_WALL),
				o -> to15Wall(o, (Wall) Material.COBBLESTONE_WALL.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Wall>registerAllStates(
				Material.POLISHED_BLACKSTONE_BRICK_WALL,
				o -> to15Wall(o, (Wall) Material.BRICK_WALL.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Slab>registerAllStates(
				Material.BLACKSTONE_SLAB,
				o -> cloneSlab(o, (Slab) Material.COBBLESTONE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Slab>registerAllStates(
				Material.POLISHED_BLACKSTONE_SLAB,
				o -> cloneSlab(o, (Slab) Material.ANDESITE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Slab>registerAllStates(
				Material.POLISHED_BLACKSTONE_BRICK_SLAB,
				o -> cloneSlab(o, (Slab) Material.STONE_BRICK_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Stairs>registerAllStates(
				Material.BLACKSTONE_STAIRS,
				o -> cloneStairs(o, (Stairs) Material.COBBLESTONE_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Stairs>registerAllStates(
				Material.POLISHED_BLACKSTONE_STAIRS,
				o -> cloneStairs(o, (Stairs) Material.ANDESITE_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Stairs>registerAllStates(
				Material.POLISHED_BLACKSTONE_BRICK_STAIRS,
				o -> cloneStairs(o, (Stairs) Material.STONE_BRICK_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Switch>registerAllStates(
				Material.POLISHED_BLACKSTONE_BUTTON,
				o -> cloneSwitch(o, (Switch) Material.STONE_BUTTON.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Powerable>registerAllStates(
				Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
				o -> clonePowerable(o, (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(Material.CRIMSON_FUNGUS, Material.RED_MUSHROOM.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.POTTED_CRIMSON_FUNGUS, Material.POTTED_RED_MUSHROOM.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.WARPED_FUNGUS, Material.BROWN_MUSHROOM.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.POTTED_WARPED_FUNGUS, Material.POTTED_BROWN_MUSHROOM.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(
				Arrays.asList(Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM),
				Material.MYCELIUM.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.CRIMSON_PLANKS, Material.WARPED_PLANKS),
				Material.OAK_PLANKS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Slab>registerAllStates(
				Arrays.asList(Material.CRIMSON_SLAB, Material.WARPED_SLAB),
				o -> cloneSlab(o, (Slab) Material.OAK_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Stairs>registerAllStates(
				Arrays.asList(Material.CRIMSON_STAIRS, Material.WARPED_STAIRS),
				o -> cloneStairs(o, (Stairs) Material.OAK_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Switch>registerAllStates(
				Arrays.asList(Material.CRIMSON_BUTTON, Material.WARPED_BUTTON),
				o -> cloneSwitch(o, (Switch) Material.OAK_BUTTON.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Powerable>registerAllStates(
				Arrays.asList(Material.CRIMSON_PRESSURE_PLATE, Material.WARPED_PRESSURE_PLATE),
				o -> clonePowerable(o, (Powerable) Material.OAK_PRESSURE_PLATE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Fence>registerAllStates(
				Arrays.asList(Material.CRIMSON_FENCE, Material.WARPED_FENCE),
				o -> cloneFence(o, (Fence) Material.OAK_FENCE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Gate>registerAllStates(
				Arrays.asList(Material.CRIMSON_FENCE_GATE, Material.WARPED_FENCE_GATE),
				o -> cloneGate(o, (Gate) Material.OAK_FENCE_GATE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<TrapDoor>registerAllStates(
				Arrays.asList(Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR),
				o -> cloneTrapDoor(o, (TrapDoor) Material.OAK_TRAPDOOR.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Door>registerAllStates(
				Arrays.asList(Material.CRIMSON_DOOR, Material.WARPED_DOOR),
				o -> cloneDoor(o, (Door) Material.OAK_DOOR.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Sign>registerAllStates(
				Arrays.asList(Material.CRIMSON_SIGN, Material.WARPED_SIGN),
				o -> cloneSign(o, (Sign) Material.OAK_SIGN.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<WallSign>registerAllStates(
				Arrays.asList(Material.CRIMSON_WALL_SIGN, Material.WARPED_WALL_SIGN),
				o -> cloneWallSign(o, (WallSign) Material.OAK_WALL_SIGN.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.CRIMSON_HYPHAE, Material.CRIMSON_STEM, Material.WARPED_HYPHAE, Material.WARPED_STEM),
				Material.OAK_WOOD.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.STRIPPED_CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_STEM, Material.STRIPPED_WARPED_HYPHAE, Material.STRIPPED_WARPED_STEM),
				Material.STRIPPED_OAK_WOOD.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.CRIMSON_ROOTS, Material.WARPED_ROOTS),
				Material.TALL_GRASS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.POTTED_CRIMSON_ROOTS, Material.POTTED_WARPED_ROOTS),
				Material.POTTED_FERN.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(
				Arrays.asList(Material.RESPAWN_ANCHOR, Material.ANCIENT_DEBRIS, Material.CRYING_OBSIDIAN, Material.NETHERITE_BLOCK),
				Material.OBSIDIAN.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(Material.CHAIN, Material.IRON_BARS.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(
				Arrays.asList(Material.TWISTING_VINES, Material.TWISTING_VINES_PLANT, Material. WEEPING_VINES, Material.WEEPING_VINES_PLANT),
				Material.VINE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(Material.NETHER_SPROUTS, Material.GRASS.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.QUARTZ_BRICKS, Material.CHISELED_QUARTZ_BLOCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.SOUL_TORCH, Material.TORCH.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.<Directional>registerAllStates(
				Material.SOUL_WALL_TORCH,
				o -> cloneDirectional(o, (Directional) Material.WALL_TORCH.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Campfire>registerAllStates(
				Material.SOUL_CAMPFIRE,
				o -> cloneCampfire(o, (Campfire) Material.CAMPFIRE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(Material.SOUL_FIRE, Material.FIRE.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.<Lantern>registerAllStates(
				Material.SOUL_LANTERN,
				o -> Material.LANTERN.createBlockData(to -> ((Lantern) to).setHanging(o.isHanging())),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.registerAllStates(Material.SOUL_SOIL, Material.PODZOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.SHROOMLIGHT, Material.GLOWSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.WARPED_WART_BLOCK, Material.NETHER_WART_BLOCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.LODESTONE, Material.CHISELED_STONE_BRICKS.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.registerAllStates(Material.TARGET, Material.HAY_BLOCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_15_2);
			this.<Wall>registerSomeStates(
				Arrays.asList(
					Material.ANDESITE_WALL, Material.GRANITE_WALL, Material.DIORITE_WALL, Material.MOSSY_STONE_BRICK_WALL,
					Material.RED_NETHER_BRICK_WALL, Material.END_STONE_BRICK_WALL, Material.BRICK_WALL, Material.PRISMARINE_WALL,
					Material.RED_SANDSTONE_WALL, Material.STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.SANDSTONE_WALL,
					Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL
				),
				o -> {
					for (BlockFace face : blockface_nsew) {
						Wall.Height height = o.getHeight(face);
						if (height == Wall.Height.TALL) {
							return true;
						}
					}
					return false;
				},
				o -> to15Wall(o, (Wall) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_15_2
			);
			this.<Jigsaw>registerAllStates(
				Material.JIGSAW,
				o -> {
					Jigsaw jigsaw = (Jigsaw) Material.JIGSAW.createBlockData();
					Jigsaw.Orientation orientation = o.getOrientation();
					switch (orientation) {
						case UP_EAST:
						case UP_WEST:
						case UP_SOUTH: {
							jigsaw.setOrientation(Jigsaw.Orientation.UP_NORTH);
							break;
						}
						case DOWN_EAST:
						case DOWN_WEST:
						case DOWN_SOUTH: {
							jigsaw.setOrientation(Jigsaw.Orientation.DOWN_NORTH);
							break;
						}
						default: {
							jigsaw.setOrientation(orientation);
							break;
						}
					}
					return jigsaw;
				},
				ProtocolVersionsHelper.DOWN_1_15_2
			);

			this.registerAllStates(Material.HONEY_BLOCK, Material.GRASS_PATH.createBlockData(), ProtocolVersionsHelper.DOWN_1_14_4);
			this.registerAllStates(Material.HONEYCOMB_BLOCK, Material.HAY_BLOCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_14_4);
			this.registerAllStates(
				Arrays.asList(Material.BEEHIVE, Material.BEE_NEST),
				Material.JUNGLE_WOOD.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_14_4
			);
			this.<Bell>registerAllStates(
				Material.BELL,
				o -> {
					Bell bell = (Bell) o.getMaterial().createBlockData();
					cloneDirectional(o, bell);
					bell.setAttachment(o.getAttachment());
					setBellPowered(bell, false);
					return bell;
				},
				ProtocolVersionsHelper.DOWN_1_14_4
			);

			this.<Directional>registerAllStates(
				Material.BARREL,
				o -> cloneDirectional(o, (Directional) Material.FURNACE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Bell>registerSomeStates(
				Material.BELL,
				o -> !isBellPowered(o),
				Material.NOTE_BLOCK.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Directional>registerAllStates(
				Arrays.asList(Material.BLAST_FURNACE, Material.SMOKER),
				o -> cloneDirectional(o, (Directional) Material.FURNACE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Campfire>registerAllStates(
				Material.CAMPFIRE,
				o -> Material.OAK_SLAB.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.registerAllStates(
				Arrays.asList(Material.CARTOGRAPHY_TABLE, Material.FLETCHING_TABLE, Material.LOOM, Material.SMITHING_TABLE),
				Material.CRAFTING_TABLE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Levelled>registerAllStates(
				Material.COMPOSTER,
				o -> cloneLevelled(o, (Levelled) Material.CAULDRON.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Directional>registerAllStates(
				Arrays.asList(Material.GRINDSTONE, Material.STONECUTTER),
				o -> cloneDirectional(o, (Directional) Material.ANVIL.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.withIgnoringDuplicateRemaps(() -> this.registerAllStates(Material.JIGSAW, Material.BEDROCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2));
			this.registerAllStates(Material.LANTERN, Material.GLOWSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.LECTERN, Material.BOOKSHELF.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.<Scaffolding>registerAllStates(
				Material.SCAFFOLDING,
				o -> o.isBottom() ? Material.OAK_PLANKS.createBlockData() : Material.LADDER.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.registerAllStates(Material.CORNFLOWER, Material.BLUE_ORCHID.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.LILY_OF_THE_VALLEY, Material.AZURE_BLUET.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.WITHER_ROSE, Material.POPPY.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.BAMBOO, Material.SUGAR_CANE.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.POTTED_CORNFLOWER, Material.POTTED_BLUE_ORCHID.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.POTTED_LILY_OF_THE_VALLEY, Material.POTTED_AZURE_BLUET.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.POTTED_WITHER_ROSE, Material.POTTED_POPPY.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.POTTED_BAMBOO, Material.POTTED_OAK_SAPLING.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.BAMBOO_SAPLING, Material.OAK_SAPLING.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.registerAllStates(Material.SWEET_BERRY_BUSH, Material.GRASS.createBlockData(), ProtocolVersionsHelper.DOWN_1_13_2);
			this.<Sign>registerAllStates(
				Arrays.asList(
					Material.ACACIA_SIGN, Material.BIRCH_SIGN, Material.DARK_OAK_SIGN,
					Material.JUNGLE_SIGN, Material.SPRUCE_SIGN
				),
				o -> Material.OAK_SIGN.createBlockData(to -> {
					Sign sign = (Sign) to;
					cloneRotatable(o, sign);
					sign.setWaterlogged(o.isWaterlogged());
				}),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<WallSign>registerAllStates(
				Arrays.asList(
					Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.DARK_OAK_WALL_SIGN,
					Material.JUNGLE_WALL_SIGN, Material.SPRUCE_WALL_SIGN
				),
				o -> Material.OAK_WALL_SIGN.createBlockData(to -> {
					WallSign sign = (WallSign) to;
					cloneDirectional(o, sign);
					sign.setWaterlogged(o.isWaterlogged());
				}),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Slab>registerAllStates(
				Arrays.asList(
					Material.ANDESITE_SLAB, Material.POLISHED_ANDESITE_SLAB, Material.GRANITE_SLAB, Material.POLISHED_GRANITE_SLAB, Material.MOSSY_STONE_BRICK_SLAB,
					Material.MOSSY_COBBLESTONE_SLAB, Material.SMOOTH_SANDSTONE_SLAB, Material.CUT_SANDSTONE_SLAB, Material.SMOOTH_RED_SANDSTONE_SLAB,
					Material.CUT_RED_SANDSTONE_SLAB, Material.SMOOTH_QUARTZ_SLAB, Material.RED_NETHER_BRICK_SLAB, Material.END_STONE_BRICK_SLAB,
					Material.DIORITE_SLAB, Material.POLISHED_DIORITE_SLAB, Material.SMOOTH_STONE_SLAB
				),
				o -> cloneSlab(o, (Slab) Material.COBBLESTONE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Stairs>registerAllStates(
				Arrays.asList(
					Material.ANDESITE_STAIRS, Material.POLISHED_ANDESITE_STAIRS, Material.GRANITE_STAIRS, Material.POLISHED_GRANITE_STAIRS,
					Material.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_COBBLESTONE_STAIRS, Material.SMOOTH_SANDSTONE_STAIRS,
					Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_QUARTZ_STAIRS, Material.RED_NETHER_BRICK_STAIRS,
					Material.END_STONE_BRICK_STAIRS, Material.DIORITE_STAIRS, Material.POLISHED_DIORITE_STAIRS, Material.STONE_STAIRS
				),
				o -> cloneStairs(o, (Stairs) Material.COBBLESTONE_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Wall>registerSomeStates(
				Arrays.asList(
					Material.ANDESITE_WALL, Material.GRANITE_WALL, Material.DIORITE_WALL,
					Material.RED_NETHER_BRICK_WALL, Material.END_STONE_BRICK_WALL, Material.BRICK_WALL, Material.PRISMARINE_WALL,
					Material.RED_SANDSTONE_WALL, Material.STONE_BRICK_WALL, Material.NETHER_BRICK_WALL, Material.SANDSTONE_WALL
				), is15Wall,
				o -> cloneWall(o, (Wall) Material.COBBLESTONE_WALL.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<Wall>registerSomeStates(
				Material.MOSSY_STONE_BRICK_WALL, is15Wall,
				o -> cloneWall(o, (Wall) Material.MOSSY_COBBLESTONE_WALL.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_13_2
			);
			this.<NoteBlock>registerAllStates(
				Material.NOTE_BLOCK,
				o -> {
					NoteBlock noteblock = (NoteBlock) o.getMaterial().createBlockData();
					clonePowerable(o, noteblock);
					noteblock.setNote(o.getNote());
					switch (o.getInstrument()) {
						case IRON_XYLOPHONE:
						case COW_BELL:
						case DIDGERIDOO:
						case BIT:
						case BANJO:
						case PLING: {
							noteblock.setInstrument(Instrument.PIANO);
							break;
						}
						default: {
							noteblock.setInstrument(o.getInstrument());
							break;
						}
					}
					return noteblock;
				},
				ProtocolVersionsHelper.DOWN_1_13_2
			);

			this.registerAllStates(Material.TNT, o -> o.getMaterial().createBlockData(), ProtocolVersionsHelper.DOWN_1_13);
			this.registerAllStates(Material.DEAD_BRAIN_CORAL, Material.BRAIN_CORAL.createBlockData(), ProtocolVersionsHelper.DOWN_1_13);
			this.registerAllStates(Material.DEAD_BUBBLE_CORAL, Material.BUBBLE_CORAL.createBlockData(), ProtocolVersionsHelper.DOWN_1_13);
			this.registerAllStates(Material.DEAD_FIRE_CORAL, Material.FIRE_CORAL.createBlockData(), ProtocolVersionsHelper.DOWN_1_13);
			this.registerAllStates(Material.DEAD_HORN_CORAL, Material.HORN_CORAL.createBlockData(), ProtocolVersionsHelper.DOWN_1_13);
			this.registerAllStates(Material.DEAD_TUBE_CORAL, Material.TUBE_CORAL.createBlockData(), ProtocolVersionsHelper.DOWN_1_13);
			this.registerAllStates(
				Arrays.asList(
					Material.BRAIN_CORAL, Material.BUBBLE_CORAL, Material.FIRE_CORAL, Material.HORN_CORAL, Material.TUBE_CORAL, Material.CONDUIT
				),
				o -> o.getMaterial().createBlockData(),
				ProtocolVersion.MINECRAFT_1_13
			);

			this.registerAllStates(
				Arrays.asList(
					Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.BIRCH_LEAVES,
					Material.JUNGLE_LEAVES, Material.SPRUCE_LEAVES, Material.OAK_LEAVES,
					Material.ACACIA_FENCE, Material.DARK_OAK_FENCE, Material.BIRCH_FENCE,
					Material.JUNGLE_FENCE, Material.SPRUCE_FENCE, Material.OAK_FENCE,
					Material.NETHER_BRICK_FENCE, Material.IRON_BARS,
					Material.CHORUS_PLANT, Material.MUSHROOM_STEM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.GLASS_PANE,
					Material.BLACK_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
					Material.GRAY_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_GRAY_STAINED_GLASS_PANE,
					Material.LIME_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE,
					Material.PURPLE_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.WHITE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE,
					Material.GRASS_BLOCK, Material.MYCELIUM, Material.PODZOL
				),
				o -> o.getMaterial().createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.withIgnoringDuplicateRemaps(() -> {
				this.registerAllStates(
					Material.NOTE_BLOCK,
					o -> o.getMaterial().createBlockData(),
					ProtocolVersionsHelper.DOWN_1_12_2
				);
			});
			this.<Directional>registerAllStates(
				Arrays.asList(
					Material.SKELETON_WALL_SKULL,
					Material.WITHER_SKELETON_WALL_SKULL,
					Material.CREEPER_WALL_HEAD,
					Material.DRAGON_WALL_HEAD,
					Material.PLAYER_WALL_HEAD,
					Material.ZOMBIE_WALL_HEAD
				),
				o -> cloneDirectional(o, (Directional) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Wall>registerSomeStates(
				Arrays.asList(Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL),
				is15Wall,
				o -> o.getMaterial().createBlockData(to -> {
					Wall wall = (Wall) to;
					for (BlockFace face : blockface_nsew) {
						wall.setHeight(face, Wall.Height.NONE);
					}
					setWallUp(wall, false);
					wall.setWaterlogged(false);
				}),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<MultipleFacing>registerAllStates(
				Material.VINE,
				o -> {
					MultipleFacing mfacing = (MultipleFacing) o.clone();
					mfacing.setFace(BlockFace.UP, true);
					return mfacing;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Gate>registerAllStates(
				Arrays.asList(
					Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.BIRCH_FENCE_GATE,
					Material.JUNGLE_FENCE_GATE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE
				),
				o -> toPreFlatteningGate(o, (Gate) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Switch>registerAllStates(
				Material.LEVER,
				o -> toPreFlatteningSwitch(o, (Switch) o.getMaterial().createBlockData(), true),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Switch>registerAllStates(
				Material.STONE_BUTTON,
				o -> toPreFlatteningSwitch(o, (Switch) o.getMaterial().createBlockData(), false),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Switch>registerAllStates(
				Arrays.asList(
					Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.BIRCH_BUTTON,
					Material.JUNGLE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON
				),
				o -> toPreFlatteningSwitch(o, (Switch) Material.OAK_BUTTON.createBlockData(), false),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Door>registerAllStates(
				Arrays.asList(
					Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.BIRCH_DOOR,
					Material.JUNGLE_DOOR, Material.OAK_DOOR, Material.SPRUCE_DOOR,
					Material.IRON_DOOR
				),
				o -> toPreFlatteningDoor(o, (Door) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Slab>registerAllStates(
				Arrays.asList(
					Material.ACACIA_SLAB, Material.DARK_OAK_SLAB, Material.BIRCH_SLAB,
					Material.JUNGLE_SLAB, Material.OAK_SLAB, Material.SPRUCE_SLAB,
					Material.COBBLESTONE_SLAB, Material.SANDSTONE_SLAB, Material.RED_SANDSTONE_SLAB,
					Material.STONE_SLAB, Material.BRICK_SLAB, Material.STONE_BRICK_SLAB,
					Material.NETHER_BRICK_SLAB, Material.QUARTZ_SLAB, Material.PURPUR_SLAB,
					Material.PETRIFIED_OAK_SLAB
				),
				o -> toPreFlatteningSlab(o, (Slab) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Slab>registerAllStates(
				Arrays.asList(Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_SLAB, Material.DARK_PRISMARINE_SLAB),
				o -> toPreFlatteningSlab(o, (Slab) Material.COBBLESTONE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Powerable>registerAllStates(
				Arrays.asList(
					Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE,
					Material.JUNGLE_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE
				),
				o -> clonePowerable(o, (Powerable) Material.OAK_PRESSURE_PLATE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Stairs>registerAllStates(
				Arrays.asList(
					Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS, Material.BIRCH_STAIRS,
					Material.JUNGLE_STAIRS, Material.OAK_STAIRS, Material.SPRUCE_STAIRS,
					Material.COBBLESTONE_STAIRS, Material.STONE_BRICK_STAIRS,
					Material.SANDSTONE_STAIRS, Material.RED_SANDSTONE_STAIRS,
					Material.QUARTZ_STAIRS, Material.NETHER_BRICK_STAIRS, Material.PURPUR_STAIRS,
					Material.BRICK_STAIRS
				),
				o -> toPreFlatteningStairs(o, (Stairs) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Stairs>registerAllStates(
				Arrays.asList(
					Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_STAIRS, Material.DARK_PRISMARINE_STAIRS
				),
				o -> toPreFlatteningStairs(o, (Stairs) Material.STONE_BRICK_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<TrapDoor>registerAllStates(
				Arrays.asList(
					Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.BIRCH_TRAPDOOR,
					Material.JUNGLE_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR
				),
				o -> toPreFlatteningTrapDoor(o, (TrapDoor) Material.OAK_TRAPDOOR.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<TrapDoor>registerAllStates(
				Material.IRON_TRAPDOOR,
				o -> toPreFlatteningTrapDoor(o, (TrapDoor) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Bed>registerAllStates(
				Arrays.asList(
					Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED, Material.CYAN_BED,
					Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED,
					Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED,
					Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED
				),
				o -> {
					Bed bed = (Bed) Material.RED_BED.createBlockData();
					bed.setFacing(o.getFacing());
					bed.setPart(o.getPart());
					setBedOccupied(bed, o.isOccupied());
					return bed;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Directional>registerAllStates(
				Arrays.asList(Material.LADDER, Material.ENDER_CHEST, Material.OAK_WALL_SIGN),
				o -> cloneDirectional(o, (Directional) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Rotatable>registerAllStates(
				Material.OAK_SIGN,
				o -> cloneRotatable(o, (Rotatable) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Chest>registerAllStates(
				Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST),
				o -> {
					EnderChest enderChest = (EnderChest) Material.ENDER_CHEST.createBlockData();
					enderChest.setWaterlogged(false);
					enderChest.setFacing(o.getFacing());
					return enderChest;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<PistonHead>registerAllStates(
				Material.PISTON_HEAD,
				o -> {
					PistonHead pistonHead = (PistonHead) o.getMaterial().createBlockData();
					pistonHead.setFacing(o.getFacing());
					pistonHead.setType(o.getType());
					return pistonHead;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Fire>registerAllStates(
				Material.FIRE,
				o -> cloneAgeable(o, (Fire) o.getMaterial().createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Tripwire>registerAllStates(
				Material.TRIPWIRE,
				o -> {
					Tripwire tripwire = (Tripwire) o.getMaterial().createBlockData();
					clonePowerable(o, tripwire);
					tripwire.setAttached(o.isAttached());
					tripwire.setDisarmed(o.isDisarmed());
					return tripwire;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<RedstoneWire>registerAllStates(
				Material.REDSTONE_WIRE,
				o -> {
					RedstoneWire wire = (RedstoneWire) o.getMaterial().createBlockData();
					wire.setPower(o.getPower());
					return wire;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Repeater>registerAllStates(
				Material.REPEATER,
				o -> {
					Repeater repeater = (Repeater) Material.REPEATER.createBlockData();
					clonePowerable(o, repeater);
					repeater.setDelay(o.getDelay());
					repeater.setFacing(o.getFacing());
					return repeater;
				},
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.registerAllStates(
				Arrays.asList(Material.CAVE_AIR, Material.VOID_AIR),
				Material.AIR.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.registerAllStates(Material.ATTACHED_MELON_STEM, o -> {
				return Material.MELON_STEM.createBlockData(data -> {
					((Ageable) data).setAge(((Ageable) data).getMaximumAge());
				});
			}, ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.ATTACHED_PUMPKIN_STEM, o -> {
				return Material.PUMPKIN_STEM.createBlockData(data -> {
					((Ageable) data).setAge(((Ageable) data).getMaximumAge());
				});
			}, ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.BLUE_ICE, Material.LIGHT_BLUE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.BUBBLE_COLUMN, Material.WATER.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.PUMPKIN, Material.CARVED_PUMPKIN.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.CONDUIT, Material.STONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(
				Arrays.asList(
					Material.BRAIN_CORAL, Material.BRAIN_CORAL_FAN, Material.BRAIN_CORAL_WALL_FAN,
					Material.BUBBLE_CORAL, Material.BUBBLE_CORAL_FAN, Material.BUBBLE_CORAL_WALL_FAN,
					Material.FIRE_CORAL, Material.FIRE_CORAL_FAN, Material.FIRE_CORAL_WALL_FAN,
					Material.HORN_CORAL, Material.HORN_CORAL_FAN, Material.HORN_CORAL_WALL_FAN,
					Material.TUBE_CORAL, Material.TUBE_CORAL_FAN, Material.TUBE_CORAL_WALL_FAN,
					Material.DEAD_BRAIN_CORAL_FAN, Material.DEAD_BRAIN_CORAL_WALL_FAN,
					Material.DEAD_BUBBLE_CORAL_FAN, Material.DEAD_BUBBLE_CORAL_WALL_FAN,
					Material.DEAD_FIRE_CORAL_FAN, Material.DEAD_FIRE_CORAL_WALL_FAN,
					Material.DEAD_HORN_CORAL_FAN, Material.DEAD_HORN_CORAL_WALL_FAN,
					Material.DEAD_TUBE_CORAL_FAN, Material.DEAD_TUBE_CORAL_WALL_FAN
				),
				Material.DANDELION.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.registerAllStates(Material.TUBE_CORAL_BLOCK, Material.BLUE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.BRAIN_CORAL_BLOCK, Material.PINK_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.BUBBLE_CORAL_BLOCK, Material.PURPLE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.FIRE_CORAL_BLOCK, Material.RED_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.HORN_CORAL_BLOCK, Material.YELLOW_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(
				Arrays.asList(
					Material.DEAD_BRAIN_CORAL_BLOCK, Material.DEAD_BUBBLE_CORAL_BLOCK, Material.DEAD_FIRE_CORAL_BLOCK,
					Material.DEAD_HORN_CORAL_BLOCK, Material.DEAD_TUBE_CORAL_BLOCK
				),
				Material.LIGHT_GRAY_WOOL.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.registerAllStates(Material.DRIED_KELP_BLOCK, Material.GREEN_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(Material.SHULKER_BOX, Material.PINK_SHULKER_BOX.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(
				Arrays.asList(Material.SEA_PICKLE, Material.TURTLE_EGG),
				Material.CAKE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.registerAllStates(Material.SEAGRASS, Material.GRASS.createBlockData(), ProtocolVersionsHelper.DOWN_1_12_2);
			this.registerAllStates(
				Arrays.asList(Material.TALL_SEAGRASS, Material.KELP, Material.KELP_PLANT),
				Material.TALL_GRASS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_ACACIA_LOG,
				o -> cloneOrientable(o, (Orientable) Material.ACACIA_LOG.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_ACACIA_WOOD,
				o -> cloneOrientable(o, (Orientable) Material.ACACIA_WOOD.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_DARK_OAK_LOG,
				o -> cloneOrientable(o, (Orientable) Material.DARK_OAK_LOG.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_DARK_OAK_WOOD,
				o -> cloneOrientable(o, (Orientable) Material.DARK_OAK_WOOD.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_BIRCH_LOG,
				o -> cloneOrientable(o, (Orientable) Material.BIRCH_LOG.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_BIRCH_WOOD,
				o -> cloneOrientable(o, (Orientable) Material.BIRCH_WOOD.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_JUNGLE_LOG,
				o -> cloneOrientable(o, (Orientable) Material.JUNGLE_LOG.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_JUNGLE_WOOD,
				o -> cloneOrientable(o, (Orientable) Material.JUNGLE_WOOD.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_SPRUCE_LOG,
				o -> cloneOrientable(o, (Orientable) Material.SPRUCE_LOG.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_SPRUCE_WOOD,
				o -> cloneOrientable(o, (Orientable) Material.SPRUCE_WOOD.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_OAK_LOG,
				o -> cloneOrientable(o, (Orientable) Material.OAK_LOG.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.<Orientable>registerAllStates(
				Material.STRIPPED_OAK_WOOD,
				o -> cloneOrientable(o, (Orientable) Material.OAK_WOOD.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_12_2
			);
			this.registerAllStates(
				Arrays.asList(
					Material.POTTED_ACACIA_SAPLING, Material.POTTED_ALLIUM, Material.POTTED_AZURE_BLUET, Material.POTTED_BIRCH_SAPLING,
					Material.POTTED_BLUE_ORCHID, Material.POTTED_BROWN_MUSHROOM, Material.POTTED_CACTUS, Material.POTTED_DANDELION,
					Material.POTTED_DARK_OAK_SAPLING, Material.POTTED_DEAD_BUSH, Material.POTTED_FERN, Material.POTTED_JUNGLE_SAPLING,
					Material.POTTED_OAK_SAPLING, Material.POTTED_ORANGE_TULIP, Material.POTTED_OXEYE_DAISY, Material.POTTED_PINK_TULIP,
					Material.POTTED_POPPY, Material.POTTED_RED_MUSHROOM, Material.POTTED_RED_TULIP, Material.POTTED_SPRUCE_SAPLING,
					Material.POTTED_WHITE_TULIP
				),
				Material.FLOWER_POT.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_12_2
			);

			this.registerAllStates(
				Arrays.asList(
					Material.BLACK_CONCRETE, Material.BLUE_CONCRETE, Material.BROWN_CONCRETE, Material.CYAN_CONCRETE,
					Material.GRAY_CONCRETE, Material.GREEN_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_GRAY_CONCRETE,
					Material.LIME_CONCRETE, Material.MAGENTA_CONCRETE, Material.ORANGE_CONCRETE, Material.PINK_CONCRETE,
					Material.PURPLE_CONCRETE, Material.RED_CONCRETE, Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE,
					Material.BLACK_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA,
					Material.GRAY_GLAZED_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
					Material.LIME_GLAZED_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA,
					Material.PURPLE_GLAZED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA, Material.WHITE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA
				),
				Material.BRICKS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_11_1
			);
			this.registerAllStates(Material.BLACK_CONCRETE_POWDER, Material.BLACK_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.BLUE_CONCRETE_POWDER, Material.BLUE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.BROWN_CONCRETE_POWDER, Material.BROWN_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.CYAN_CONCRETE_POWDER, Material.CYAN_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.GRAY_CONCRETE_POWDER, Material.GRAY_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.GREEN_CONCRETE_POWDER, Material.GREEN_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_BLUE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.LIGHT_GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.LIME_CONCRETE_POWDER, Material.LIME_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.MAGENTA_CONCRETE_POWDER, Material.MAGENTA_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.ORANGE_CONCRETE_POWDER, Material.ORANGE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.PINK_CONCRETE_POWDER, Material.PINK_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.PURPLE_CONCRETE_POWDER, Material.PURPLE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.RED_CONCRETE_POWDER, Material.RED_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.WHITE_CONCRETE_POWDER, Material.WHITE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);
			this.registerAllStates(Material.YELLOW_CONCRETE_POWDER, Material.YELLOW_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_11_1);


			this.<Observer>registerAllStates(
				Material.OBSERVER,
				o -> cloneDirectional(o, (Directional) Material.FURNACE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_10
			);
			this.registerAllStates(
				Arrays.asList(
					Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
					Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
					Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX,
					Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX
				),
				Material.FURNACE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_10
			);

			this.registerAllStates(Material.STRUCTURE_VOID, Material.GLASS.createBlockData(), ProtocolVersionsHelper.DOWN_1_9_4);
			this.registerAllStates(Material.NETHER_WART_BLOCK, Material.RED_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_9_4);
			this.registerAllStates(Material.RED_NETHER_BRICKS, Material.NETHER_BRICKS.createBlockData(), ProtocolVersionsHelper.DOWN_1_9_4);
			this.registerAllStates(Material.MAGMA_BLOCK, Material.NETHERRACK.createBlockData(), ProtocolVersionsHelper.DOWN_1_9_4);
			this.registerAllStates(Material.BONE_BLOCK, o -> {
				Slab slab = (Slab) Material.STONE_SLAB.createBlockData();
				slab.setType(Slab.Type.DOUBLE);
				return slab;
			}, ProtocolVersionsHelper.DOWN_1_9_4);


			this.registerAllStates(Material.END_GATEWAY, Material.END_PORTAL.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(Material.END_ROD, Material.GLOWSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(Material.END_STONE_BRICKS, Material.END_STONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(Material.FROSTED_ICE, Material.ICE.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(Material.GRASS_PATH, Material.FARMLAND.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(Material.STRUCTURE_BLOCK, Material.BEDROCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(Material.BEETROOTS, Material.POTATOES.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.<CommandBlock>registerAllStates(
				Arrays.asList(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK),
				o -> {
					CommandBlock data = (CommandBlock) Material.COMMAND_BLOCK.createBlockData();
					data.setFacing(BlockFace.DOWN);
					return data;
				},
				ProtocolVersionsHelper.DOWN_1_8
			);
			this.registerAllStates(Material.CHORUS_FLOWER, Material.OAK_WOOD.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.register(Material.CHORUS_PLANT.createBlockData(), Material.OAK_WOOD.createBlockData(), ProtocolVersionsHelper.DOWN_1_8);
			this.registerAllStates(
				Arrays.asList(Material.PURPUR_PILLAR, Material.PURPUR_BLOCK),
				Material.STONE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_8
			);
			this.withIgnoringDuplicateRemaps(() -> this.<Stairs>registerAllStates(
				Material.PURPUR_STAIRS,
				o -> toPreFlatteningStairs(o, (Stairs) Material.COBBLESTONE_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_8
			));
			this.withIgnoringDuplicateRemaps(() -> this.<Slab>registerAllStates(
				Material.PURPUR_SLAB,
				o -> toPreFlatteningSlab(o, (Slab) Material.COBBLESTONE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_8
			));


			this.registerAllStates(Material.SLIME_BLOCK, Material.EMERALD_BLOCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.BARRIER, Material.GLASS.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.PRISMARINE, Material.MOSSY_COBBLESTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.DARK_PRISMARINE, Material.MOSSY_COBBLESTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.PRISMARINE_BRICKS, Material.MOSSY_STONE_BRICKS.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.SEA_LANTERN, Material.GLOWSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.DAYLIGHT_DETECTOR, Material.DAYLIGHT_DETECTOR.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.withIgnoringDuplicateRemaps(() -> this.<TrapDoor>registerAllStates(//not the best remap, but we have no choice
				Material.IRON_TRAPDOOR,
				o -> toPreFlatteningTrapDoor(o, (TrapDoor) Material.OAK_TRAPDOOR.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			));
			this.<Rotatable>registerAllStates(
				Arrays.asList(
					Material.BLACK_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER, Material.CYAN_BANNER,
					Material.GRAY_BANNER, Material.GREEN_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIGHT_GRAY_BANNER,
					Material.LIME_BANNER, Material.MAGENTA_BANNER, Material.ORANGE_BANNER, Material.PINK_BANNER,
					Material.PURPLE_BANNER, Material.RED_BANNER, Material.WHITE_BANNER, Material.YELLOW_BANNER
				),
				o -> cloneRotatable(o, (Rotatable) Material.OAK_SIGN.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			);
			this.<Directional>registerAllStates(
				Arrays.asList(
					Material.BLACK_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_WALL_BANNER, Material.CYAN_WALL_BANNER,
					Material.GRAY_WALL_BANNER, Material.GREEN_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER,
					Material.LIME_WALL_BANNER, Material.MAGENTA_WALL_BANNER, Material.ORANGE_WALL_BANNER, Material.PINK_WALL_BANNER,
					Material.PURPLE_WALL_BANNER, Material.RED_WALL_BANNER, Material.WHITE_WALL_BANNER, Material.YELLOW_WALL_BANNER
				),
				o -> cloneDirectional(o, (Directional) Material.OAK_WALL_SIGN.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			);
			this.withIgnoringDuplicateRemaps(() -> this.<Gate>registerAllStates(
				Arrays.asList(
					Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.BIRCH_FENCE_GATE,
					Material.JUNGLE_FENCE_GATE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE
				),
				o -> toPreFlatteningGate(o, (Gate) Material.OAK_FENCE_GATE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			));
			this.registerStates(
				Arrays.asList(
					Material.ACACIA_FENCE.createBlockData(), Material.DARK_OAK_FENCE.createBlockData(), Material.BIRCH_FENCE.createBlockData(),
					Material.JUNGLE_FENCE.createBlockData(), Material.SPRUCE_FENCE.createBlockData(), Material.OAK_FENCE.createBlockData()
				),
				Material.OAK_FENCE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_7_10
			);
			this.withIgnoringDuplicateRemaps(() -> this.<Door>registerAllStates(
				Arrays.asList(
					Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.BIRCH_DOOR,
					Material.JUNGLE_DOOR, Material.OAK_DOOR, Material.SPRUCE_DOOR
				),
				o -> toPreFlatteningDoor(o, (Door) Material.OAK_DOOR.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			));
			this.registerAllStates(Material.RED_SAND, Material.SAND.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.RED_SANDSTONE, Material.SANDSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_SANDSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.CHISELED_RED_SANDSTONE, Material.CHISELED_SANDSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.registerAllStates(Material.CUT_RED_SANDSTONE, Material.CUT_SANDSTONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_7_10);
			this.withIgnoringDuplicateRemaps(() -> this.<Slab>registerAllStates(
				Material.RED_SANDSTONE_SLAB,
				o -> toPreFlatteningSlab(o, (Slab) Material.SANDSTONE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			));
			this.withIgnoringDuplicateRemaps(() -> this.<Stairs>registerAllStates(
				Material.RED_SANDSTONE_STAIRS,
				o -> toPreFlatteningStairs(o, (Stairs) Material.SANDSTONE_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_7_10
			));

			this.registerAllStates(
				Material.TALL_GRASS,
				Material.GRASS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			);
			this.registerAllStates(Material.PACKED_ICE, Material.BLUE_WOOL.createBlockData(), ProtocolVersionsHelper.DOWN_1_6_4);
			this.registerAllStates(
				Arrays.asList(Material.ACACIA_LOG, Material.DARK_OAK_LOG),
				Material.OAK_LOG.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			);
			this.withIgnoringDuplicateRemaps(() -> this.registerAllStates(
				Arrays.asList(Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS),
				Material.OAK_STAIRS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			));
			this.withIgnoringDuplicateRemaps(() -> this.registerAllStates(
				Arrays.asList(Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES),
				Material.OAK_LEAVES.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			));
			this.registerAllStates(
				Arrays.asList(
					Material.BLACK_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.BROWN_STAINED_GLASS, Material.CYAN_STAINED_GLASS,
					Material.GRAY_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS,
					Material.LIME_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.PINK_STAINED_GLASS,
					Material.PURPLE_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.WHITE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS
				),
				Material.GLASS.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			);
			this.withIgnoringDuplicateRemaps(() -> this.registerAllStates(
				Arrays.asList(
					Material.BLACK_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
					Material.GRAY_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_GRAY_STAINED_GLASS_PANE,
					Material.LIME_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE,
					Material.PURPLE_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.WHITE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE
				),
				Material.GLASS_PANE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			));
			this.registerAllStates(
				Arrays.asList(
					Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP,
					Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
					Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.LARGE_FERN
				),
				Material.DANDELION.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_6_4
			);


			this.registerAllStates(Material.HAY_BLOCK, Material.STONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_5_2);
			this.registerAllStates(Material.COAL_BLOCK, Material.OBSIDIAN.createBlockData(), ProtocolVersionsHelper.DOWN_1_5_2);
			this.registerAllStates(
				Arrays.asList(
					Material.BLACK_CARPET, Material.BLUE_CARPET, Material.BROWN_CARPET, Material.CYAN_CARPET,
					Material.GRAY_CARPET, Material.GREEN_CARPET, Material.LIGHT_BLUE_CARPET, Material.LIGHT_GRAY_CARPET,
					Material.LIME_CARPET, Material.MAGENTA_CARPET, Material.ORANGE_CARPET, Material.PINK_CARPET,
					Material.PURPLE_CARPET, Material.RED_CARPET, Material.WHITE_CARPET, Material.YELLOW_CARPET
				),
				Material.SNOW.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_5_2
			);
			this.registerAllStates(
				Arrays.asList(
					Material.TERRACOTTA,
					Material.BLACK_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.CYAN_TERRACOTTA,
					Material.GRAY_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
					Material.LIME_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.PINK_TERRACOTTA,
					Material.PURPLE_TERRACOTTA, Material.RED_TERRACOTTA, Material.WHITE_TERRACOTTA, Material.YELLOW_TERRACOTTA
				),
				Material.STONE.createBlockData(),
				ProtocolVersionsHelper.DOWN_1_5_2
			);


			this.registerAllStates(Material.QUARTZ_BLOCK, Material.STONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_4_7);
			this.registerAllStates(Material.QUARTZ_PILLAR, Material.STONE.createBlockData(), ProtocolVersionsHelper.DOWN_1_4_7);
			this.registerAllStates(Material.NETHER_QUARTZ_ORE, Material.COAL_ORE.createBlockData(), ProtocolVersionsHelper.DOWN_1_4_7);
			this.registerAllStates(Material.REDSTONE_BLOCK, Material.EMERALD_BLOCK.createBlockData(), ProtocolVersionsHelper.DOWN_1_4_7);
			this.registerAllStates(Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL.createBlockData(), ProtocolVersionsHelper.DOWN_1_4_7);
			this.register(Material.DAYLIGHT_DETECTOR.createBlockData(), Material.COBBLESTONE_SLAB.createBlockData(), ProtocolVersionsHelper.DOWN_1_4_7);
			this.<AnaloguePowerable>registerAllStates(
				Arrays.asList(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE),
				o -> {
					Powerable powerable = (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData();
					powerable.setPowered(o.getPower() == o.getMaximumPower());
					return powerable;
				}, ProtocolVersionsHelper.DOWN_1_4_7
			);
			this.<Comparator>registerAllStates(
				Material.COMPARATOR,
				o -> clonePowerable(o, (Repeater) Material.REPEATER.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_4_7
			);
			this.withIgnoringDuplicateRemaps(() -> this.<Stairs>registerAllStates(
				Material.QUARTZ_STAIRS,
				o -> toPreFlatteningStairs(o, (Stairs) Material.COBBLESTONE_STAIRS.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_4_7
			));
			this.withIgnoringDuplicateRemaps(() -> this.<Slab>registerAllStates(
				Material.QUARTZ_SLAB,
				o -> toPreFlatteningSlab(o, (Slab) Material.COBBLESTONE_SLAB.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_4_7
			));
			this.withIgnoringDuplicateRemaps(() -> this.<Directional>registerAllStates(
				Material.TRAPPED_CHEST,
				o -> cloneDirectional(o, (Directional) Material.ENDER_CHEST.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_4_7
			));
			this.<Directional>registerAllStates(
				Arrays.asList(Material.DROPPER, Material.HOPPER),
				o -> cloneDirectional(o, (Directional) Material.FURNACE.createBlockData()),
				ProtocolVersionsHelper.DOWN_1_4_7
			);

			for (ProtocolVersion version : ProtocolVersionsHelper.DOWN_1_14_4) {
				ArrayBasedIdRemappingTable table = getTable(version);

				LegacyTypeUtils.chainRemapTable(table, MinecraftData.BLOCKDATA_COUNT);

				IntFunction<Boolean> blockDataExistsFunc = null;
				if (version.isAfterOrEq(ProtocolVersion.MINECRAFT_1_13)) {
					blockDataExistsFunc = id -> FlatteningBlockDataMappingsGenerator.REGISTRY.getTable(version).getRemap(id) != null;
				} else if (version == ProtocolVersion.MINECRAFT_1_12) {
					blockDataExistsFunc = id -> PreFlatteningBlockIdDataMappingsGenerator.getCombinedId(id) != -1;
				}

				if (blockDataExistsFunc != null) {
					LegacyTypeUtils.checkTable(
						table, MinecraftData.BLOCKDATA_COUNT, blockDataExistsFunc,
						(originalId, remappedId) -> System.err.println(MessageFormat.format(
							"[Warning] Version {0}: blockdata {1} is mapped to {2}, but it exists at this version",
							version,
							MaterialAPI.getBlockDataByNetworkId(originalId).getAsString(),
							MaterialAPI.getBlockDataByNetworkId(remappedId).getAsString()
						)),
						(originalId, remappedId) -> System.err.println(MessageFormat.format(
							"[Error] Version {0}: blockdata {1} is mapped to {2}, which doesn''t exist at this version",
							version,
							MaterialAPI.getBlockDataByNetworkId(originalId).getAsString(),
							MaterialAPI.getBlockDataByNetworkId(remappedId).getAsString()
						))
					);
				}
			}
		}

		protected void registerAllStates(List<Material> materials, BlockData to, ProtocolVersion... versions) {
			for (Material material : materials) {
				registerAllStates(material, to, versions);
			}
		}

		@SuppressWarnings("unchecked")
		protected <T extends BlockData> void registerSomeStates(Material from, Predicate<T> predicate, BlockData to, ProtocolVersion... versions) {
			MaterialAPI.getBlockDataList(from).stream()
			.filter((Predicate<? super BlockData>) predicate)
			.forEach(blockdata -> register(blockdata, to, versions));
		}

		protected void registerAllStates(Material from, BlockData to, ProtocolVersion... versions) {
			MaterialAPI.getBlockDataList(from)
			.forEach(blockdata -> register(blockdata, to, versions));
		}

		protected <T extends BlockData> void registerSomeStates(List<Material> materials, Predicate<T> predicate, Function<T, BlockData> remapFunc, ProtocolVersion... versions) {
			for (Material material : materials) {
				registerSomeStates(material, predicate, remapFunc, versions);
			}
		}

		protected <T extends BlockData> void registerAllStates(List<Material> materials, Function<T, BlockData> remapFunc, ProtocolVersion... versions) {
			for (Material material : materials) {
				registerAllStates(material, remapFunc, versions);
			}
		}

		@SuppressWarnings("unchecked")
		protected <T extends BlockData> void registerSomeStates(Material from, Predicate<T> predicate, Function<T, BlockData> remapFunc, ProtocolVersion... versions) {
			MaterialAPI.getBlockDataList(from).stream()
			.filter((Predicate<? super BlockData>) predicate)
			.forEach(blockdata -> register(blockdata, remapFunc.apply((T) blockdata), versions));
		}

		@SuppressWarnings("unchecked")
		protected <T extends BlockData> void registerAllStates(Material from, Function<T, BlockData> remapFunc, ProtocolVersion... versions) {
			MaterialAPI.getBlockDataList(from)
			.forEach(blockdata -> register(blockdata, remapFunc.apply((T) blockdata), versions));
		}

		protected void registerStates(List<BlockData> from, BlockData to, ProtocolVersion... versions) {
			from.forEach(blockdata -> register(blockdata, to, versions));
		}

		boolean ignoreDuplicateRemaps = false;
		protected void withIgnoringDuplicateRemaps(Runnable run) {
			ignoreDuplicateRemaps = true;
			run.run();
			ignoreDuplicateRemaps = false;
		}

		protected void register(BlockData from, BlockData to, ProtocolVersion... versions) {
			int fromId = MaterialAPI.getBlockDataNetworkId(from);
			int toId = MaterialAPI.getBlockDataNetworkId(to);
			if (!ignoreDuplicateRemaps) {
				for (ProtocolVersion version : versions) {
					int remappedId = getTable(version).getRemap(fromId);
					if (remappedId != fromId) {
						System.err.println(MessageFormat.format(
							"[Warning] Version {0}: blockdata {1} remap is already set to {2} (Now set to {3})",
							version,
							MaterialAPI.getBlockDataByNetworkId(fromId).getAsString(),
							MaterialAPI.getBlockDataByNetworkId(remappedId).getAsString(),
							MaterialAPI.getBlockDataByNetworkId(toId).getAsString()
						));
					}
				}
			}
			register(fromId, toId, versions);
		}

		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			return new ArrayBasedIdRemappingTable(MinecraftData.BLOCKDATA_COUNT);
		}
	}


	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			ArrayBasedIdRemappingTable table = REGISTRY.getTable(version);
			JsonObject versionObject = new JsonObject();
			for (int originalId = 0; originalId < MinecraftData.BLOCKDATA_COUNT; originalId++) {
				int remappedId = table.getRemap(originalId);
				if (originalId != remappedId) {
					versionObject.addProperty(String.valueOf(originalId), remappedId);
				}
			}
			rootObject.add(version.toString(), versionObject);
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "legacyblockdata.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
