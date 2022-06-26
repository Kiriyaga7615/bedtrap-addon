package me.bedtrapteam.addon.modules.misc;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import static me.bedtrapteam.addon.util.basic.BlockInfo.isAir;
import static me.bedtrapteam.addon.util.basic.EntityInfo.*;

public class Phase extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>().name("mode").description("Sends to the chat message.").defaultValue(Mode.OldMethod).build());
    private final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder().name("Speed").description("The X and Z distance per clip.").defaultValue(0.1).min(0.0).max(10.0).visible(() -> mode.get() != Mode.Normal).build());

    public Phase() {
        super(BedTrap.Misc, "phase", "Lets you walk through blocks.");
    }

    private double prevX;
    private double prevY;
    private double prevZ;

    @Override
    public void onActivate() {
        assert mc.player != null;
        prevX = X(mc.player);
        prevY = Y(mc.player);
        prevZ = Z(mc.player);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        net.minecraft.client.network.ClientPlayerEntity player = mc.player;
        Vec3d forward = Vec3d.fromPolar(0.0F, player.getYaw());
        Vec3d back = Vec3d.fromPolar(0.0F, player.getYaw() - 180.0F);
        Vec3d left = Vec3d.fromPolar(0.0F, player.getYaw() - 90.0F);
        Vec3d right = Vec3d.fromPolar(0.0F, player.getYaw() - 270.0F);
        Vec3d up = Vec3d.fromPolar(player.getPitch() - 150, 0.0F);

        double tX = Math.abs(mc.player.getX() - mc.player.prevX);
        double tZ = Math.abs(mc.player.getZ() - mc.player.prevZ);
        double length = Math.sqrt(tX * tX + tZ * tZ);
        double speed = length * 20;

        double pX = X(mc.player);
        double pY = Y(mc.player);
        double pZ = Z(mc.player);

        if (notNull(mc.player))
            if (!this.mc.player.isOnGround()) return;

        if (mode.get() == Mode.OldMethod) {
            if (mc.options.forwardKey.isPressed()) {
                player.updatePosition(prevX + forward.x * distance.get(), player.getY(), prevZ + forward.z * distance.get());
                prevX = prevX + forward.x * distance.get();
                prevZ = prevZ + forward.z * distance.get();
            }
            if (this.mc.options.backKey.isPressed()) {
                player.updatePosition(prevX + back.x * distance.get(), player.getY(), prevZ + back.z * distance.get());
                prevX = prevX + back.x * distance.get();
                prevZ = prevZ + back.z * distance.get();
            }
            if (this.mc.options.leftKey.isPressed()) {
                player.updatePosition(prevX + left.x * distance.get(), player.getY(), prevZ + left.z * distance.get());
                prevX = prevX + left.x * distance.get();
                prevZ = prevZ + left.z * distance.get();
            }
            if (this.mc.options.rightKey.isPressed()) {
                player.updatePosition(prevX + right.x * distance.get(), player.getY(), prevZ + right.z * distance.get());
                prevX = prevX + right.x * distance.get();
                prevZ = prevZ + right.z * distance.get();
            }
            if (mc.options.jumpKey.isPressed()) {
                player.updatePosition(player.getX(), prevY + up.y * 0.4, player.getZ());
                prevX = prevX + up.x * distance.get();
                prevZ = prevZ + up.z * distance.get();
            }
            if (mc.options.sneakKey.isPressed()) {
                double y = MathHelper.floor(mc.player.getY()) - 0.2;
                player.updatePosition(mc.player.getX(), y, mc.player.getZ());
            }
        }
        if (mode.get() == Mode.Normal) {
            if (speed <= 1 || !isAir(getBlockPos(mc.player))) {
                if (mc.options.forwardKey.isPressed()) {
                    player.updatePosition(pX + forward.x * distance.get(), player.getY(), pZ + forward.z * distance.get());
                    pX = pX + forward.x * distance.get();
                    pZ = pZ + forward.z * distance.get();
                }
                if (this.mc.options.backKey.isPressed()) {
                    player.updatePosition(pX + back.x * distance.get(), player.getY(), pZ + back.z * distance.get());
                    pX = pX + back.x * distance.get();
                    pZ = pZ + back.z * distance.get();
                }
                if (this.mc.options.leftKey.isPressed()) {
                    player.updatePosition(pX + left.x * distance.get(), player.getY(), pZ + left.z * distance.get());
                    pX = pX + left.x * distance.get();
                    pZ = pZ + left.z * distance.get();
                }
                if (this.mc.options.rightKey.isPressed()) {
                    player.updatePosition(pX + right.x * distance.get(), player.getY(), pZ + right.z * distance.get());
                    pX = pX + right.x * distance.get();
                    pZ = pZ + right.z * distance.get();
                }
                if (mc.options.jumpKey.isPressed()) {
                    player.updatePosition(player.getX(), pY + up.y * 0.4, player.getZ());
                    pX = pX + up.x * distance.get();
                    pZ = pZ + up.z * distance.get();
                }
                if (mc.options.sneakKey.isPressed()) {
                    double y = MathHelper.floor(mc.player.getY()) - 0.2;
                    player.updatePosition(mc.player.getX(), y, mc.player.getZ());
                }
            }
        }
    }

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (mode.get() != Mode.Collide) return;
        if (event.type != CollisionShapeEvent.CollisionType.BLOCK) return;
        if (event.pos.getY() < Y(mc.player)) {
            if (mc.player.isSneaking()) {
                event.shape = VoxelShapes.empty();
            }
        } else {
            event.shape = VoxelShapes.empty();
        }
    }

    public enum Mode {
        OldMethod, Normal, Collide
    }
}
