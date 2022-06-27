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
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.HUD;

import java.lang.invoke.MethodHandles;

public class Loader {

    public static void init() {
        // Event Listeners
        MeteorClient.EVENT_BUS.registerLambdaFactory("me.bedtrapteam.addon.modules", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        MeteorClient.EVENT_BUS.registerLambdaFactory("me.bedtrapteam.addon.utils", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));


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
                new LogSpots(),

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

    }
}
