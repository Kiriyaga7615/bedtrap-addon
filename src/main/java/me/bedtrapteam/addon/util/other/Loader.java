package me.bedtrapteam.addon.util.other;

import me.bedtrapteam.addon.BedTrap;
import me.bedtrapteam.addon.commands.MoneyMod;
import me.bedtrapteam.addon.commands.Move;
import me.bedtrapteam.addon.modules.combat.AutoCrystal;
import me.bedtrapteam.addon.modules.combat.SilentCity;
import me.bedtrapteam.addon.modules.info.*;
import me.bedtrapteam.addon.modules.combat.*;
import me.bedtrapteam.addon.modules.hud.*;
import me.bedtrapteam.addon.modules.misc.*;
import me.bedtrapteam.addon.modules.combat.HeadProtect;
import me.bedtrapteam.addon.util.advanced.BedUtils;
import me.bedtrapteam.addon.util.advanced.CrystalUtils;
import me.bedtrapteam.addon.util.advanced.DeathUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.HUD;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Loader {
    public static String hwid = "";
    public static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static ExecutorService moduleExecutor = Executors.newFixedThreadPool(5);
    public static int integrity = 926; // 926 = false // 152 = true
    public static int loaded = 555; // 555 = false // 581 = true

    // Set the current hwid
    public static void setHwid() {  // Original: setHwid
        hwid = Authenticator.getHwid();
    }

    // Second stage of loading
    public static void init() {
        setHwid(); // set the current hwid
        //LeakWrapper.setup(); // doesn't work cause of resources which is read only
        //checkAuth(); // check auth server
        //ExternalWrapper.init(); // start external auth
        //Troller.copiumDoser(); // start background integrity checks
        integrity = 152; // set loader integrity to true (most monkey crackers will just blank this function with byte-code editing)
        //TextUtils.setEncrypt(Authenticator.unHex("68747470733a2f2f706173746562696e2e636f6d2f7261772f637a6d7776686d75"));
        postInit(); // last stage of loading
    }

    public static void postInit() {
        // Load modules
        //if (Troller.started != 16) integrity = 926; // check if the init() method was modified to skip the auth check (or do nothing)
        // we don't exit right away to provide information about whether the crack worked. TrolliusMaximus will alert us and reset their config
        // after a certain period of time.

        //Beta
        if (Wrapper.isBeta() || Wrapper.isDev()) {
            BedTrap.addModules(
                    new LogSpots()
                    );
        }

        //Modules
        BedTrap.addModules(
                // Info
                new AutoLogin(),
                new ChatEncrypt(),
                new Notifications(),
                new ChatConfig(),
                new AutoReKit(),
                new AutoExcuse(),
                new RPC(),
                new AutoEz(),
                new KillFx(),

                // Combat
                new QQuiver(),
                new AutoCrystal(),
                new AntiSurroundBlocks(),
                new AutoMinecart(),
                new BowBomb(),
                new Burrow(),
                new PistonAura(),
                new BedBomb(),
                new CevBreaker(),
                new HoleFill(),
                new SilentCity(),
                new CityBreaker(),
                new HeadProtect(),
                new OldSurround(),
                new SelfTrap(),
                new Surround(),
                new TNTAura(),
                new AutoTrap(),
                new PistonPush(),
                new AntiRegear(),
                new AnchorBomb(),

                // Misc
                new AntiRespawnLose(),
                new AutoBuild(),
                new ChestExplorer(),
                new OffHando(),
                new HandTweaks(),
                new LogOut(),
                new AntiLay(),
                new MultiTask(),
                new EFly(),
                new OldAnvil(),
                new Strafe(),
                new Phase(),
                new BedCrafter(),
                new Sevila(),
                new ChorusPredict(),
                new PistonPush(),
                new TimerFall(),
                new AutoBedTrap()
        );

        // Hud
        HUD hud = Systems.get(HUD.class);
        hud.elements.add(new BedTrapHud(hud));
        hud.elements.add(new BetterCordsHud(hud));
        hud.elements.add(new WatermarkHud(hud));
        hud.elements.add(new WelcomeHud(hud));
        hud.elements.add(new ToastNotifications(hud));
        hud.elements.add(new OnlineTimeHud(hud));
        hud.elements.add(new CPSHud(hud));
        hud.elements.add(new YawHud(hud));
        hud.elements.add(new ArmorHud(hud));

        // Commands
        Commands.get().add(new MoneyMod());
        Commands.get().add(new Move());

        loaded = 581; // set the loaded flag to true. This ensures all module states were loaded before doing Systems.save() in doExit()
    }

    public static void moduleAuth() {  // Original: moduleAuth
        // check at random
        //if (Wrapper.randomNum(1, 8) == 5) moduleExecutor.execute(Loader::checkModuleAuth);
        if (Wrapper.randomNum(1, 7) == 4) checkModuleAuth(); // thread is now executed in Authenticator.java
    }

    // Regular Auth
    public static void checkAuth() {  // Original: checkAuth
        // use non threaded check on init to prevent loading anything else until auth is confirmed
        Authenticator.doCheck(Authenticator.getAuthUrl()); // initial notification comes from external auth now
        MeteorClient.EVENT_BUS.registerLambdaFactory("me.bedtrapteam.addon", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup())); // event handler
        //Authenticator.check();
        BedUtils.init();
        CrystalUtils.init();
        DeathUtils.init();
    }

    // Per-Module Auth
    public static void checkModuleAuth() {
        Authenticator.checkModule();
    }  // Original: checkModuleAuth

    // Shutdown background auth threads
    public static void shutdown() { // Original: shutdown
        executor.shutdown();
        moduleExecutor.shutdown();
    }

    public static void exit(String exitMessage) {
        Authenticator.sendTamperEmbed("Joined User - " + mc.getSession().getUsername(), false, exitMessage);
        executor.execute(doExit(exitMessage));
    } // Original: exit

    public static Runnable doExit(String exitMessage) { // Original: doExit
        BedTrap.log(exitMessage);
        if (loaded == 581) Systems.save(); // only save if the modules loaded, to try avoiding wiping config
        try {
            Thread.sleep(1500);
        } catch (Exception ignored) {
        } // need to sleep bc it caused errors with printing the exit message if we didn't
        executor.shutdown();
        System.exit(0);
        return null;
    }
}
