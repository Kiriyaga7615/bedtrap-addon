package me.bedtrapteam.addon.modules.info;

import me.bedtrapteam.addon.BedTrap;
import me.bedtrapteam.addon.util.other.Authenticator;
import me.bedtrapteam.addon.util.other.TextUtils;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Objects;

public class ChatEncrypt extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Mode> mode = sgDefault.add(new EnumSetting.Builder<Mode>().name("mode").description("The way to use Chat Encryptor.").defaultValue(Mode.Both).build());
    private final Setting<TextUtils.Color> nameColor = sgDefault.add(new EnumSetting.Builder<TextUtils.Color>().name("name-color").description("Sets the color of names in decrypted messages.").defaultValue(TextUtils.Color.Red).build());
    private final Setting<TextUtils.Color> messageColor = sgDefault.add(new EnumSetting.Builder<TextUtils.Color>().name("message-color").description("Sets the color of message in decrypted messages.").defaultValue(TextUtils.Color.Gray).build());
    private final Setting<Boolean> proximaDecrypt = sgDefault.add(new BoolSetting.Builder().name("proxima-decrypt").description("Decrypts messages from Proxima users.").defaultValue(false).visible(ChatEncrypt::isBeta).build());

    public ChatEncrypt() {
        super(BedTrap.Info, "chat-encrypt", "Encrypts and decrypts chat messages.");
    }

    public static String[] encryptors = {"[BT] ","[CE] "};

    @EventHandler
    public void onReceiveMessage(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket) || mode.get() == Mode.Encrypt) return;
        String message = ((GameMessageS2CPacket) event.packet).getMessage().getString();

        String way;
        TextUtils.Decryptor decryptor;

        if (message.contains(encryptors[0])) {
            way = encryptors[0];
            decryptor = TextUtils.Decryptor.BedTrap;
        } else if (message.contains(encryptors[1]) && proximaDecrypt.get()) {
            way = encryptors[1];
            decryptor = TextUtils.Decryptor.Proxima;
        } else return;

        String name = getSender(message);

        message = message.substring(message.indexOf(way) + 5);
        if (message.isEmpty()) return;

        message = TextUtils.decrypt(message, decryptor);
        ChatUtils.sendMsg(Text.of(TextUtils.getColor(nameColor.get()) + name + Formatting.GRAY + ": " + TextUtils.getColor(messageColor.get()) + message));
        event.cancel();
    }

    private static boolean isBeta() {
        return Objects.equals(Authenticator.getHwid(), "aa523b96e17301badacc5c54e5990a1d12020376da3ea49be894d9ad666f2b22") ||
            Objects.equals(Authenticator.getHwid(), "271a9336660edba1f09b605d10a54bbc99ee464c35e140fb7c69fc130e656820") ||
            Objects.equals(Authenticator.getHwid(), "98ff4fb13bed0c09504dbdf803b77bd5226dc602ccf35d7ce86004cd9432bb26") ||
            Objects.equals(Authenticator.getHwid(), "3ff15f3509ca66159b9dcffd835ea99db1bc9fbc63b29c4c3f55e283f2a9d9ff") ||
            Objects.equals(Authenticator.getHwid(), "26c96537909a7f96cc36a4cb2a95c0d5b25f9fe56163be16cfb8f12eb7fafbff");
    }

    private String getSender(String message) {
        ArrayList<PlayerListEntry> entry = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
        for (PlayerListEntry player : entry) {
            if (message.contains(player.getProfile().getName())) return player.getProfile().getName();
        }

        return "null";
    }

    @EventHandler
    public void onSendMessage(SendMessageEvent event) {
        if (mode.get() == Mode.Decrypt) return;

        if (!event.message.startsWith(String.valueOf(Config.get().prefix)) && !event.message.startsWith("/")) {
            event.message = "[BT] " + TextUtils.encrypt(event.message.toLowerCase());
        }
    }

    public enum Mode {
        Encrypt, Decrypt, Both
    }
}
