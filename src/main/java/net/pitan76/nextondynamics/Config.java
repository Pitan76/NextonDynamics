package net.pitan76.nextondynamics;

import net.pitan76.easyapi.FileControl;
import net.pitan76.easyapi.config.JsonConfig;
import java.io.File;

public class Config {
    private static File configDir;
    private static final String FILENAME = NextonDynamics.MOD_ID + ".json";
    private static JsonConfig config = new JsonConfig();

    public static File getConfigFile() {
        return new File(configDir, FILENAME);
    }

    public static void init(File configDir) {
        if (Config.configDir != null) return;
        Config.configDir = configDir;

        if (FileControl.fileExists(getConfigFile()))
            config.load(getConfigFile());

        defaultConfig(); // 既存設定以外をデフォルトに
        config.save(getConfigFile());
    }

    public static boolean reload() {
        if (FileControl.fileExists(getConfigFile())) {
            config.load(getConfigFile());
            return true;
        }
        return false;
    }

    public static void defaultConfig() {
        if (!config.has("energy.rebornEnergyConversionRate"))
            config.setDouble("energy.rebornEnergyConversionRate", 1.0);

        if (!config.has("energy.transferRate.energyCable"))
            config.setInt("energy.transferRate.energyCable", 512);
        if (!config.has("energy.transferRate.copperCable"))
            config.setInt("energy.transferRate.copperCable", 256);
        if (!config.has("energy.transferRate.ironCable"))
            config.setInt("energy.transferRate.ironCable", 512);
        if (!config.has("energy.transferRate.goldCable"))
            config.setInt("energy.transferRate.goldCable", 1024);
    }

    public static void save() {
        config.save(getConfigFile());
    }

    public static double getRebornEnergyConversionRate() {
        return config.getDoubleOrDefault("energy.rebornEnergyConversionRate", 1.0);
    }

    public static int getEnergyTransferRate(String cableType) {
        return config.getIntOrDefault("energy.transferRate." + cableType, switch (cableType) {
            case "energyCable" -> 512;
            case "copperCable" -> 256;
            case "ironCable" -> 512;
            case "goldCable" -> 1024;
            default -> 512;
        });
    }

    public static int getEnergyCableTransferRate() {
        return getEnergyTransferRate("energyCable");
    }

    public static void setEnergyCableTransferRate(int value) {
        config.setInt("energy.transferRate.energyCable", value);
    }

    public static int getCopperCableTransferRate() {
        return getEnergyTransferRate("copperCable");
    }

    public static void setCopperCableTransferRate(int value) {
        config.setInt("energy.transferRate.copperCable", value);
    }

    public static int getIronCableTransferRate() {
        return getEnergyTransferRate("ironCable");
    }

    public static void setIronCableTransferRate(int value) {
        config.setInt("energy.transferRate.ironCable", value);
    }

    public static int getGoldCableTransferRate() {
        return getEnergyTransferRate("goldCable");
    }

    public static void setGoldCableTransferRate(int value) {
        config.setInt("energy.transferRate.goldCable", value);
    }
}
