package com.c010ur1355.simpleterritory;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener{

    //Cancel event when territory does not belongs player.
    //When player attempted to place block
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(!e.isCancelled()){
            //instantiation
            Territory territory = new Territory(e.getBlock().getChunk().getX(), e.getBlock().getChunk().getZ());

            //deserialization to instance
            if(territory.getRecord()){

                //get territory's owner team.
                Team team = new Team(territory.owner);

                //if player does not equal to owner
                if(territory.owner.equalsIgnoreCase(e.getPlayer().getUniqueId().toString())){
                    return;
                //if owner's team exists
                }else if(team.getTeam() == Team.RESULT.RECORD_EXISTED){
                    if(team.member != null){
                        if(team.member.length >= 0){
                            //if player belongs to owner's team
                            if(team.raw.contains(e.getPlayer().getUniqueId().toString())){
                                return;
                            }
                        }
                    }
                }
            }

            e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to modify anything in current territory.");
            e.setCancelled(true);
        }
    }

    //When player attempted to break block
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(!e.isCancelled()){
            Territory territory = new Territory(e.getBlock().getChunk().getX(), e.getBlock().getChunk().getZ());

            if(territory.getRecord()){
                Team team = new Team(territory.owner);

                if(territory.owner.equalsIgnoreCase(e.getPlayer().getUniqueId().toString())){
                    return;
                }else if(team.getTeam() == Team.RESULT.RECORD_EXISTED){
                    if(team.member != null){
                        if(team.member.length >= 0){
                            if(team.raw.contains(e.getPlayer().getUniqueId().toString())){
                                return;
                            }
                        }
                    }
                }
            }

            e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to modify anything in current territory.");
            e.setCancelled(true);
        }
    }

    //When player attempted to modify block
    @EventHandler
    public void onUse(PlayerInteractEvent e){
        if(!e.isCancelled()){
            Chunk chunk = e.getClickedBlock().getLocation().getChunk();

            int x = chunk.getX();
            int z = chunk.getZ();

            Territory territory = new Territory(x, z);

            if(territory.getRecord()){
                Team team = new Team(territory.owner);

                if(territory.owner.equalsIgnoreCase(e.getPlayer().getUniqueId().toString())){
                    return;
                }else if(team.getTeam() == Team.RESULT.RECORD_EXISTED){
                    if(team.member != null){
                        if(team.member.length >= 0){
                            if(team.raw.contains(e.getPlayer().getUniqueId().toString())){
                                return;
                            }
                        }
                    }
                }
            }

            e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to modify anything in current territory.");
            e.setCancelled(true);
        }
    }

    //Cancel event when territory does not belongs player and current territory has been registered.
    //When player attempted to invoke method on entity
    @EventHandler
    public void onClick(PlayerInteractEntityEvent e){
        if(!e.isCancelled()){
            Chunk chunk = e.getRightClicked().getLocation().getChunk();

            int x = chunk.getX();
            int z = chunk.getZ();

            Territory territory = new Territory(x, z);

            if(territory.getRecord()){
                if(!territory.owner.equalsIgnoreCase(e.getPlayer().getUniqueId().toString())) {
                    Team team = new Team(territory.owner);

                    if(team.getTeam() == Team.RESULT.RECORD_EXISTED) {
                        if (team.member != null) {
                            if (team.member.length >= 0) {
                                if (team.raw.contains(e.getPlayer().getUniqueId().toString())) {
                                    return;
                                }
                            }
                        }
                    }

                    e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to modify anything in current territory.");
                    e.setCancelled(true);
                }
            }
        }
    }

    //When player attempted to damage entity(player, animal, mobs)
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent e){
        if(!e.isCancelled()){
            Chunk chunk = e.getEntity().getLocation().getChunk();

            int x = chunk.getX();
            int z = chunk.getZ();

            Territory territory = new Territory(x, z);

            if(territory.getRecord()){
                if(!territory.owner.equalsIgnoreCase(e.getDamager().getUniqueId().toString())){
                    Team team = new Team(territory.owner);

                    if(team.getTeam() == Team.RESULT.RECORD_EXISTED) {
                        if (team.member != null) {
                            if (team.member.length >= 0) {
                                if (team.raw.contains(e.getEntity().getUniqueId().toString())) {
                                    return;
                                }
                            }
                        }
                    }

                    e.getDamager().sendMessage(ChatColor.RED + "You are not permitted to modify anything in current territory.");
                    e.setCancelled(true);
                }
            }
        }
    }
}
