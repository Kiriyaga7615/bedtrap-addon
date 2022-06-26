package me.bedtrapteam.addon.modules.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.modules.HudElement;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> flipOrder = sgGeneral.add(new BoolSetting.Builder().name("flip-order").description("Flips the order of armor items.").defaultValue(true).build());
    private final Setting<Durability> durability = sgGeneral.add(new EnumSetting.Builder<Durability>().name("durability").description("How to display armor durability.").defaultValue(Durability.Percentage).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("scale").description("The scale.").defaultValue(2.4).min(1).sliderRange(1, 5).build());

    public ArmorHud(HUD hud) {
        super(hud, "armor-hud", "Displays information about your armor.");
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * scale.get() * 4 + 2 * 4, 16 * scale.get());

    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();
        double armorX;
        double armorY;

        MatrixStack matrices = RenderSystem.getModelViewStack();

        matrices.push();
        matrices.scale(scale.get().floatValue(), scale.get().floatValue(), 1);

        int slot = flipOrder.get() ? 3 : 0;
        for (int position = 0; position < 4; position++) {
            ItemStack itemStack = getItem(slot);

            armorX = x / scale.get() + position * 18;
            armorY = y / scale.get();


            RenderUtils.drawItem(itemStack, (int) armorX, (int) armorY, (itemStack.isDamageable()));

            if (itemStack.isDamageable() && !isInEditor() && durability.get() != Durability.None) {
                String message = switch (durability.get()) {
                    case Total -> Integer.toString(itemStack.getMaxDamage() - itemStack.getDamage());
                    case Percentage -> Integer.toString(Math.round(((itemStack.getMaxDamage() - itemStack.getDamage()) * 100f) / (float) itemStack.getMaxDamage()));
                    default -> "err";
                };

                double messageWidth = renderer.textWidth(message);
                armorX = x + 18 * position * scale.get() + 8 * scale.get() - messageWidth / 2.0;
                armorY = y + (box.height - renderer.textHeight() * 2.7);


                final float g = (itemStack.getMaxDamage() - (float) itemStack.getDamage()) / itemStack.getMaxDamage();
                final float r = 1.0f - g;

                renderer.text(message, armorX, armorY, new Color(r, g, 0, 255));
            }

            if (flipOrder.get()) slot--;
            else slot++;
        }

        matrices.pop();
    }

    private ItemStack getItem(int i) {
        if (isInEditor()) {
            return switch (i) {
                default -> Items.NETHERITE_BOOTS.getDefaultStack();
                case 1 -> Items.NETHERITE_LEGGINGS.getDefaultStack();
                case 2 -> Items.NETHERITE_CHESTPLATE.getDefaultStack();
                case 3 -> Items.NETHERITE_HELMET.getDefaultStack();
            };
        }

        return mc.player.getInventory().getArmorStack(i);
    }

    public enum Durability {
        None,
        Total,
        Percentage
    }
}