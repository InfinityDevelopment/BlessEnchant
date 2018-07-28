package com.vk2gpz.tokenenchant.potions;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import com.vk2gpz.tokenenchant.api.EnchantInfo;
import com.vk2gpz.tokenenchant.api.PotionHandler;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;

public class BlessEnchant extends PotionHandler{
	/**
     * This is used to set the velosity of the firework
     */
    private int speed = 1;

    /**
     * This is the firework effect being used.
     */
    private FireworkEffect effect;


    /**
     * @param plugin TokenEnchant plugin, which wil be automatically provided by the TokenEnchant
     */
    public BlessEnchant(TokenEnchantAPI plugin) {
        super(plugin);
        loadConfig();
    }

    /**
     * If your custom enchant require configuration,
     * you can read those parameter in this method.
     */
    public void loadConfig() {
        // just in case super class (PotionHandler) has some config to read
        super.loadConfig();

        // read speed data.
        this.speed = this.plugin.getConfig().getInt("Potions." + getName() + ".speed", 1);

        // building the firework effect
        FireworkEffect.Builder builder = FireworkEffect.builder();
        Boolean flicker = this.plugin.getConfig().getBoolean("Potions." + getName() + ".flicker", true);
        if (flicker)
            builder = builder.withFlicker();

        Boolean trail = this.plugin.getConfig().getBoolean("Potions." + getName() + ".trail", true);
        if (trail)
            builder = builder.withTrail();

        String type = this.plugin.getConfig().getString("Potions." + getName() + ".effect", "STAR");
        builder = builder.with(FireworkEffect.Type.valueOf(type));

        Color color = getColor(this.plugin.getConfig().getInt("Potions." + getName() + ".color", 1));
        builder = builder.withColor(color);

        this.effect = builder.build();
    }

  /* ******** */
  /* Listener */
  /* ******** */

    /**
     * This method should return the name of your custom enchantment.
     */
    public String getName() {
        return "Bless";
    }

    /**
     * You can write your own event listener to implement your
     * custom enchant effect, if it should be trigered by one of events.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

    /* this "hasPotionEffect(Player, yourEnchant.class)" will return
       EnchantInfo object with the basic information:
       ei.getRealName() : the name of the enchant listed in the config.yml
       ei.getName() : same as above unless you provided "alias:"
       ei.getLevel() : the level of enchant the player is currently holding
       ei.getMax() : the max level of enchantment
    */

        EnchantInfo ei = hasPotionEffect(p, p.getItemInHand(), BlessEnchant.class);
        /*
          If you're certain that you only need to check an item in hand, you can use
          the following instead since this one is bit faster.

          EnchantInfo ei = hasPotionEffect(p, p.getItemInHand(), SampleEnchant.class);
        */

        /*
         canExecute will check the occurrence against the level of enchants.
         */
        if (ei == null || !canExecute(ei))
            return;

        String realname = ei.getRealName();
        final Block broken = e.getBlock();

      /*
        isValid(ei.getRealName(), Location) will check whether this enchant
        is allowed at the specifid location or not.
       */
        if (!isValid(realname, broken.getLocation()))
            return;

        int lvl = ei.getLevel();
        
        Random randloc = new Random();
        int n = randloc.nextInt(700) + 1;
        
        if(n == 500) {
        	TokenEnchantAPI teAPI = TokenEnchantAPI.getInstance();
        	if(teAPI != null) {
        		
        		int amount = (int) 100 / Bukkit.getOnlinePlayers().size();
        		
        		teAPI.addTokens(p, 500);
        		launchFirework(broken.getLocation(), lvl, this.speed, this.effect);
        		Bukkit.broadcastMessage("" + ChatColor.WHITE + ChatColor.BOLD + "[" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Mystic " + ChatColor.AQUA + ChatColor.BOLD + "Mines" + ChatColor.WHITE +
        		ChatColor.BOLD + "] " + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + p.getName() + ChatColor.GREEN + ChatColor.BOLD + " has recieved 500 " + ChatColor.AQUA +
        		ChatColor.BOLD + "Crystals " + ChatColor.GREEN + ChatColor.BOLD + "from " + ChatColor.AQUA + ChatColor.ITALIC + ChatColor.BOLD + "Bless " + ChatColor.RESET + ChatColor.GREEN +
        		ChatColor.BOLD + "and everyone online has recieved " + amount + ChatColor.AQUA + ChatColor.BOLD + " Crystals" + ChatColor.BOLD + ChatColor.GREEN + "!");
        		
        		for(Player pl : Bukkit.getOnlinePlayers()) {
        			teAPI.addTokens(pl, amount);
        		}
        		
        		return;
        	}else {
        		System.out.println("[TE-BlessEnchant] TokenEnchantAPI missing!");
        		return;
        	}
        }else {
        	return;
        }

    }

    private void launchFirework(Location l, int level, int speed, FireworkEffect effect) {
        l.add(0.5, 0.5, 0.5);
        Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(effect);
        meta.setPower(level);
        fw.setFireworkMeta(meta);

        //use meta to customize the firework or add parameters to the method
        fw.setVelocity(l.getDirection().multiply(speed));
        //speed is how fast the firework flies
    }

    private Color getColor(int c) {
        switch (c) {
            case 1:
            default:
                return Color.AQUA;
            case 2:
                return Color.BLACK;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.FUCHSIA;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.GREEN;
            case 7:
                return Color.LIME;
            case 8:
                return Color.MAROON;
            case 9:
                return Color.NAVY;
            case 10:
                return Color.OLIVE;
            case 11:
                return Color.ORANGE;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.RED;
            case 14:
                return Color.SILVER;
            case 15:
                return Color.TEAL;
            case 16:
                return Color.WHITE;
            case 17:
                return Color.YELLOW;
        }
    }

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return true;
	}
}
