package com.c010ur1355.simpleterritory;

import net.minecraft.server.v1_12_R1.CommandExecute;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.lang.reflect.*;

import java.util.UUID;

public class Commands extends CommandExecute implements CommandExecutor{
    private main plugin = main.getPlugin(main.class);
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        //if sender is player
        if(sender instanceof Player){
            Player player = (Player)sender;

            //check args
            if(args.length >= 1){
                //get location
                Chunk chunk = player.getLocation().getChunk();
                int x = chunk.getX();
                int z = chunk.getZ();
                Territory territory = new Territory(x, z);

                //configuration
                FileConfiguration config = plugin.getConfig();

                double initial_value = config.getDouble("setting.initial_value");
                double common_ratio = config.getDouble("setting.common_ratio");

                //team commands
                if(args[0].equalsIgnoreCase("team")){
                    //initialize
                    Team team = new Team(player.getUniqueId().toString());
                    if(args.length >= 2){
                        if(args[1].equalsIgnoreCase("current")){
                            if(team.getTeam() == Team.RESULT.RECORD_EXISTED){
                                player.sendMessage(ChatColor.GREEN + "Team Name: " + team.name + "\nTeam owner: " + plugin.getServer().getOfflinePlayer(UUID.fromString(team.owner)).getName() + (team.description == null ? "" : "\ndescription: " + team.description + "\n"));

                                if(team.member != null){
                                    if(team.member.length > 0){
                                        String str = "member: ";
                                        for(int i = 0; i < team.member.length; i++){
                                            str += plugin.getServer().getOfflinePlayer((UUID.fromString(team.member[i]))).getName() + ", ";
                                        }
                                        player.sendMessage(ChatColor.GREEN + str);
                                    }
                                }


                            }else{
                                player.sendMessage(ChatColor.RED + "Team not found.");
                            }
                            return true;
                        }else if(args[1].equalsIgnoreCase("create")){
                            if(args.length >= 3){
                                String str = "";

                                for(int i = 2; i < args.length; i++){
                                    str += args[i] + " ";
                                }

                                Team.RESULT result = team.createTeam(str);

                                switch (result){
                                    case SUCCESS:
                                        player.sendMessage(ChatColor.GREEN + "Successfully created a team.");
                                        break;
                                    case CONNECTION_FAILURE:
                                        player.sendMessage(ChatColor.RED + "Unable to create a team due to Connection failure.");
                                        break;
                                    case RECORD_EXISTED:
                                        player.sendMessage(ChatColor.RED + "You have created a team already.");
                                        break;
                                }

                            }else{
                                player.sendMessage(ChatColor.RED + "Too few parameters has been entered.");
                            }
                            return true;
                        }else if(args[1].equalsIgnoreCase("cancel")){

                            Team.RESULT result = team.removeTeam();

                            switch (result){
                                case SUCCESS:
                                    player.sendMessage(ChatColor.GREEN + "Successfully dismiss current team.");
                                    break;
                                case CONNECTION_FAILURE:
                                    player.sendMessage(ChatColor.RED + "Unable to dismiss current team due to connection failure.");
                                    break;
                                case NO_RECORD_EXISTED:
                                    player.sendMessage(ChatColor.RED + "You have no team that could be dismissed.");
                                    break;
                            }

                            return true;
                        }else if(args[1].equalsIgnoreCase("add")){

                            if(args.length == 3){
                                Team.RESULT result = team.addMember(UUID.fromString(args[3]).toString());

                                switch (result){
                                    case SUCCESS:
                                        player.sendMessage(ChatColor.GREEN + "Successfully add player \"" + args[2] + "\" to current team.");
                                        break;
                                    case FAILURE:
                                        player.sendMessage(ChatColor.RED + "Unable to add player to current team.");
                                        break;
                                    case DUPLICATED:
                                        player.sendMessage(ChatColor.RED + "This player already belongs to current team.");
                                        break;
                                    case CONNECTION_FAILURE:
                                        player.sendMessage(ChatColor.RED + "Unable to add player to current team due to connection failure.");
                                        break;
                                }

                            }else{
                                player.sendMessage(ChatColor.RED + "Usage: /trt team add [player name]");
                            }

                            return true;
                        }else if(args[1].equalsIgnoreCase("remove")){

                            if(args.length == 3){
                                Team.RESULT result = team.removeMember(UUID.fromString(args[3]).toString());

                                switch (result){
                                    case SUCCESS:
                                        player.sendMessage(ChatColor.GREEN + "Successfully remove player \"" + args[2] + "\" from current team.");
                                        break;
                                    case FAILURE:
                                        player.sendMessage(ChatColor.RED + "Unable to remove player from current team.");
                                        break;
                                    case NOT_FOUND:
                                        player.sendMessage(ChatColor.RED + "Current team does not contains this player.");
                                        break;
                                    case CONNECTION_FAILURE:
                                        player.sendMessage(ChatColor.RED + "Unable to remove player from current team due to connection failure.");
                                        break;
                                }

                            }else{
                                player.sendMessage(ChatColor.RED + "Usage: /trt team add [player name]");
                            }
                            return true;
                        }else if(args[1].equalsIgnoreCase("description")){

                            if(args.length >= 3){
                                String str = "";

                                for(int i = 2; i < args.length; i++){
                                    str += args[i] + " ";
                                }

                                Team.RESULT result = team.setDescription(str);

                                switch (result){
                                    case SUCCESS:
                                        player.sendMessage(ChatColor.GREEN + "Successfully modified team's description.");
                                        break;
                                    case NO_RECORD_EXISTED:
                                        player.sendMessage(ChatColor.RED + "You have no team that could be modified.");
                                        break;
                                    case CONNECTION_FAILURE:
                                        player.sendMessage(ChatColor.RED + "Unable to modify current team's description due to connection failure.");
                                        break;
                                }

                            }else{
                                player.sendMessage(ChatColor.RED + "Too few parameters has been entered.");
                            }
                            return true;
                        }else{
                            player.sendMessage(ChatColor.RED + "/trt team usage:\n/trt team current - show current team.\n/trt team create - create a new team.\n/trt team cancel - cancel current team.\n/trt team add - add a player to current team.\n/trt team remove - remove a player from current team.\n/trt team description - set team description.");
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "/trt team usage:\n/trt team current - show current team.\n/trt team create - create a new team.\n/trt team cancel - cancel current team.\n/trt team add - add a player to current team.\n/trt team remove - remove a player from current team.\n/trt team description - set team description.");
                        return true;
                    }
                }else if(args[0].equalsIgnoreCase("current")){
                    //check current territory properties
                    if(territory.getRecord()){
                        //get detail information
                        player.sendMessage(ChatColor.GREEN + "Territory is found.\nLocation: (" + x + ", " + z + ")\nOwner: " + plugin.getServer().getOfflinePlayer(UUID.fromString(territory.owner)).getName());
                    }else{  //not found.
                        player.sendMessage(ChatColor.RED + "No territory has been found.");
                    }
                    return true;
                    //buy territory
                }else if(args[0].equalsIgnoreCase("buy")){
                    //get player territories count
                    int count = Territory.getPlayerTerritoryCount(player);

                    //if player pass the conditions of buying territory
                    if(Territory.checkAroundTerritory(player, x, 1, z, 1) || count == 0){

                        //important value
                        double money = Vault.economy.getBalance(player);
                        double price = initial_value * Math.pow(common_ratio, count);

                        //if player is able to purchase territory.
                        if(money >= price){
                            //if the territory has nobody owned and created successfully.
                            if(territory.createRecord(player)){
                                Vault.economy.withdrawPlayer(player, price);
                                player.sendMessage(ChatColor.GREEN + "Successfully purchased territory.");
                                player.sendMessage((money > price ? ChatColor.GREEN : ChatColor.RED) + "Next territory: (" + money + "/" + price + ")");
                            }else{
                                player.sendMessage(ChatColor.RED + "Unable to purchased current territory.\nMaybe current territory already belongs to someone.\nIf not, contract to admin.");
                            }
                        }else{
                            player.sendMessage(ChatColor.RED + "You have no enough money to purchase territory.");
                            player.sendMessage((money > price ? ChatColor.GREEN : ChatColor.RED) + "Next territory: (" + money + "/" + price + ")");
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "Unable to purchase a territory which are not around your own territory.");
                    }
                    return true;
                    //abandon territory
                }else if(args[0].equalsIgnoreCase("abandon")){
                    if(territory.abandonRecord(player)){
                        player.sendMessage(ChatColor.GREEN + "Abandon successfully");
                        Vault.economy.depositPlayer(player, initial_value * Math.pow(common_ratio, Territory.getPlayerTerritoryCount(player) - config.getDouble("setting.abandon_decay_rate")));
                    }else{
                        player.sendMessage(ChatColor.RED + "Unable to abandon current territory.\nPlease make sure you are standing on your own territory.");
                    }
                    return true;

                    //show map
                }else if(args[0].equalsIgnoreCase("map")){
                    String txt = "\n" + ChatColor.GREEN + "[=====================Display Map====================]\n";

                    int row = 53;
                    int col = 9;
                    int[][] arr = territory.getMap(player, row, col);

                    for(int i = 0; i < col; i++, txt += "\n"){
                        for(int j = 0; j < row; j++){
                            txt += (arr[j][i] == 0 ? ChatColor.GRAY : arr[j][i] == 1 ? ChatColor.GREEN : arr[j][i] == 2 ? ChatColor.BLUE : ChatColor.RED) + (i == col/2 && j == row/2 ? "+" : arr[j][i] == 0 ? "O" : "X");
                        }
                    }

                    player.sendMessage(txt);
                    return true;

                    //print remaining money and required price of next territory.
                }else if(args[0].equalsIgnoreCase("next")){
                    //important value
                    double money = Vault.economy.getBalance(player);
                    double price = initial_value * Math.pow(common_ratio, Territory.getPlayerTerritoryCount(player));

                    player.sendMessage((money > price ? ChatColor.GREEN : ChatColor.RED) + "Next territory: (" + money + "/" + price + ")");

                    return true;
                }else if(args[0].equalsIgnoreCase("own")){
                    player.sendMessage(ChatColor.GREEN + "Owned territory(s): " + Territory.getPlayerTerritoryCount(player));
                    return true;
                }else if(args[0].equalsIgnoreCase("condition")){
                    player.sendMessage("Condition: " + (Territory.checkAroundTerritory(player, chunk.getX(), 1, chunk.getZ(), 1) ? ChatColor.GREEN + "Satisfied." : ChatColor.RED + "Unqualified."));
                    return true;
                }else{
                    player.sendMessage(ChatColor.RED + "Command not found.");
                    return true;
                }
            }else{
                player.sendMessage(ChatColor.RED + "Too few parameters has been entered.");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.RED + "ON9 WARNING: You're so on9 that are not permitted to use this commands.");
        }
        return false;
    }
}
