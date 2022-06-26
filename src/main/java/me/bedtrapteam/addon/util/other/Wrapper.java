package me.bedtrapteam.addon.util.other;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.tutorial.TutorialStep;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Wrapper {
    public static int isLinux = 92; // 92 = false // 225 = true

    // first stage of loading
    public static void init() {
        try {
            Loader.init();
        } catch (Exception ignored) {
            Loader.exit("Error in auth");
        } // start second stage of loading
        Wrapper.setTitle(BedTrap.ADDON + " " + BedTrap.VERSION); // override window title
        skipTutorial();
        Runtime.getRuntime().addShutdownHook(new Thread(Wrapper::shutdown)); // shutdown hook
        if (!isDev())
            Authenticator.sendTamperEmbed("Joined User - " + mc.getSession().getUsername(), true, "Load check.");
    }

    // shutdown hook
    public static void shutdown() { // Original: shutdown
        Loader.shutdown(); // stop auth services
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

    public static boolean isDev() {
        return Objects.equals(Authenticator.getHwid(), "98ff4fb13bed0c09504dbdf803b77bd5226dc602ccf35d7ce86004cd9432bb26") ||
                Objects.equals(Authenticator.getHwid(), "3ff15f3509ca66159b9dcffd835ea99db1bc9fbc63b29c4c3f55e283f2a9d9ff") ||
                Objects.equals(Authenticator.getHwid(), "26c96537909a7f96cc36a4cb2a95c0d5b25f9fe56163be16cfb8f12eb7fafbff");
    }

    public static boolean isBeta() {
        return Objects.equals(Authenticator.getHwid(), "23c5fd639223fc70dce44ba04008e1372feae4e6b43447062f8f6aad8bd3a125") ||
                Objects.equals(Authenticator.getHwid(), "aa523b96e17301badacc5c54e5990a1d12020376da3ea49be894d9ad666f2b22") ||
                Objects.equals(Authenticator.getHwid(), "9ab57a02de243fb5002fcbe193deedc22b0225052258375d1aa2b70ecbe3abfa") ||
                Objects.equals(Authenticator.getHwid(), "f6a99abe2f16ddb772a69ac9b7da02a7b263b901ff2c1ab0e6c5788fbf3ea66a") ||
                Objects.equals(Authenticator.getHwid(), "8f45c05bc98277c7176f7f1c505193c39b6094bfe4dd4c5a6a430e22a12be0d7") ||
                Objects.equals(Authenticator.getHwid(), "e7e7bd52445d0ac06d766aa912912b2f8db0a8c20e0a66f477abbe2eed9a4930") ||
                Objects.equals(Authenticator.getHwid(), "8533d781acbdccf9940b44fbfb1b5648e2036fdaf609c19d6c1cfbb227117258") ||
                Objects.equals(Authenticator.getHwid(), "93ccf626730b13ab831f22c4682737e8d7be8a901a443b215639c994972df46a") ||
                Objects.equals(Authenticator.getHwid(), "0330d44628384051654f5b740cbc0e11565328e1150f60593c05104a41f0b135") ||
                Objects.equals(Authenticator.getHwid(), "0237f92e586dabb996da692d881d3fb98d6dce992b299ffedaeeb4ad55e50302") ||
                Objects.equals(Authenticator.getHwid(), "271a9336660edba1f09b605d10a54bbc99ee464c35e140fb7c69fc130e656820");
    }

    public static String onlineTime() {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - BedTrap.initTime, "HH:mm", true);
    }
}
