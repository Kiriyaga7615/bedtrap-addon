package me.bedtrapteam.addon.modules.hud;

import me.bedtrapteam.addon.util.other.Wrapper;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.modules.DoubleTextHudElement;

public class OnlineTimeHud extends DoubleTextHudElement {
    public OnlineTimeHud(HUD hud) {
        super(hud, "online-time", "", "Online for: ");
    }
    @Override
    protected String getRight() {
        return Wrapper.onlineTime();
    }
}
