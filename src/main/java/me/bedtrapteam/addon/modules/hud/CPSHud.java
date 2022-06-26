package me.bedtrapteam.addon.modules.hud;

import me.bedtrapteam.addon.modules.combat.AutoCrystal;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class CPSHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> text = sgGeneral.add(new StringSetting.Builder().name("text").description("Editable module name.").defaultValue("Crystal/Sec:").build());

    public CPSHud(HUD hud) {
        super(hud, "CPS", "Crystal per second.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        double width = 0;
        double height = 0;
        width = Math.max(width, renderer.textWidth(text.get() + " 0"));
        height += renderer.textHeight();

        box.setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX() - 0.5;
        double y = box.getY() - 0.5;
        Color textColor = new Color(255, 255, 255, 255);
        if (isInEditor()) {
            renderer.text(text.get() + " 0", x, y, textColor);
            return;
        }

        renderer.text(text.get() + " " + AutoCrystal.getCPS(), x, y, textColor);
    }
}