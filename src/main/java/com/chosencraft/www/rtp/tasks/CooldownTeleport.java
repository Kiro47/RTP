package com.chosencraft.www.rtp.tasks;

import com.chosencraft.www.rtp.Commands.RandomTeleport;
import org.bukkit.entity.Player;

/**
 * Class Task to activate cooldowns
 */
public class CooldownTeleport implements Runnable
{
    private Player player;

    public CooldownTeleport(Player player)
    {
        this.player = player;
    }

    @Override
    public void run()
    {
        if (RandomTeleport.teleports.get(player) == 1)
        {
            RandomTeleport.teleports.remove(player);
        }
        else
        {
            RandomTeleport.teleports.put(player, RandomTeleport.teleports.get(player) - 1);
        }
        player.sendMessage(RandomTeleport.formatMessage("You can now teleport randomly another time!"));
    }
}
