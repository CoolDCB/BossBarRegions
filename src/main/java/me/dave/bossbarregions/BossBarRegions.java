package me.dave.bossbarregions;

import me.dave.bossbarregions.hook.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class BossBarRegions extends JavaPlugin {
    private static BossBarHandler bossBarHandler;
    private static WorldGuardHook worldGuardHook;
    private static BukkitTask task;

    @Override
    public void onLoad() {
        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.getPlugin("WorldGuard") != null) {
            getLogger().info("Found plugin \"WorldGuard\". Enabling WorldGuard support");
            worldGuardHook = new WorldGuardHook();
        }
    }

    @Override
    public void onEnable() {
        bossBarHandler = new BossBarHandler();

        task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            bossBarHandler.refreshBossBars();
        }, 200, 100);
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public static WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }
}
