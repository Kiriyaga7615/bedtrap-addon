package me.bedtrapteam.addon.util.other;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Troller {
    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public static int started = 32; // 32 = false // 16 = true
    public static String lastReason;

    // Do an integrity check every minute, but wait 5 minutes after the addon is launched before starting
    // This is to troll potential crackers. If they byte-code edit out the starting auth checks, this
    // will make it seem like it was a success, but at the cost of their config :trollface:
    public static void copiumDoser() {
        started = 16;
        executor.scheduleAtFixedRate(Troller::integrityCheck, 5, 1, TimeUnit.MINUTES);
    }

    // TODO: leak check
    public static void integrityCheck() {
        // Check if they did some fuckery with the hwid client side
        if (Loader.hwid.isEmpty() || Loader.hwid.isBlank()) copium1000("hwid is empty");
        // Check if the Loader auth method was byte-code edited
        if (Loader.integrity != 152) copium1000("Loading check fail");
        // Check if the Authenticator was byte-code edited
        if (Authenticator.checked != 109) copium1000("Auth check fail");
    }

    // Send a tamper alert
    public static void copium1000(String reason) {
        // make sure to only send one alert per reason, to avoid webhook spamming
        if (!Objects.equals(lastReason, reason)) Authenticator.sendTamperEmbed(
            "Bumb detected",
            false,
            reason
        );
        // reset all module settings + toggle them off
        Modules.get().getAll().forEach(module -> module.settings.forEach(group -> group.forEach(Setting::reset)));
        new ArrayList<>(Modules.get().getActive()).forEach(Module::toggle);
    }
}
