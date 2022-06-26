package me.bedtrapteam.addon.modules.hud;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.modules.DoubleTextHudElement;

public class WatermarkHud extends DoubleTextHudElement {
    public WatermarkHud(HUD hud) {
        super(hud, "Watermark", BedTrap.ADDON + " watermark", "BedTrap ", false);
    }

    @Override
    protected String getRight() {
        return BedTrap.VERSION;
    }
}
