package me.bedtrapteam.addon.modules.info;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.Collections;
import java.util.List;

public class AutoLogin extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> server = sgGeneral.add(new StringListSetting.Builder().name("server").description("Text field of server adresses.").defaultValue(Collections.emptyList()).build());
    private final Setting<List<String>> password = sgGeneral.add(new StringListSetting.Builder().name("password").description("Text field of passwords.").defaultValue(Collections.emptyList()).build());

    public AutoLogin() {
        super(BedTrap.Info, "auto-login", "Sends password on cracked servers to be authorized. Server line = pass line.");
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket)) return;

        // TODO: Getting text from a packet needs to be fixed
        if (!((GameMessageS2CPacket) event.packet).getSender().toString().contains("000000000")) return;

        String message = ((GameMessageS2CPacket) event.packet).getMessage().getString();
        if (message.contains("/l")) {
            for (String s : server.get()) {
                if (Utils.getWorldName().equals(s)) {
                    int serverIndex = server.get().indexOf(s);
                    String passIndex = password.get().get(serverIndex);
                    mc.player.sendChatMessage("/login " + passIndex);
                    break;
                }
            }
        }
    }
}
