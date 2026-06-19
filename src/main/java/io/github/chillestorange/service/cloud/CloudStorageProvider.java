package io.github.chillestorange.service.cloud;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Provider-agnostic contract for a cloud storage backend. GoogleDriveProvider
 * (in the gdrive subpackage) is the only implementation today, but every
 * method here is deliberately phrased in terms general enough to fit Dropbox,
 * OneDrive, etc. — folders containing named items, each with an id and a
 * modified time. SyncDiffEngine, FileTransferManager, and WorldSyncService all
 * depend on this interface, never on a concrete provider.
 */
public interface CloudStorageProvider {

    /**
     * One BFS pass over the whole tree under rootFolderId. Implemented here as
     * a default method built from listChildren, rather than as an abstract
     * method every provider has to satisfy — the traversal itself (queue,
     * visited set, recurse into anything isFolder()) has nothing
     * provider-specific in it, so a future Dropbox/OneDrive implementation
     * only needs listChildren and gets tree-fetching for free instead of
     * copy-pasting the same BFS GoogleDriveProvider used to carry.
     */
    default Map<String, List<CloudItem>> fetchTree(String rootFolderId) throws IOException, InterruptedException {
        Map<String, List<CloudItem>> tree = new HashMap<>();
        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(rootFolderId);

        while (!queue.isEmpty()) {
            String folderId = queue.poll();
            if (!visited.add(folderId)) continue;

            List<CloudItem> children = listChildren(folderId);
            tree.put(folderId, children);

            for (CloudItem item : children) {
                if (item.isFolder()) {
                    queue.add(item.id());
                }
            }
        }
        return tree;
    }

    List<CloudItem> listChildren(String folderId) throws IOException, InterruptedException;

    Optional<CloudItem> findByNameInFolder(String filename, String folderId) throws IOException, InterruptedException;

    void downloadFile(String fileId, Path destination) throws IOException, InterruptedException;

    /**
     * Create a new file, or overwrite an existing one's content.
     *
     * @param existingFileId the remote id if this file is already known to
     *                       exist (SyncDiffEngine already resolved this while
     *                       building the changeset), or null for a brand-new
     *                       upload. Passing the known id avoids a redundant
     *                       existence-check lookup the caller already did the
     *                       work for.
     */
    String uploadOrReplace(Path localFile, String existingFileId, String folderId, String filename, Instant modifiedTime)
            throws IOException, InterruptedException;

    String createFolder(String parentFolderId, String name, Instant modifiedTime) throws IOException, InterruptedException;

    /**
     * Computes a local fingerprint comparable to a remote CloudItem's
     * contentFingerprint, using whatever algorithm this provider's remote side
     * uses. Implementations with no cheap way to do this may return null or
     * an empty string, which tells the diff engine to skip the "don't
     * re-upload if content didn't actually change" optimization and just
     * trust the modified-time comparison instead.
     */
    String computeLocalFingerprint(Path localFile) throws IOException;
}