package com.c010ur1355.simpleterritory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Team {
    public enum RESULT {
        //GET RECORD RESULT
        RECORD_EXISTED, NO_RECORD_EXISTED, CONNECTION_FAILURE,
        //EXECUTION RESULT
        SUCCESS, FAILURE,
        //MEMBER LIST MODIFICATION RESULT
        DUPLICATED, NOT_FOUND
    }

    public String name;
    public String description;
    public String owner;
    public String[] member;
    public String raw;

    public Team(String owner){
        this.owner = owner;
    }

    public RESULT getTeam(){
        try {
            ResultSet resultSet = Database.getConnection().prepareStatement("SELECT * FROM team WHERE owner = '" + owner + "'").executeQuery();
            if(resultSet.next()){
                //record exists.

                //save instance properties
                this.name = resultSet.getString("name");
                this.description = resultSet.getString("description");

                //load member
                String raw = resultSet.getString("member");

                if(raw != null){

                    String[] arr = new String[raw.length()/36];

                    //spilt raw-data to String array
                    for(int i = 0; i < raw.length(); i++){
                        arr[i/36] += raw.charAt(i);
                    }

                    //save instance properties
                    this.member = arr;
                    this.raw = raw;
                }

                //exist, so true.
                return RESULT.RECORD_EXISTED;
            }else{
                //no any single record has been found.
                return RESULT.NO_RECORD_EXISTED;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //cannot connect to database, false.
        return RESULT.CONNECTION_FAILURE;
    }

    public RESULT createTeam(String name){
        RESULT result = getTeam();

        if(result == RESULT.NO_RECORD_EXISTED){
            try {
                Database.getConnection().prepareStatement("INSERT INTO team (name, owner) VALUES ('" + name + "', '" + this.owner + "')").execute();

                //Successfully executed.
                //Set instance property.
                this.name = name;
                return RESULT.SUCCESS;
            } catch (SQLException e) {
                e.printStackTrace();
                //Connection failure.
                return RESULT.CONNECTION_FAILURE;
            }
        }

        //return record existed or connection failure.
        return result;
    }

    public RESULT removeTeam(){
        RESULT result = getTeam();

        if(result == RESULT.RECORD_EXISTED){
            try {
                Database.getConnection().prepareStatement("DELETE FROM team WHERE name = '" + this.name + "'").execute();
                //Successfully executed.
                return RESULT.SUCCESS;
            } catch (SQLException e) {
                e.printStackTrace();
                //Connection failure.
                return RESULT.CONNECTION_FAILURE;
            }
        }

        //return no record existed or connection failure.
        return result;
    }

    public RESULT addMember(String id){
        RESULT result = getTeam();

        if(result == RESULT.RECORD_EXISTED){
            //if team contains member
            if(this.member != null && this.raw != null){
                //If it exceeded maximum amount of team member
                if(this.member.length >= 10)                return RESULT.FAILURE;
                //If member list already contains current person.
                if(this.raw.contains(id))                   return RESULT.FAILURE;
            }

            try {
                Database.getConnection().prepareStatement("UPDATE team SET member = '" + this.raw + id + "' WHERE name = '" + this.name + "'").execute();
                //Successfully executed.
                //Set instance property.
                this.raw += id;

                String[] arr = new String[this.raw.length()/36];

                //spilt raw-data to String array
                for(int i = 0; i < raw.length(); i++){
                    arr[i/36] += raw.charAt(i);
                }

                this.member = arr;

                return RESULT.SUCCESS;
            } catch (SQLException e) {
                if(e.getErrorCode() == 1062)    return RESULT.DUPLICATED;
                else                            return RESULT.CONNECTION_FAILURE;
            }
        }

        //return no record existed or connection failure.
        return result;
    }

    public RESULT removeMember(String id){
        RESULT result = getTeam();

        if(result == RESULT.RECORD_EXISTED){
            if(this.member == null && this.raw == null) return RESULT.FAILURE;
            //If it has no team member
            if(this.member.length == 0)                 return RESULT.FAILURE;
            //If member list does not contains current person.
            if(!this.raw.contains(id))                  return RESULT.NOT_FOUND;

            try {
                Database.getConnection().prepareStatement("UPDATE team SET member = '" + this.raw.replace(id, "") + "' WHERE name = '" + this.name + "'").execute();
                //Successfully executed.
                //Set instance property.
                this.raw = this.raw.replace(id, "");

                String[] arr = new String[this.raw.length()/36];

                //spilt raw-data to String array
                for(int i = 0; i < raw.length(); i++){
                    arr[i/36] += raw.charAt(i);
                }

                this.member = arr;

                return RESULT.SUCCESS;
            } catch (SQLException e) {
                e.printStackTrace();
                //Connection failure.
                return RESULT.CONNECTION_FAILURE;
            }
        }

        //return no record existed or connection failure.
        return result;
    }

    public RESULT setDescription(String description){
        RESULT result = getTeam();

        if(result == RESULT.RECORD_EXISTED){
            try {
                Database.getConnection().prepareStatement("UPDATE team SET description = '" + description + "' WHERE name = '" + this.name + "'").execute();
                //Successfully changed description.
                //Set instance property.
                this.description = description;
                //Return success
                return RESULT.SUCCESS;
            } catch (SQLException e) {
                e.printStackTrace();
                //Connection failure.
                return RESULT.CONNECTION_FAILURE;
            }
        }

        //return no record existed or connection failure.
        return result;
    }
}