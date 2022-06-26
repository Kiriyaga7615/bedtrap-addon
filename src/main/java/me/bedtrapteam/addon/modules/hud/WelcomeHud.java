package me.bedtrapteam.addon.modules.hud;

import me.bedtrapteam.addon.util.other.TextUtils;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.modules.DoubleTextHudElement;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class WelcomeHud extends DoubleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> text = sgGeneral.add(new StringSetting.Builder().name("text").description("Editable module name.").defaultValue("Evening, <player>, keep owning <server>").build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("color").description("Color of welcome text.").defaultValue(new SettingColor(120, 43, 153)).build());

    public WelcomeHud(HUD hud) {
        super(hud, "Welcome", "Displays a welcome message, also you can use placeholders <player>, <server>, <time>.", "");
        rightColor = color.get();
    }

    @Override
    protected String getRight() {
        return text.get()
            .replace("<player>", TextUtils.getName())
            .replace("<server>", TextUtils.getServer())
            .replace("<time>", TextUtils.getTime());
    }
}
