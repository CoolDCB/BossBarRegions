package me.dave.bossbarregions.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class WorldGuardHook {
    private static StringFlag BOSS_BAR_FLAG;

    public WorldGuardHook() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StringFlag flag = new StringFlag("boss-bar");
            registry.register(flag);
            BOSS_BAR_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("boss-bar");
            if (existing instanceof StringFlag) {
                BOSS_BAR_FLAG = (StringFlag) existing;
            }
        }
    }

    @Nullable
    public RegionKey getRegionKey(@NotNull Player player) {
        World world = player.getWorld();
        String regionId = getRegionId(world, player.getLocation());

        return regionId != null ? new RegionKey(world, regionId) : null;
    }

    @Nullable
    public String getRegionId(@NotNull World world, @NotNull Location location) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return null;
        }

        ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
        List<ProtectedRegion> regions = set.getRegions().stream().sorted(Comparator.comparing(ProtectedRegion::getPriority)).toList();
        if (regions.size() == 0) {
            return null;
        }

        ProtectedRegion region = regions.get(0);
        return region.getId();
    }

    @Nullable
    public String getBossBarAt(@NotNull World world, @NotNull String regionId) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return null;
        }

        ProtectedRegion region = regionManager.getRegion(regionId);
        return region != null ? region.getFlag(BOSS_BAR_FLAG) : null;
    }

    public record RegionKey(World world, String regionId) {}
}
