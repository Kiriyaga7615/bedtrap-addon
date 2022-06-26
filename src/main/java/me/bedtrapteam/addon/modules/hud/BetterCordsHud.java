package me.bedtrapteam.addon.modules.hud;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

public class BetterCordsHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private String left1;
    private double left1Width;
    private String right1;

    private String left2;
    private double left2Width;
    private String right2;

    private final Setting<Boolean> style = sgGeneral.add(new BoolSetting.Builder().name("another-style").description("Another way to render position. More cool.").defaultValue(true).build());
    private final Setting<Integer> fakeX = sgGeneral.add(new IntSetting.Builder().name("x").description("Spoofing X coords.").defaultValue(0).sliderMin(-30000000).sliderMax(30000000).build());
    private final Setting<Integer> fakeZ = sgGeneral.add(new IntSetting.Builder().name("z").description("Spoofing Z coords.").defaultValue(0).sliderMin(-30000000).sliderMax(30000000).build());

    public BetterCordsHud(HUD hud) {
        super(hud, "better-coords", "Better player position renderer");
    }

    @Override
    public void update(HudRenderer renderer) {
        left2 = null;
        if (!style.get()) left1 = "Pos: ";
        else left1 = "XYZ: ";
        left1Width = renderer.textWidth(left1);
        if (isInEditor()) {
            right1 = "0, 0, 0";
            box.setSize(left1Width + renderer.textWidth(right1), renderer.textHeight() + 2);
            return;
        }

        Freecam freecam = Modules.get().get(Freecam.class);

        double x1 = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().x : mc.player.getX();
        double y1 = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().y - mc.player.getEyeHeight(mc.player.getPose()) : mc.player.getY();
        double z1 = freecam.isActive() ? mc.gameRenderer.getCamera().getPos().z : mc.player.getZ();

        right1 = String.format("%.1f %.1f %.1f", x1 + fakeX.get(), y1, z1 + fakeZ.get());

        if (!style.get()) {
            switch (PlayerUtils.getDimension()) {
                case Overworld -> {
                    left2 = "Nether Pos: ";
                    right2 = String.format("%.1f %.1f %.1f", (x1 + fakeX.get()) / 8.0, y1, (z1 + fakeZ.get()) / 8.0);
                }
                case Nether -> {
                    left2 = "Overworld Pos: ";
                    right2 = String.format("%.1f %.1f %.1f", (x1 + fakeX.get()) * 8.0, y1, (z1 + fakeZ.get()) * 8.0);
                }
            }
        } else {
            switch (PlayerUtils.getDimension()) {
                case Overworld -> right1 = right1 + " " + String.format("(%.1f %.1f)", (x1 + fakeX.get()) / 8.0, (z1 + fakeZ.get()) / 8.0);

                case Nether -> right1 = right1 + " " + String.format("(%.1f %.1f)", (x1 + fakeX.get()) * 8.0, (z1 + fakeZ.get()) * 8.0);
            }
        }
        double width = left1Width + renderer.textWidth(right1);

        if (left2 != null && !style.get()) {
            left2Width = renderer.textWidth(left2);
            width = Math.max(width, left2Width + renderer.textWidth(right2));
        }

        box.setSize(width, renderer.textHeight() + 2);
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (left2 != null && !style.get()) {
            renderer.text(left2, x, y, hud.primaryColor.get());
            renderer.text(right2, x + left2Width, y, hud.secondaryColor.get());
        }

        double xOffset = box.alignX(left1Width + renderer.textWidth(right1));

        renderer.text(left1, x + xOffset, y, hud.primaryColor.get());
        renderer.text(right1, x + xOffset + left1Width, y, hud.secondaryColor.get());
    }
}
