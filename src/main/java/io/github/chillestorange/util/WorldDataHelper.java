package io.github.chillestorange.util;

import io.github.chillestorange.logging.WorldSyncLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.core.UUIDUtil;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Minecraft-specific world-data utilities. Kept separate from WorldSyncService
 * so the sync pipeline itself (LevelSync, SyncDiffEngine, etc.) stays free of
 * Minecraft class dependencies and could theoretically be tested without a
 * Minecraft environment on the classpath.
 */
public final class WorldDataHelper {

    private WorldDataHelper() {}

    /**
     * Rewrites the singleplayer_uuid field in level.dat to match the current
     * user's profile UUID. Called after a download sync completes so Minecraft
     * doesn't treat the world as belonging to a different player, which can
     * cause inventory and permission issues in singleplayer.
     *
     * @return true if the update succeeded, false if it failed (the caller
     *         should decide whether to abort opening the world or proceed anyway)
     */
    public static boolean updateSingleplayerUuid(Path levelDatPath, UUID uuid) {
        try {
            CompoundTag root = NbtIo.readCompressed(levelDatPath, NbtAccounter.unlimitedHeap());
            CompoundTag data = root.getCompound("Data")
                    .orElseThrow(() -> new IllegalStateException("Missing Data compound in level.dat"));

            data.storeNullable("singleplayer_uuid", UUIDUtil.CODEC, uuid);
            NbtIo.writeCompressed(root, levelDatPath);

            WorldSyncLogger.info("Updated singleplayer_uuid in level.dat: " + levelDatPath);
            return true;
        } catch (Exception e) {
            WorldSyncLogger.error("Failed to update singleplayer_uuid in level.dat: " + levelDatPath, e);
            return false;
        }
    }
}