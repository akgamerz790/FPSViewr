package dev.akgamerz_790.fpsviewr.screen;

import dev.akgamerz_790.fpsviewr.config.FpsConfig;
import dev.akgamerz_790.fpsviewr.config.FpsConfigManager;
import dev.akgamerz_790.fpsviewr.FpsHudClient;
import dev.akgamerz_790.fpsviewr.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class FpsConfigScreen extends Screen {
    private final FpsConfig working;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    public FpsConfigScreen(FpsConfig current) {
        super(Text.literal("FPS HUD Config"));
    this.working = new FpsConfig();
    if (current != null) {
        this.working.x = current.x;
        this.working.y = current.y;
        this.working.scale = current.scale;
        this.working.fixed144 = current.fixed144;
    } else {
        // Default values
        this.working.x = 5;
        this.working.y = 5;
        this.working.scale = 1.0f;
    }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        double initialSlider = (working.scale - 0.5) / 2.5;

        // Scale Slider
        this.addDrawableChild(new SliderWidget(centerX - 100, this.height - 80, 200, 20, Text.literal("Scale"), initialSlider) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal("Scale: " + String.format("%.2f", working.scale)));
            }
            @Override
            protected void applyValue() {
                working.scale = (float) (0.5 + value * 2.5);
            }
        });

        // Save Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), btn -> {
            FpsHudClient.CONFIG.x = working.x;
            FpsHudClient.CONFIG.y = working.y;
            FpsHudClient.CONFIG.scale = working.scale;
            FpsConfigManager.save(FpsHudClient.CONFIG);
            this.close();
        }).position(centerX - 100, this.height - 40).size(98, 20).build());

        // Cancel Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), btn -> this.close())
                .position(centerX + 2, this.height - 40).size(98, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Standard background
        this.renderBackground(ctx, mouseX, mouseY, delta);
        
        // Render buttons/sliders
        super.render(ctx, mouseX, mouseY, delta);

        // Draw HUD Preview
        HudRenderer.render(ctx, working);

        // Simple Instruction Text
        ctx.drawTextWithShadow(this.textRenderer, "Drag text to move", 10, 10, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int w = HudRenderer.getTextWidth(working);
            int h = HudRenderer.getTextHeight(working);
            if (mouseX >= working.x && mouseX <= working.x + w &&
                mouseY >= working.y && mouseY <= working.y + h) {
                dragging = true;
                dragOffsetX = (int) mouseX - working.x;
                dragOffsetY = (int) mouseY - working.y;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            working.x = (int) mouseX - dragOffsetX;
            working.y = (int) mouseY - dragOffsetY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}