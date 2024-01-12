package me.ultimate.waxlog;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;


public final class WaxLog extends JavaPlugin implements Listener {
    private final NamespacedKey KEY = new NamespacedKey(this, "waxlog");
    private static final HashSet<Material> LOGS = new HashSet<Material>() {{
        add(Material.ACACIA_LOG);
        add(Material.BIRCH_LOG);
        add(Material.DARK_OAK_LOG);
        add(Material.JUNGLE_LOG);
        add(Material.OAK_LOG);
        add(Material.SPRUCE_LOG);
        add(Material.MANGROVE_LOG);
        add(Material.CHERRY_LOG);
    }};
    private static final HashSet<Material> AXES = new HashSet<Material>() {{
        add(Material.WOODEN_AXE);
        add(Material.STONE_AXE);
        add(Material.IRON_AXE);
        add(Material.GOLDEN_AXE);
        add(Material.DIAMOND_AXE);
        add(Material.NETHERITE_AXE);
    }};

    @Override
    public void onEnable() {
        CustomBlockData.registerListener(this);

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean isWaxed(Block block) {
        return Boolean.TRUE.equals(new CustomBlockData(block, this).get(KEY, PersistentDataType.BOOLEAN));
    }

    public void setWaxed(Block block, boolean waxed) {
        new CustomBlockData(block, this).set(KEY, PersistentDataType.BOOLEAN, waxed);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) return; // Right-clicking a block
        if (e.getItem() == null || !LOGS.contains(e.getClickedBlock().getType())) return; // Right-clicking a log with an item in hand

        // Check for waxing a log
        if (e.getItem().getType() == Material.HONEYCOMB) {
            if (isWaxed(e.getClickedBlock())) return; // Already waxed

            // Remove item from player's inventory
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.getItem().setAmount(e.getItem().getAmount() - 1);

            setWaxed(e.getClickedBlock(), true);
            e.getClickedBlock().getWorld().playEffect(e.getClickedBlock().getLocation(), Effect.COPPER_WAX_ON, null);
        } else if (AXES.contains(e.getItem().getType()) && isWaxed(e.getClickedBlock())) { // Check for stripping a log
            e.setCancelled(true);

            // Play effect to let them know you can't strip this log (Since client side it looks like you can for a sec)
            e.getPlayer().playEffect(e.getClickedBlock().getLocation(), Effect.OXIDISED_COPPER_SCRAPE, null);
        }
    }
}
