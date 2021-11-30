package protocolsupportresourcesgenerator.version;

public class ProtocolVersionsHelper {

	public static final ProtocolVersion LATEST_PC = ProtocolVersion.getLatest(ProtocolType.PC);

	public static final ProtocolVersion[] DOWN_1_4_7 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_4_7);

	public static final ProtocolVersion[] DOWN_1_5_2 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_5_2);

	public static final ProtocolVersion[] DOWN_1_6_4 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_6_4);

	public static final ProtocolVersion[] DOWN_1_7_10 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_7_10);

	public static final ProtocolVersion[] DOWN_1_8 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_8);

	public static final ProtocolVersion[] DOWN_1_9_4 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_9_4);

	public static final ProtocolVersion[] DOWN_1_10 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_10);

	public static final ProtocolVersion[] DOWN_1_11 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_11);

	public static final ProtocolVersion[] DOWN_1_11_1 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_11_1);

	public static final ProtocolVersion[] DOWN_1_12_2 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_12_2);

	public static final ProtocolVersion[] DOWN_1_13 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_13);

	public static final ProtocolVersion[] DOWN_1_13_2 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_13_2);

	public static final ProtocolVersion[] DOWN_1_14_4 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_14_4);

	public static final ProtocolVersion[] DOWN_1_15_2 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_15_2);

	public static final ProtocolVersion[] DOWN_1_16_1 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_16_1);

	public static final ProtocolVersion[] DOWN_1_16_4 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_16_4);

	public static final ProtocolVersion[] DOWN_1_17_1 = ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_16_4);

	public static final ProtocolVersion[] ALL_PC = ProtocolVersion.getAllBetween(ProtocolVersion.getOldest(ProtocolType.PC), LATEST_PC);

	public static final ProtocolVersion[] ALL_1_12 = ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_12, ProtocolVersion.MINECRAFT_1_12_2);

}
