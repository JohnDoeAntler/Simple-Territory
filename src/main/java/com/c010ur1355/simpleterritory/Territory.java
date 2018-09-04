package com.c010ur1355.simpleterritory;

import com.c010ur1355.simpleterritory.Database;
import com.mysql.fabric.xmlrpc.base.Data;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Territory {
    public int x;
    public int z;
    public String owner;

    //constructor
    public Territory(int x, int z){
        this.x = x;
        this.z = z;
    }

    private void removeElement(Object[] arr, int removedIdx){
        System.arraycopy(arr, removedIdx + 1, arr, removedIdx, arr.length - 1 - removedIdx);
    }

    //deserialization and return existence
    public boolean getRecord(){
        try {
            PreparedStatement statement = Database.getConnection().prepareStatement("SELECT * FROM territory WHERE x = " + x + " AND z = " + z);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                owner = resultSet.getString("owner");
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //create territory
    public boolean createRecord(Player player){
        if(!getRecord()){
            try {
                Database.getConnection().prepareStatement("INSERT INTO territory (x, z, owner) VALUES ('" + x + "', '" + z + "', '" + player.getUniqueId().toString() + "')").execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //remove territory
    public boolean abandonRecord(Player player){
        if(getRecord()){
            if(owner.equalsIgnoreCase(player.getUniqueId().toString())){
                try {
                    Database.getConnection().prepareStatement("DELETE FROM territory WHERE x = '" + this.x + "' AND z = '" + this.z + " '").execute();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        //no record was found.
        //the player isn't owner.
        return false;
    }

    //return a 2d-array that shows player around territory
    public int[][] getMap(Player player, int row, int col){
        if(row % 2 != 1 || col % 2 != 1) return null;

        int[][] arr = new int[row][col];
        int x = row/2;
        int z = col/2;

        try {
            PreparedStatement statement = Database.getConnection()
                    .prepareStatement("SELECT * FROM territory WHERE x <= "
                            + (this.x + x)
                            + " AND x >= "
                            + (this.x - x)
                            + " AND z <= "
                            + (this.z + z)
                            + " AND z >= "
                            + (this.z - z));

            ResultSet resultSet = statement.executeQuery();

            //create variables
            String name = player.getUniqueId().toString();

            while(resultSet.next()){
                String owner = resultSet.getString("owner");

                //if current territory's owner equals to player, return 1
                if(resultSet.getString("owner").equals(name)){
                    arr[resultSet.getInt("x")-this.x+x][resultSet.getInt("z")-this.z+z] = 1;
                }else{
                    Team team = new Team(owner);

                    //If current territory owner has a team.
                    if(team.getTeam() == Team.RESULT.RECORD_EXISTED){
                        if(team.raw != null){
                            //If player belongs to current territory owner's team, return 2.
                            if(team.raw.contains(name)){
                                arr[resultSet.getInt("x")-this.x+x][resultSet.getInt("z")-this.z+z] = 2;
                            }
                        }
                    }

                    //If current territory belongs to nobody or current territory does not team with player.
                    arr[resultSet.getInt("x")-this.x+x][resultSet.getInt("z")-this.z+z] = 3;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //return array
        return arr;
    }

    //static: asset game rules that checking around territory
    public static boolean checkAroundTerritory(Player player, int x, int x_range, int z, int z_range){
        try {
            if(Database.getConnection().prepareStatement("SELECT * FROM territory WHERE (x >= "+ (x-x_range) + " AND x <= " + (x+x_range) + ") AND (z >= " + (z-z_range) + " AND z <= " + (z+z_range) + ") AND (x = " + x + " OR z = " + z + ") AND owner = '" + player.getUniqueId().toString() + "'").executeQuery().next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //static: get player total territories count
    public static int getPlayerTerritoryCount(Player player){
        try {
            ResultSet resultSet = Database.getConnection().prepareStatement("SELECT COUNT(*) FROM territory WHERE owner = '" + player.getUniqueId().toString() + "'").executeQuery();
            if(resultSet.next()) return resultSet.getInt("COUNT(*)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}