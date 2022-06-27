package me.bedtrapteam.addon.util.advanced;

import me.bedtrapteam.addon.modules.info.AutoEz;
import me.bedtrapteam.addon.modules.info.KillFx;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Init;
import meteordevelopment.meteorclient.utils.InitStage;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

import static me.bedtrapteam.addon.util.basic.EntityInfo.getName;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DeathUtils {
    private static final int DeathStatus = 3;

    // Возможность использовать евенты в утилсах
    @Init(stage = InitStage.Pre)
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(DeathUtils.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPacket(PacketEvent.Receive event) {
        if (getTargets().isEmpty()) return;

        if (!(event.packet instanceof EntityStatusS2CPacket packet)) return;
        if (packet.getStatus() != DeathStatus) return;

        Entity entity = packet.getEntity(mc.world);
        if (entity == null) return;

        if (entity instanceof PlayerEntity player && getTargets().contains(getName(player))) {
            Modules.get().get(AutoEz.class).onKill(player);
            Modules.get().get(KillFx.class).onKill(player);
        }
    }

    @EventHandler
    public void onJoin(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket)) return;

        // TODO: Getting text from a packet needs to be fixed
        String message = ((GameMessageS2CPacket) event.packet).getMessage().getString();
        if (message.contains("joined")) {
            for (String n : name) {
                if (message.contains(n)) ChatUtils.info("Developer " + Formatting.RED + n + Formatting.GRAY + " just joined the server!");
            }
            for (String n : beta) {
                if (message.contains(n)) ChatUtils.info("Beta user " + Formatting.GREEN + n + Formatting.GRAY + " just joined the server!");
            }
        }
    }

    private final String[] name = {"EurekaEffect", "Kiriyaga", "BEHA"};
    private final String[] beta = {"Skar1o", "Ya_Pank", "Cyn41k228", "Gr1dlog", "CowboyWHCrystal", "ernanto", "_Rei_Ayanami", "ImaCactus4", "popipac", "Sssnipa"};


    public static ArrayList<String> getTargets() {
        ArrayList<String> list = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            String name = module.getInfoString();

            if (module.isActive() && name != null && !list.contains(name)) list.add(name);
        }

        // мб это вызывает ошибку ConcurrentModificationException
        try {
            list.removeIf(name -> !isName(name));
        } catch (Exception exception) {
            exception.fillInStackTrace();
        }

        return list;
    }

    private static boolean isName(String string) {
        ArrayList<PlayerListEntry> playerListEntries = new ArrayList<>(mc.getNetworkHandler().getPlayerList());

        for (PlayerListEntry entry : playerListEntries) {
            if (string.contains(entry.getProfile().getName())) return true;
        }

        return false;
    }
}