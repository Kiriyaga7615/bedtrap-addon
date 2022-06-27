package me.bedtrapteam.addon.modules.misc;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;

import static me.bedtrapteam.addon.util.basic.BlockInfo.getBlock;

public class AntiRespawnLose extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> beds = sgGeneral.add(new BoolSetting.Builder().name("bed").description("Prevent from loosing Bed respawn point.").defaultValue(true).build());
    private final Setting<Boolean> anchors = sgGeneral.add(new BoolSetting.Builder().name("anchor").description("Prevent from loosing Anchor respawn point.").defaultValue(true).build());

    public AntiRespawnLose() {
        super(BedTrap.Misc, "anti-respawn-lose", "Prevent the player from losing the respawn point.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if(!(event.packet instanceof PlayerInteractBlockC2SPacket)) return;

        BlockPos blockPos = ((PlayerInteractBlockC2SPacket) event.packet).getBlockHitResult().getBlockPos();

        boolean isOver = mc.world.getDimension().bedWorks();
        boolean isNether = mc.world.getDimension().respawnAnchorWorks();

        boolean isBed = getBlock(blockPos) instanceof BedBlock;
        boolean isAnchor = getBlock(blockPos).equals(Blocks.RESPAWN_ANCHOR);

        if (beds.get() && (isBed && isOver)) event.cancel();
        if (anchors.get() && (isAnchor && isNether)) event.cancel();
    }
}
