package me.bedtrapteam.addon.modules.hud;

import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.utils.render.color.RainbowColor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.Identifier;

public class BedTrapHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<BedLogo> logo = sgGeneral.add(new EnumSetting.Builder<BedLogo>().name("logo").defaultValue(BedLogo.Text).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("scale").description("The scale of the logo.").defaultValue(3.5).min(0.1).sliderRange(0.1, 5).build());
    public final Setting<Boolean> chroma = sgGeneral.add(new BoolSetting.Builder().name("chroma").description("Chroma logo animation.").defaultValue(false).build());
    private final Setting<Double> chromaSpeed = sgGeneral.add(new DoubleSetting.Builder().name("factor").defaultValue(0.10).min(0.01).sliderMax(5).decimalPlaces(4).visible(chroma::get).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("color").defaultValue(new SettingColor(255, 255, 255)).visible(() -> !chroma.get()).build());

    private Identifier image = new Identifier("bedtrap", "text.png");

    private static final RainbowColor RAINBOW = new RainbowColor();

    public BedTrapHud(HUD hud) {
        super(hud, "bedtrap-logo", "You should use fabric api to see it!");
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(72 * scale.get(), 15 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        switch (logo.get()) {
            case Text -> image = new Identifier("bedtrap", "text.png");
            case OldLogo -> image = new Identifier("bedtrap", "bt.png");
            case Discord -> image = new Identifier("bedtrap", "bedtrap4k.png");
        }

        GL.bindTexture(image);
        Renderer2D.TEXTURE.begin();
        if (chroma.get()) {
            RAINBOW.setSpeed(chromaSpeed.get() / 100);
            Renderer2D.TEXTURE.texQuad(box.getX(), box.getY() - 29 * scale.get(), 70 * scale.get(), 70 * scale.get(), RAINBOW.getNext(renderer.delta));
        } else {
            Renderer2D.TEXTURE.texQuad(box.getX(), box.getY() - 29 * scale.get(), 70 * scale.get(), 70 * scale.get(), color.get());
        }
        Renderer2D.TEXTURE.render(null);
    }

    public enum BedLogo {
        Text, OldLogo, Discord
    }
}
