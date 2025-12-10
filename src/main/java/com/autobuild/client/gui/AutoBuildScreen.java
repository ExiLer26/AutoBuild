package com.autobuild.client.gui;

import com.autobuild.AutoBuild;
import com.autobuild.common.config.AutoBuildConfig;
import com.autobuild.common.data.StructureManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class AutoBuildScreen extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 5;
    private static final int JSON_PER_PAGE = 5;

    private int currentTab = 0;
    private int jsonPage = 0;

    public AutoBuildScreen() {
        super(Component.translatable("screen.autobuild.title"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = 50;

        // TAB BUTTONS
        this.addRenderableWidget(Button.builder(Component.translatable("screen.autobuild.tab.home"), button -> {
            currentTab = 0;
            rebuildWidgets();
        }).bounds(centerX - 160, startY, 100, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("screen.autobuild.tab.json"), button -> {
            currentTab = 1;
            rebuildWidgets();
        }).bounds(centerX - 50, startY, 100, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.translatable("screen.autobuild.tab.settings"), button -> {
            currentTab = 2;
            rebuildWidgets();
        }).bounds(centerX + 60, startY, 100, BUTTON_HEIGHT).build());

        switch (currentTab) {
            case 0 -> initHomeTab(centerX, startY + 40);
            case 1 -> initJsonTab(centerX, startY + 40);
            case 2 -> initSettingsTab(centerX, startY + 40);
        }
    }

    // ---------------- HOME TAB ----------------
    private void initHomeTab(int centerX, int startY) {
        boolean enabled = AutoBuildConfig.isEnabled();
        Component toggleText = enabled ?
                Component.translatable("screen.autobuild.toggle.on") :
                Component.translatable("screen.autobuild.toggle.off");

        this.addRenderableWidget(Button.builder(toggleText, button -> {
            AutoBuildConfig.setEnabled(!AutoBuildConfig.isEnabled());
            rebuildWidgets();
        }).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.create(Component.translatable("screen.autobuild.toggle.tooltip")))
                .build());

        int buildSpeed = AutoBuildConfig.getBuildSpeed();
        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.speed", buildSpeed),
                button -> {
                    int newSpeed = buildSpeed >= 10 ? 1 : buildSpeed + 1;
                    AutoBuildConfig.setBuildSpeed(newSpeed);
                    rebuildWidgets();
                }).bounds(centerX - BUTTON_WIDTH / 2, startY + 30, BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.create(Component.translatable("screen.autobuild.speed.tooltip")))
                .build());

        int proximity = AutoBuildConfig.getProximityDistance();
        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.proximity", proximity),
                button -> {
                    int newProximity = proximity >= 10 ? 1 : proximity + 1;
                    AutoBuildConfig.setProximityDistance(newProximity);
                    rebuildWidgets();
                }).bounds(centerX - BUTTON_WIDTH / 2, startY + 60, BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.create(Component.translatable("screen.autobuild.proximity.tooltip")))
                .build());

        String selected = AutoBuildConfig.getSelectedStructure();
        Component selectedText = selected.isEmpty() ?
                Component.translatable("screen.autobuild.noselection") :
                Component.literal(selected);
        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.selected", selectedText),
                button -> {
                    currentTab = 1;
                    rebuildWidgets();
                }).bounds(centerX - BUTTON_WIDTH / 2, startY + 90, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    // ---------------- JSON TAB WITH PAGINATION ----------------
    private void initJsonTab(int centerX, int startY) {
        List<String> structures = StructureManager.getStructureNames();

        // Refresh button
        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.refresh"),
                button -> {
                    StructureManager.loadStructures();
                    jsonPage = 0;
                    rebuildWidgets();
                }).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Pagination buttons < >
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (jsonPage > 0) {
                jsonPage--;
                rebuildWidgets();
            }
        }).bounds(centerX - 100, startY + 30, 20, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if ((jsonPage + 1) * JSON_PER_PAGE < structures.size()) {
                jsonPage++;
                rebuildWidgets();
            }
        }).bounds(centerX + 80, startY + 30, 20, BUTTON_HEIGHT).build());

        // Page indicator
        int totalPages = Math.max(1, (int) Math.ceil(structures.size() / (double) JSON_PER_PAGE));
        this.addRenderableWidget(Button.builder(Component.literal("Page " + (jsonPage + 1) + "/" + totalPages), button -> {})
                .bounds(centerX - 40, startY + 30, 80, BUTTON_HEIGHT).build());

        // JSON LIST ELEMENTS
        int yOffset = 60;
        int startIndex = jsonPage * JSON_PER_PAGE;
        int endIndex = Math.min(structures.size(), startIndex + JSON_PER_PAGE);

        if (structures.isEmpty()) {
            this.addRenderableWidget(Button.builder(
                    Component.translatable("screen.autobuild.nostructures"),
                    button -> {}).bounds(centerX - BUTTON_WIDTH / 2, startY + 30, BUTTON_WIDTH, BUTTON_HEIGHT).build());
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            String name = structures.get(i);
            boolean isSelected = name.equals(AutoBuildConfig.getSelectedStructure());
            Component buttonText = isSelected ? Component.literal(">> " + name + " <<") : Component.literal(name);

            this.addRenderableWidget(Button.builder(buttonText, button -> {
                AutoBuildConfig.setSelectedStructure(name);
                rebuildWidgets();
            }).bounds(centerX - BUTTON_WIDTH / 2, startY + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT).build());

            yOffset += 25;
        }
    }

    // ---------------- SETTINGS TAB ----------------
    private void initSettingsTab(int centerX, int startY) {
        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.speed.decrease"),
                button -> {
                    int current = AutoBuildConfig.getBuildSpeed();
                    if (current > 1) {
                        AutoBuildConfig.setBuildSpeed(current - 1);
                        rebuildWidgets();
                    }
                }).bounds(centerX - 80, startY, 30, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(
                Component.literal(String.valueOf(AutoBuildConfig.getBuildSpeed())), button -> {})
                .bounds(centerX - 45, startY, 90, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.speed.increase"),
                button -> {
                    int current = AutoBuildConfig.getBuildSpeed();
                    if (current < 100) {
                        AutoBuildConfig.setBuildSpeed(current + 1);
                        rebuildWidgets();
                    }
                }).bounds(centerX + 50, startY, 30, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.proximity.decrease"),
                button -> {
                    int current = AutoBuildConfig.getProximityDistance();
                    if (current > 1) {
                        AutoBuildConfig.setProximityDistance(current - 1);
                        rebuildWidgets();
                    }
                }).bounds(centerX - 80, startY + 30, 30, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(
                Component.literal(String.valueOf(AutoBuildConfig.getProximityDistance())), button -> {})
                .bounds(centerX - 45, startY + 30, 90, BUTTON_HEIGHT).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.autobuild.proximity.increase"),
                button -> {
                    int current = AutoBuildConfig.getProximityDistance();
                    if (current < 10) {
                        AutoBuildConfig.setProximityDistance(current + 1);
                        rebuildWidgets();
                    }
                }).bounds(centerX + 50, startY + 30, 30, BUTTON_HEIGHT).build());
    }

    // ---------------- RENDER ----------------
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        String tabTitle;
        switch (currentTab) {
            case 0:
                tabTitle = Component.translatable("screen.autobuild.tab.home").getString();
                break;
            case 1:
                tabTitle = Component.translatable("screen.autobuild.tab.json").getString();
                break;
            case 2:
                tabTitle = Component.translatable("screen.autobuild.tab.settings").getString();
                break;
            default:
                tabTitle = "";
                break;
        }

        // tamamlanmış çağrı: tabTitle, x, y, renk
        guiGraphics.drawCenteredString(this.font, tabTitle, this.width / 2, 35, 0xAAAAAA);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
