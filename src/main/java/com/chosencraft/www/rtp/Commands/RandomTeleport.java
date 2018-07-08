package com.chosencraft.www.rtp.Commands;

import com.chosencraft.www.rtp.RandomTeleportMain;
import com.chosencraft.www.rtp.utils.Configurations;
import com.chosencraft.www.rtp.utils.PermCache;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RandomTeleport implements CommandExecutor
{

    private ArrayList<Player> asked = new ArrayList<>();
    private HashMap<Player, Integer> teleports = new HashMap<>();

    private String format = ChatColor.GOLD + "[" + ChatColor.AQUA + "RTP" + ChatColor.GOLD + "]" + ChatColor.GREEN + " %s";

    private Plugin plugin;

    /**
     * Constructor
     * @param plugin Current plugin
     */
    public RandomTeleport(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(formatMessage("Unable to use from terminal!"));
            return true;
        }

        Player player = (Player) commandSender;

        if (player.hasPermission(PermCache.PERM_RTP_COMMAND))
        {
            // halt if used more than 3 times in past 5 minutes
            if (!player.hasPermission(PermCache.PERM_RTP_COOLDOWN_BYPASS))
            {
                if (teleports.containsKey(player) && teleports.get(player) == Configurations.amountOfTeleports)
                {
                    player.sendMessage(formatMessage("Please wait to use random teleport again!"));
                    return true;
                }
            }

            // If they have not been before, return
            if (!hasBeenAsked(player))
            {
                return true;
            }
            // attempts to charge player
            if (!chargePlayer(player))
            {
                return true;
            }

            Location randomLocation = generateLocation();
            // Keep going until spot is safe
            while (!isSafe(randomLocation))
            {
                randomLocation = generateLocation();
            }

            // increment
            if (!teleports.containsKey(player))
            {
                teleports.put(player, 1);
            }
            else
            {
                teleports.put(player, teleports.get(player) + 1);
            }

            startCooldown(player);
            player.sendMessage(formatMessage("Teleporting to: " + randomLocation.getBlockX() + " " + randomLocation.getBlockY() + " " + randomLocation.getBlockZ() + " !"));
            player.teleport(randomLocation);
            asked.remove(player);
        }
        else
        {
            commandSender.sendMessage("You don't have permission to use this!");
            return true;
        }

        return true;
    }

    /**
     * Formats the string to be sent back in a prettyish manner.
     * @param message  Message to be formatted.
     * @return The formatted message
     */
    private String formatMessage(String message)
    {
        if (message == null)
        {
            return null;
        }
        return String.format(format, message);

    }


    /**
     * Generate the random location
     * @return A newly generated random location
     */
    private Location generateLocation()
    {
        Random random = new Random();

        int x = random.nextInt(Math.abs(Configurations.maximumXRadius));
        int z = random.nextInt(Math.abs(Configurations.maximumZRadius));

        boolean stateX = random.nextBoolean();
        boolean stateZ = random.nextBoolean();

        // if false convert to negative
        if (!stateX)
        {
            x = Math.negateExact(x);
        }
        if (!stateZ)
        {
            z = Math.negateExact(z);
        }
        // bump up once to put player feet above it
        return Bukkit.getWorlds().get(0)
        .getHighestBlockAt(x,z).getLocation()
                .add(0, 1, 0);
    }

    /**
     * Checks to see if a location is safe to teleport to
     * @param location Location to check
     * @return true if safe, false otherwise
     */
    private boolean isSafe(Location location)
    {
        //TODO: implement without lagging everyhting down
        return true;
    }

    /**
     * Runs task and adds it to list
     * @param player Player to lower cooldown of.
     */
    private void startCooldown(Player player)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new cooldownTeleport(this , player), Configurations.cooldownTimeInSeconds * 20);
    }

    /**
     * Class Task to activate cooldowns
     */
    private class cooldownTeleport implements Runnable
    {
        private Player player;
        private RandomTeleport clazz;

        private cooldownTeleport(RandomTeleport clazz, Player player)
        {
            this.player = player;
            this.clazz = clazz;
        }

        @Override
        public void run()
        {
            if (clazz.teleports.get(player) == 1)
            {
                clazz.teleports.remove(player);
            }
            else
            {
                clazz.teleports.put(player, clazz.teleports.get(player));
            }
            player.sendMessage(clazz.formatMessage("You can now teleport randomly another time!"));
        }
    }


    /**
     * Checks to see if a player has been asked to teleport yet
     * @param player Player to check
     * @return Returns true if the player has already been asked, or if this is disabled in the config, false otherwise.
     */
    private boolean hasBeenAsked(Player player)
    {
        if (Configurations.askBeforeTeleporting)
        {
            if (asked.contains(player))
            {
                return true;
            }
            else
            {
                String charge = "";
                if (Configurations.chargeForTeleport)
                {
                    charge = String.format("You will be charged %s to teleport!", new DecimalFormat("#.00").format(Configurations.costPerTeleport));
                }
                player.sendMessage(formatMessage("Are you sure you want to teleport? Type /rtp again to teleport. " + charge));
                asked.add(player);
                return false;
            }
        }
        else
        {
            // disabled in config
            return true;
        }
    }

    /**
     * Attempts to charge player the specified amount
     * @param player Player to charge
     * @return Returns true if the player successfully pays or the option is disabled, false otherwise.
     */
    private boolean chargePlayer(Player player)
    {
        Economy economy = RandomTeleportMain.economy;
        if (Configurations.chargeForTeleport)
        {
         EconomyResponse response = economy.withdrawPlayer(player, Configurations.costPerTeleport);
         if (response.transactionSuccess())
         {
             return true;
         }
         else
         {
             player.sendMessage(formatMessage("Unable to confirm transaction!"));
             String playerBalance = new DecimalFormat("#.00").format(economy.getBalance(player));
             String failMessage = String.format("Your current balance is %s , the amount needed to teleport is %f.", playerBalance, Configurations.costPerTeleport);
             player.sendMessage(formatMessage(failMessage));
             return false;
         }

        }
        else
        {
            // disabled
            return true;
        }
    }
}