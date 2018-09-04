package com.c010ur1355.simpleterritory;

import java.util.logging.Logger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.c010ur1355.simpleterritory.Vault;

public class main extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable(){
        //command setup
        getCommand("trt").setExecutor(new Commands());

        //event setup
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        //load config
        loadConfig();

        //database connection
        String host     = getConfig().getString("database.host");
        int port        = getConfig().getInt("database.port");
        String database = getConfig().getString("database.database");
        String username = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");

        Database.setup(host, port, database, username, password);

        //Vault API setup
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    //Configuration
    private void loadConfig(){
        getConfig().options().copyDefaults();
        saveConfig();
    }

    //Vault API
    private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null) {
            return false;
        }

        Vault.economy = rsp.getProvider();
        return Vault.economy != null;
    }
}
