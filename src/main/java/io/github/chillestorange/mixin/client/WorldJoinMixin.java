package io.github.chillestorange.mixin.client;

import io.github.chillestorange.config.WorldSyncConfig;
import io.github.chillestorange.logging.WorldSyncLogger;
import io.github.chillestorange.service.WorldSyncService;
import io.github.chillestorange.util.WorldDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList.WorldListEntry;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.UUID;

@Mixin(WorldListEntry.class)
public class WorldJoinMixin {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private LevelSummary summary;
    @Shadow
    @Final
    private WorldSelectionList list;

    @Inject(method = "joinWorld", at = @At("HEAD"), cancellable = true)
    private void worldsync$joinWorld(CallbackInfo ci) {

        String levelId = summary.getLevelId();
        if (!WorldSyncConfig.targetWorld().equals(levelId)) {
            return;
        }

        ci.cancel();

        Path worldPath = minecraft.getLevelSource().getLevelPath(levelId);
        WorldSyncLogger.info("Starting join-triggered sync for world: " + levelId);

        WorldSyncService.runSyncCycle(worldPath, () -> {
            UUID uuid = minecraft.getUser().getProfileId();
            Path levelDatPath = worldPath.resolve("level.dat");

            if (!WorldDataHelper.updateSingleplayerUuid(levelDatPath, uuid)) {
                WorldSyncLogger.error("Failed to update level.dat — opening world anyway: " + levelDatPath);
            }

            minecraft.execute(() ->
                    minecraft.createWorldOpenFlows().openWorld(levelId, list::returnToScreen));
        });
    }
}