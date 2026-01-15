package dev.akgamerz_790.fpsviewr;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import dev.akgamerz_790.fpsviewr.config.FpsConfig;

public class HudRenderer {
    public static void render(DrawContext ctx, FpsConfig cfg) {
        // Safety check: if config or Minecraft is null, stop immediately
        if (cfg == null) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return;

        String text;
        if (cfg.fixed144) {
            text = "FPS: 144";
        } else {
            // Using accessor from MinecraftClient instead of debugHud
            int fps = mc.getCurrentFps();
            // Fallback to 0 if the game hasn't calculated FPS yet
            text = "FPS: " + Math.max(0, fps);
        }

        // Apply scale safely
        ctx.getMatrices().push();
        
        // Use translate to move the text to the config's X and Y
        ctx.getMatrices().translate(cfg.x, cfg.y, 0);
        
        // Only scale if it's a valid number
        float s = Math.max(0.1f, cfg.scale);
        ctx.getMatrices().scale(s, s, 1.0f);

        // Render the text at 0,0 relative to the translated position
        ctx.drawTextWithShadow(mc.textRenderer, text, 0, 0, 0xFFFFFF);

        ctx.getMatrices().pop();
    }

    public static int getTextWidth(FpsConfig cfg) {
        if (cfg == null) return 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return 0;
        
        String text = cfg.fixed144 ? "FPS: 144" : "FPS: " + mc.getCurrentFps();
        return (int) (mc.textRenderer.getWidth(text) * cfg.scale);
    }

    public static int getTextHeight(FpsConfig cfg) {
        if (cfg == null) return 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return 0;
        
        return (int) (mc.textRenderer.fontHeight * cfg.scale);
    }
}