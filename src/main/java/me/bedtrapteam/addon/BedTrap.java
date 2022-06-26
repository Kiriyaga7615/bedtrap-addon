package me.bedtrapteam.addon;

import me.bedtrapteam.addon.util.other.Wrapper;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class BedTrap extends MeteorAddon {
    public static final String ADDON = "BedTrap";
    public static final String VERSION = Wrapper.isBeta() || Wrapper.isDev() ? "455-beta" : "454";
    public static long initTime;

    public static final Category Combat = new Category("Combat+", Items.RED_BED.getDefaultStack());
    public static final Category Misc = new Category("Misc+", Items.BLUE_BED.getDefaultStack());
    public static final Category Info = new Category("Info", Items.WHITE_BED.getDefaultStack());

    public static final Logger LOG = LogManager.getLogger();
    public static final File FOLDER = new File(System.getProperty("user.home"), "BedTrapEx");

    public static void log(String message) {
        LOG.log(Level.INFO, "[" + BedTrap.ADDON + "] " + message);
    }

    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();
        if (!FOLDER.exists()) FOLDER.mkdirs();
        log("Initializing " + ADDON + " " + VERSION);
        Wrapper.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("Saving config...");
            Config.get().save();
            log("Thanks for using " + ADDON + " " + VERSION + "! Don't forget to join our discord -> https://discord.gg/4cupzRkP29");
        }));
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(Combat);
        Modules.registerCategory(Misc);
        Modules.registerCategory(Info);
    }

    public static void addModules(Module... module) {
        for (Module module1 : module) {
            Modules.get().add(module1);
        }
    }
}
