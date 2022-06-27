package me.bedtrapteam.addon.mixins;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ReloadableResourceManagerImpl.class)
public class ResourceManagerMixin {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void onGetResource(Identifier id, CallbackInfoReturnable<Optional<Resource>> info) {
        if (id.getNamespace().equals("bedtrap")) {
            info.setReturnValue(Optional.of(new Resource("bedtrap", () -> MeteorClient.class.getResourceAsStream("/assets/bedtrap/" + id.getPath()))));
        }
    }
}
