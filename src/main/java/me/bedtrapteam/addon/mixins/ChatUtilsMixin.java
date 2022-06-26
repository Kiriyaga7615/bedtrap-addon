package me.bedtrapteam.addon.mixins;

import me.bedtrapteam.addon.modules.info.ChatConfig;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatUtils.class)
public class ChatUtilsMixin {
    @Inject(method = "getPrefix", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getPrefix(CallbackInfoReturnable<BaseText> cir) {
        if (!Modules.get().isActive(ChatConfig.class)) return;
        if (Modules.get().get(ChatConfig.class).mode.get() != ChatConfig.Mode.BedTrap) {
            BaseText PREFIX = new LiteralText(Modules.get().get(ChatConfig.class).mode.get() == ChatConfig.Mode.Clear ? "" : Modules.get().get(ChatConfig.class).text.get());
            BaseText prefix = new LiteralText("");
            PREFIX.setStyle(PREFIX.getStyle().withFormatting(Formatting.RED));
            prefix.setStyle(prefix.getStyle().withFormatting(Formatting.RED));
            prefix.append(Modules.get().get(ChatConfig.class).mode.get() == ChatConfig.Mode.Clear ? "" : "[");
            prefix.append(PREFIX);
            prefix.append(Modules.get().get(ChatConfig.class).mode.get() == ChatConfig.Mode.Clear ? "" : "] ");
            cir.setReturnValue(prefix);
        }
    }
}
