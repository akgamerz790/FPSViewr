package dev.akgamerz_790.fpsviewr;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import dev.akgamerz_790.fpsviewr.config.FpsConfig;

public class HudRenderer {
    public static void render(DrawContext ctx, FpsConfig cfg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return;

        String text;
        if (cfg.fixed144) {
            text = "FPS: 144";
        } else {
            // Real FPS
            int fps = mc.getCurrentFps();
            text = "FPS: " + fps;
        }

        // Apply scale
        ctx.getMatrices().push();
        ctx.getMatrices().translate(cfg.x, cfg.y, 0);
        ctx.getMatrices().scale(cfg.scale, cfg.scale, 1.0f);

        // Render with shadow for readability
        ctx.drawTextWithShadow(mc.textRenderer, text, 0, 0, 0xFFFFFF);

        ctx.getMatrices().pop();
    }

    public static int getTextWidth(FpsConfig cfg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return 0;
        String text = cfg.fixed144 ? "FPS: 144" : "FPS: " + mc.getCurrentFps();
        return (int) (mc.textRenderer.getWidth(text) * cfg.scale);
    }

    public static int getTextHeight(FpsConfig cfg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return 0;
        return (int) (mc.textRenderer.fontHeight * cfg.scale);
    }
}
