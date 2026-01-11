package dev.akgamerz_790.fpsviewr;

import dev.akgamerz_790.fpsviewr.screen.FpsConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import dev.akgamerz_790.fpsviewr.config.FpsConfig;
import dev.akgamerz_790.fpsviewr.config.FpsConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FpsHudClient implements ClientModInitializer {
    public static FpsConfig CONFIG;
    @Override
    public void onInitializeClient() {
        CONFIG = FpsConfigManager.load();
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            HudRenderer.render(drawContext, CONFIG);
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("fps").executes(ctx -> {
                MinecraftClient.getInstance().setScreen(new FpsConfigScreen(CONFIG));
                return 1;
            }));
        });
    }
}