package me.dave.bossbarregions;

import me.dave.bossbarregions.hook.WorldGuardHook;
import me.dave.chatcolorhandler.ChatColorHandler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;

public class BossBarHandler {
    private final HashMap<WorldGuardHook.RegionKey, BossBar> bossBars;

    public BossBarHandler() {
        bossBars = new HashMap<>();
    }

    public void refreshBossBars() {
        bossBars.forEach((regionKey, bossBar) -> {
            String newTitle = BossBarRegions.getWorldGuardHook().getBossBarAt(regionKey.world(), regionKey.regionId());
            bossBar.setTitle(ChatColorHandler.translate(newTitle));
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            WorldGuardHook.RegionKey regionKey = BossBarRegions.getWorldGuardHook().getRegionKey(player);

            bossBars.forEach(((key, bossBar) -> {
                if (regionKey != null && key.regionId().equals(regionKey.regionId())) {
                    bossBar.addPlayer(player);
                } else if (bossBar.getPlayers().contains(player)) {
                    bossBar.removePlayer(player);
                }
            }));

            if (regionKey != null) {
                String regionId = regionKey.regionId();

                BossBar bossBar;
                if (bossBars.containsKey(regionKey)) {
                    bossBar = bossBars.get(regionKey);
                } else {
                    String title = BossBarRegions.getWorldGuardHook().getBossBarAt(player.getWorld(), regionId);
                    bossBar = title != null ? bossBars.put(regionKey, Bukkit.createBossBar(title, BarColor.WHITE, BarStyle.SOLID)) : null;
                }

                if (bossBar != null) {
                    bossBar.addPlayer(player);
                }
            }
        });
    }
}
