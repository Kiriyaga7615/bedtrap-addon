package me.bedtrapteam.addon.modules.hud;

import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ToastNotifications extends HudElement {
    private static ToastNotifications instance;

    public static ToastNotifications getInstance() {
        return instance;
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> toggleMessage = sgGeneral.add(new BoolSetting.Builder().name("toggle-message").description("Sends info about toggled modules.").defaultValue(true).build());
    public final Setting<Boolean> sound = sgGeneral.add(new BoolSetting.Builder().name("sound").defaultValue(true).build());
    public final Setting<Boolean> left = sgGeneral.add(new BoolSetting.Builder().name("left-sided").defaultValue(false).build());
    public final Setting<List<Module>> toggleList = sgGeneral.add(new ModuleListSetting.Builder().name("Modules for displaying").defaultValue(Modules.get().getGroup(BedTrap.Combat)).visible(toggleMessage::get).build());
    private final Setting<Integer> removeDelay = sgGeneral.add(new IntSetting.Builder().name("remove-delay").description("Delay to clean latest message.").defaultValue(7).min(1).sliderMax(10).build());

    public ToastNotifications(HUD hud) {
        super(hud, "toast-notifications", "Displays toast notifications on hud", true);
        instance = this;
    }

    public static ArrayList<notifications> toasts = new ArrayList<>();
    static int timer1;

    @Override
    public void update(HudRenderer renderer) {
        updator();
        double width = 0;
        double height = 0;
        width = Math.max(width, renderer.textWidth("toast-messages"));
        height += renderer.textHeight();

        box.setSize(width, height);

        //if (mc.player.isUsingItem()) addToast("Player is using");
    }

    @Override
    public void render(HudRenderer renderer) {
        try {
            Color back = new Color(50, 50, 50, 255);
            Color textColor = new Color(255, 255, 255, 255);
            updator();
            double x = box.getX() - 0.5;
            double y = box.getY() - 0.5;

            int w = (int) box.width;
            int h = (int) box.height;

            if (isInEditor()) {
                renderer.text("toast-messages", x, y, textColor);
                Renderer2D.COLOR.begin();
                Renderer2D.COLOR.quad(x, y, w, h, back);
                Renderer2D.COLOR.render(null);
                return;
            }
            int i = 0;

            if (toasts.isEmpty()) {
                String t = "";
                renderer.text(t, x + box.alignX(renderer.textWidth(t)), y, textColor);
            } else {
                for (notifications mes : toasts) {
                    notifications m;
                    m = mes;

                    double width = TextRenderer.get().getWidth(m.text) + 5;
                    double end = left.get() ? box.getX() + width : box.getX() + box.width - width;
                    double start = left.get() ? box.getX() - width : end + width;


                    if (m.pos < width)
                        m.pos = moveX(m.pos, width + 1);

                    if (m.pos > width)
                        m.pos = width;

                    if (i == 0 && timer1 >= removeDelay.get() * 140 - 100) {
                        m.pos = moveX(m.pos,-(width +1));
                    }

                    start = left.get() ? start + m.pos + 6 : start - m.pos;

                    Renderer2D.COLOR.begin();
                    Renderer2D.COLOR.quad(start - 6, y - 4, TextRenderer.get().getWidth(m.text) + 10, renderer.textHeight(), m.color);
                    Renderer2D.COLOR.quad(start - 2, y - 4, TextRenderer.get().getWidth(m.text) + 2, renderer.textHeight(), back);
                    Renderer2D.COLOR.render(null);

                    renderer.text(m.text, start, y - 5, textColor);
                    y += renderer.textHeight();
                    if (i >= 0) y += 1;
                    i++;
                }
            }
        } catch (ConcurrentModificationException e) {
            e.fillInStackTrace();
        }
    }

    public static void addToast(String text, Color color) {
        if (toasts.size() == 0) timer1 = 0;
        toasts.add(new notifications(text, color));
        MinecraftClient mc;
        mc = MinecraftClient.getInstance();
        if (ToastNotifications.getInstance().sound.get()) mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    public static void addToast(String text) {
        if (toasts.size() == 0) timer1 = 0;
        toasts.add(new notifications(text, null));
        MinecraftClient mc;
        mc = MinecraftClient.getInstance();
        if (ToastNotifications.getInstance().sound.get()) mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

    }

    public static void addToggled(String text, Color color) {
        if (toasts.size() == 0) timer1 = 0;
        toasts.add(new notifications(text, color));
    }

    public static void addToggled(Module module, String mes) {
        String nameToTitle = Utils.nameToTitle(module.name);

        toasts.removeIf(toasts -> toasts.text.endsWith("OFF"));
        toasts.removeIf(toasts -> toasts.text.endsWith("ON"));

        if (mes.contains("F")) {
            addToggled(nameToTitle + " OFF", new Color(255, 0, 0, 255));
        } else {
            addToggled(nameToTitle + " ON", new Color(0, 255, 0, 255));
        }
        if (toasts.size() == 0) timer1 = 0;
    }

    private void updator() {
        if (toasts.size() > 7) toasts.remove(0);
        if (toasts.isEmpty()) return;
        if (timer1 >= removeDelay.get() * 140) {
            toasts.remove(0);
            timer1 = 0;
        } else timer1++;
    }

    private static double moveX(double start, double end) {
        double speed = (end - start) * 0.1;

        if (speed > 0) {
            speed = Math.max(0.1, speed);
            speed = Math.min(end - start, speed);
        } else if (speed < 0) {
            speed = Math.min(-0.1, speed);
            speed = Math.max(end - start, speed);
        }
        return start + speed;
    }


    public static class notifications {
        public final String text;
        public final Color color;
        public double pos = -1;

        public notifications(String text, Color color) {
            if (color == null) color = new Color(255, 237, 0, 255);
            this.text = text;
            this.color = color;
        }
    }
}