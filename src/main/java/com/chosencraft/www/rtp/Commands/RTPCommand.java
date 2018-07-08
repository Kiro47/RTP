package com.chosencraft.www.rtp.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.chosencraft.www.rtp.RandomTeleportMain;
import com.chosencraft.www.rtp.utils.PermCache;
import com.chosencraft.www.rtp.utils.SettingsManager;
import com.chosencraft.www.rtp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class RTPCommand implements CommandExecutor {

    static Configuration config = RandomTeleportMain.getThisPlugin().getConfig();
    boolean wbhook = false; // config.getBoolean("WorldBorderHook");
    public static int negX = config.getInt("SetNegativeXLimit");
    public static int negZ = config.getInt("SetNegativeZLimit");
    public static int posX = config.getInt("SetPositiveXLimit");
    public static int posZ = config.getInt("SetPositiveZLimit");
    public static Boolean cost = config.getBoolean("Cost");
    public static double price = config.getDouble("price");
    SettingsManager d = SettingsManager.getData();
    private ArrayList<String> sp;
    private HashMap<UUID,Integer> rtpTr = new HashMap<UUID,Integer>();
    private int lx;
    private int ly;
    private int lz;
    public static int x ;
    public static int z ;
    public static int y ;

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sp = new ArrayList<String>();
        sp.addAll(SettingsManager.getData().getKeys());

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only usable ingame!");
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0){
            if (!(p.hasPermission(PermCache.PERM_RTP_COMMAND))) {
                p.sendMessage(ChatColor.DARK_RED + "You don't have permission to peform this command!");
                return true;
            }

            if (config.getBoolean("HardSetTeleports") == true) {
                if (sp.isEmpty()) {
                    p.sendMessage(ChatColor.RED + "No teleports set! Please contact an administrator!");
                    return true;
                }
                else {
                    int num = Utils.randInt(0, (sp.size() -1));
                    String st = sp.get(num);
                    lx = (int) d.get(st +".x");
                    ly = (int) d.get(st+".y");
                    lz = (int) d.get(st+".z");
                    p.teleport(new Location(Bukkit.getServer().getWorld((String)d.get(st+".world")),lx, ly,lz));
                    Location nl = p.getLocation();
                    if (cost == false) {


                        if (config.getBoolean("ShowName") == true) {
                            p.sendMessage(ChatColor.GREEN + "You have been teleported to "+ sp.get(num)+ ", at " + nl.getBlockX() +", " +nl.getBlockY()+", "+nl.getBlockZ()+".");
                            return true;
                        }
                        else {
                            p.sendMessage(ChatColor.GREEN + "You have been teleported to " + nl.getBlockX() +", " +nl.getBlockY()+", "+nl.getBlockZ()+".");
                            return true;
                        }
                    }
                    else {
                        // make cost


                    }
                }
            }
            if (config.getBoolean("HardSetTeleports") == false) {
                if (wbhook == true) {
                    // do something
                }
                else {
                    Location tloc = null;
                    Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(RandomTeleportMain.getThisPlugin(), new Runnable(){
                        public void run() {
                            x = Utils.randInt(negX, posX);
                            z = Utils.randInt(negZ, posZ);
                            y = 256;

                        }
                    });

                    boolean isOnLand = false;

                    if ((rtpTr.get(p.getUniqueId()) != null)        &&    ((rtpTr.get(p.getUniqueId()) +1) > config.getInt("Cooldown.amount"))  ){
                        p.sendMessage(ChatColor.DARK_RED + "You have exceeded the maximum number of random teleports! Please wait for the cooldown.");
                        return true;
                    }
                    while (isOnLand == false) {
                        if (config.getBoolean("Worlds.DefaultOnly") == true) {
                            tloc = new Location(Bukkit.getWorlds().get(0), x, y, z);
                        }
                        else {
                            if (!(config.getList("Worlds.WhiteListedWorlds").contains(p.getWorld().getName()))) {
                                tloc = new Location(Bukkit.getWorlds().get(0), x, y, z);
                            }
                            else {
                                tloc = new Location(p.getWorld(), x, y, z);
                            }

                        }

                        if (tloc.getBlock().getType() != Material.AIR) {
                            isOnLand = true;
                        }
                        else {
                            if ( y < 16) {
                                x = Utils.randInt(negX, posX);
                                z = Utils.randInt(negZ, posZ);
                            }
                            else {
                                y--;
                            }
                        }
                    }

                    if (x == 0 && z == 0){
                        tloc = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
                    }
                    p.teleport(new Location(tloc.getWorld(), tloc.getX() + 0.5, tloc.getY() +1, tloc.getZ() + 0.5));
                    p.sendMessage(ChatColor.GREEN + "You have been teleported to " +tloc.getBlockX() +", " + (tloc.getBlockY() +1) +", " + tloc.getBlockZ());


                    if (!(p.hasPermission(PermCache.PERM_RTP_COOLDOWN_BYPASS))) {
                        if (rtpTr.containsKey(p.getUniqueId())) {
                            rtpTr.put(p.getUniqueId(), rtpTr.get(p.getUniqueId()) +1);
                        }
                        else {
                            rtpTr.put(p.getUniqueId(), 1);
                        }
                        RandomTeleportMain.getThisPlugin().getServer().getScheduler().scheduleSyncDelayedTask(RandomTeleportMain.getThisPlugin(), new Runnable(){
                            public void run() {
                                rtpTr.put(p.getUniqueId(), (rtpTr.get(p.getUniqueId()) - 1));
                                p.sendMessage(ChatColor.GREEN +"You now have another random teleport to use!");
                            }
                        },(long) (config.getInt("Cooldown.time") *20));
                        return true;
                    }
                    else {

                        return true;
                    }
                }


                return true;
            }
            return true;
        }
        else if (args.length == 1) {
            if (!(args[0].equalsIgnoreCase("list"))) {
                if (!(p.hasPermission(PermCache.PERM_RTP_COMMAND_LIST_))) {p.sendMessage(ChatColor.RED + "You don't have permission for this!"); return true;}
                p.sendMessage(ChatColor.RED +"/rtp [add,remove,list] <name>");
                return true;
            }
            else {
                p.sendMessage(ChatColor.LIGHT_PURPLE + sp.toString());
                return true;
            }
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (!(p.hasPermission(PermCache.PERM_RTP_COMMAND_ADD_TP))) {p.sendMessage(ChatColor.RED + "You don't have permission for this!"); return true;}
                if (sp.contains(args[1])) {
                    p.sendMessage(ChatColor.RED + "Warp with name " + args[1] + " already exists.");
                    return true;
                }
                else {
                    Location loc = p.getLocation();
                    d.createSection(args[1]);
                    d.set(args[1]+".world", loc.getWorld().getName());
                    d.set(args[1] +".x", loc.getBlockX());
                    d.set(args[1] +".y", loc.getBlockY());
                    d.set(args[1] +".z", loc.getBlockZ());
                    p.sendMessage(ChatColor.GREEN + "Location " +args[1] +" saved succesfully!");
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("remove")){
                if (!(p.hasPermission(PermCache.PERM_RTP_COMMAND_REM_TP))) {p.sendMessage(ChatColor.RED + "You don't have permission for this!"); return true;}
                if (!(sp.contains(args[1]))) {
                    p.sendMessage(ChatColor.RED +"Warp doesn't exist!");
                    return true;
                }
                else {
                    d.set(args[1]+".world", null);
                    d.set(args[1] +".x", null);
                    d.set(args[1] +".y", null);
                    d.set(args[1] +".z", null);
                    d.set(args[1], null);
                    d.save();
                    p.sendMessage(ChatColor.GREEN +"Location " + args[1]+ " removed succefully.");
                    return true;
                }
            }
            else {
                p.sendMessage(ChatColor.RED +"/rtp [add,remove,list] <name>"); p.sendMessage("180");
                return true;
            }
        }
        else {
            p.sendMessage(ChatColor.RED +"/rtp [add,remove,list] <name>"); p.sendMessage("185");
            return true;
        }
    }
}