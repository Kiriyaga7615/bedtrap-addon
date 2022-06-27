package me.bedtrapteam.addon.util.other;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.tutorial.TutorialStep;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Wrapper {

    // first stage of loading
    public static void init() {
        Loader.init();
        Wrapper.setTitle(BedTrap.ADDON + " " + BedTrap.VERSION); // override window title
        skipTutorial();
    }

    public static void skipTutorial() { // Original: disableTutorial
        mc.getTutorialManager().setStep(TutorialStep.NONE);
    }

    public static void setTitle(String titleText) {
        Config.get().customWindowTitle.set(true);
        Config.get().customWindowTitleText.set(BedTrap.ADDON);
        mc.getWindow().setTitle(titleText);
    }

    public static int randomNum(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static String onlineTime() {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - BedTrap.initTime, "HH:mm", true);
    }
}
