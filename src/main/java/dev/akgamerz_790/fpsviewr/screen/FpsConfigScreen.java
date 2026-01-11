package dev.akgamerz_790.fpsviewr.screen;

import dev.akgamerz_790.fpsviewr.config.FpsConfig;
import dev.akgamerz_790.fpsviewr.config.FpsConfigManager;
import dev.akgamerz_790.fpsviewr.HudRenderer;
import dev.akgamerz_790.fpsviewr.FpsHudClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class FpsConfigScreen extends Screen {
    private final FpsConfig working;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    private ButtonWidget saveButton;
    private ButtonWidget cancelButton;
    private SliderWidget scaleSlider;

    public FpsConfigScreen(FpsConfig current) {
        super(Text.literal("FPS HUD Config"));
        // Work on a copy so Cancel works
        this.working = new FpsConfig();
        this.working.x = current.x;
        this.working.y = current.y;
        this.working.scale = current.scale;
        this.working.fixed144 = current.fixed144;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        // 1. Calculate the initial normalized value (0.0 to 1.0) for the slider
        double initialNormalized = (working.scale - 0.5) / 2.5;
        double clampedNormalized = Math.max(0, Math.min(1, initialNormalized));

        // 2. Create the slider. We use an anonymous subclass to access protected methods/fields.
        this.scaleSlider = new SliderWidget(centerX - 100, this.height - 70, 200, 20, Text.literal("Scale: "), clampedNormalized) {
            @Override
            protected void updateMessage() {
                // Inside here, we are a SliderWidget, so we can access everything.
                setMessage(Text.literal("Scale: " + String.format("%.2f", working.scale)));
            }

            @Override
            protected void applyValue() {
                // 'this.value' is protected, but accessible here in the subclass.
                working.scale = (float) (0.5 + this.value * 2.5);
            }
        };

        this.saveButton = ButtonWidget.builder(Text.literal("Save"), btn -> {
            // Commit changes to the main config
            FpsHudClient.CONFIG.x = working.x;
            FpsHudClient.CONFIG.y = working.y;
            FpsHudClient.CONFIG.scale = working.scale;
            FpsHudClient.CONFIG.fixed144 = working.fixed144;

            FpsConfigManager.save(FpsHudClient.CONFIG);
            this.close();
        }).position(centerX - 100, this.height - 40).size(98, 20).build();

        this.cancelButton = ButtonWidget.builder(Text.literal("Cancel"), btn -> this.close())
                .position(centerX + 2, this.height - 40).size(98, 20).build();

        this.addDrawableChild(scaleSlider);
        this.addDrawableChild(saveButton);
        this.addDrawableChild(cancelButton);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);

        // Title + hint
        ctx.drawTextWithShadow(this.textRenderer, "Drag the FPS text to position it.", 10, 10, 0xFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, "Use the slider to change scale, then Save.", 10, 24, 0xAAAAAA);

        // Preview the HUD text at the working position
        HudRenderer.render(ctx, working);

        // Draw a subtle bounding box around the draggable region
        int w = HudRenderer.getTextWidth(working);
        int h = HudRenderer.getTextHeight(working);

        int x1 = working.x - 2;
        int y1 = working.y - 2;
        int x2 = working.x + w + 2;
        int y2 = working.y + h + 2;

        // Outline rectangle
        ctx.fill(x1, y1, x2, y1 + 1, 0x90FFFFFF);
        ctx.fill(x1, y2 - 1, x2, y2, 0x90FFFFFF);
        ctx.fill(x1, y1, x1 + 1, y2, 0x90FFFFFF);
        ctx.fill(x2 - 1, y1, x2, y2, 0x90FFFFFF);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int w = HudRenderer.getTextWidth(working);
            int h = HudRenderer.getTextHeight(working);

            boolean inside =
                    mouseX >= working.x && mouseX <= working.x + w &&
                    mouseY >= working.y && mouseY <= working.y + h;

            if (inside) {
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

            // Clamp to screen so it doesn't disappear
            int w = HudRenderer.getTextWidth(working);
            int h = HudRenderer.getTextHeight(working);

            working.x = clamp(working.x, 0, this.width - Math.max(1, w));
            working.y = clamp(working.y, 0, this.height - Math.max(1, h));

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private static int clamp(int v, int min, int max) {
        if (v < min) return min;
        return Math.min(v, max);
    }

    @Override
    public void close() {
        // Correct way to exit the screen in most Fabric versions
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}