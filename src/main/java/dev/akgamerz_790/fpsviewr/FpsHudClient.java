package dev.akgamerz_790.fpsviewr;

import dev.akgamerz_790.fpsviewr.screen.FpsConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import dev.akgamerz_790.fpsviewr.config.FpsConfig;
import dev.akgamerz_790.fpsviewr.config.FpsConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FpsHudClient implements ClientModInitializer {
    public static FpsConfig CONFIG;

    @Override
    public void onInitializeClient() {
        System.out.println("=== FPSViewr LOADED ===");
        
        CONFIG = FpsConfigManager.load();

        // FORCE HUD ON
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            HudRenderer.render(drawContext, CONFIG);
        });

        // TEST COMMAND - super simple
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            System.out.println("=== /fps REGISTERED ===");
            dispatcher.register(literal("fps")
                .executes(ctx -> {
                    System.out.println("=== /fps EXECUTED ===");
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        mc.execute(() -> {
                            System.out.println("=== OPENING GUI ===");
                            mc.setScreen(new FpsConfigScreen(CONFIG));
                        });
                    }
                    return 1;
                })
            );
        });
        
        // FORCE GUI ON LOAD (TEST)
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.currentScreen == null) {
            mc.execute(() -> mc.setScreen(new FpsConfigScreen(CONFIG)));
        }
    }
}
