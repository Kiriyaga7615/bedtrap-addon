package me.bedtrapteam.addon.modules.hud;

import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.modules.DoubleTextHudElement;
import meteordevelopment.meteorclient.utils.misc.HorizontalDirection;
import net.minecraft.util.math.MathHelper;


public class YawHud extends DoubleTextHudElement {
    public YawHud(HUD hud) {
        super(hud, "Yaw", "Just less weird rotation render", "");
    }

    @Override
    protected String getRight() {
        HorizontalDirection dir = HorizontalDirection.get(mc.gameRenderer.getCamera().getYaw());
        setLeft(String.format("%s", dir.name));

        return String.format(" (%s)", getTowards());
    }

    private String getTowards() { return switch (MathHelper.floor((double) (mc.player.getYaw() * 8.0F / 360.0F) + 0.5D) & 7) { case 0 -> "+Z"; case 1 -> "-X +Z"; case 2 -> "-X"; case 3 -> "-X -Z"; case 4 -> "-Z"; case 5 -> "+X -Z"; case 6 -> "+X"; case 7 -> "+X +Z"; default -> "Invalid"; }; }
}