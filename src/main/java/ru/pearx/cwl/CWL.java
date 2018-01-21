package ru.pearx.cwl;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.pearx.cwl.commands.GenWhitelistCommand;
import ru.pearx.cwl.commands.ReloadConfigCommand;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Created by me on 23.02.17.
 */
@Plugin(id = "cwl", name = "Custom Whitelist", authors = "mrAppleXZ", description = "Customize your whitelist message & generate the whitelist!", version = "1.2.0")
public class CWL
{
    public static CWL INSTANCE;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private CommentedConfigurationNode cfg;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private File cfgPath;

    @Inject
    private Logger log;

    private String wlMessage = "Send a whitelist request firstly!";
    private DataSource dbConnection ;
    private String dbQuery = "SELECT `username`, `uuid` from `players` WHERE `access` = 2;";
    private int secondsSyncDelay = 600;

    public Task syncTask;

    public Logger getLog()
    {
        return log;
    }

    public String getWhitelistMessage()
    {
        return wlMessage;
    }

    @Nullable
    public DataSource getDbConnection()
    {
        return dbConnection;
    }

    public String getDbQuery()
    {
        return dbQuery;
    }

    public int getSecondsSyncDelay()
    {
        return secondsSyncDelay;
    }

    @Listener
    public void join(ClientConnectionEvent.Auth e)
    {
        if (Sponge.getServer().hasWhitelist())
        {
            Optional<WhitelistService> wls = Sponge.getServiceManager().provide(WhitelistService.class);
            if (wls.isPresent())
            {
                if (!wls.get().isWhitelisted(e.getProfile()))
                {
                    e.setCancelled(true);
                    e.setMessage(TextSerializers.FORMATTING_CODE.deserialize(getWhitelistMessage()));
                }
            }
        }
    }

    public void reloadConfig() throws IOException, SQLException
    {
        if (!cfgPath.exists())
        {
            Sponge.getAssetManager().getAsset(this, "cwl.conf").get().copyToFile(cfgPath.toPath());
        }
        cfg = configManager.load();
        wlMessage = cfg.getNode("main", "whitelist").getString();
        dbConnection = Sponge.getServiceManager().provide(SqlService.class).get().getDataSource(cfg.getNode("main", "db_connection").getString());
        dbQuery = cfg.getNode("main", "db_query").getString();
        secondsSyncDelay = cfg.getNode("main", "sync_delay").getInt();
    }

    @Listener
    public void start(GameStartedServerEvent e) throws IOException
    {
        INSTANCE = this;
        Sponge.getCommandManager().register(this,
                CommandSpec.builder().permission("cwl.command").child(
                        CommandSpec.builder().permission("cwl.command.whitelist").executor(new GenWhitelistCommand(this)).build(), "whitelist", "gen-whitelist"
                ).child(
                        CommandSpec.builder().permission("cwl.command.reload").executor(new ReloadConfigCommand(this)).build(), "reload", "reload-config"
                ).build(), "cwl");
        try
        {
            reload();
        }
        catch (SQLException | UncheckedExecutionException e1)
        {
            getLog().error("An exception occurred while setting up the CWL plugin! Recheck the database settings and run /cwl reload!");
        }
    }

    public void syncWhitelist() throws SQLException
    {
        if(getDbConnection() == null)
            getLog().error("Can't sync the whitelist! Recheck the database settings and run /cwl reload!");
        getLog().info("Syncing whitelist...");
        WhitelistService wh = Sponge.getServiceManager().provide(WhitelistService.class).get();
        wh.getWhitelistedProfiles().clear();
        try(Connection conn = getDbConnection().getConnection())
        {
            try(PreparedStatement st = conn.prepareStatement(getDbQuery()))
            {
                ResultSet res = st.executeQuery();
                while(res.next())
                {
                    String name = res.getString(1);
                    String uuid = res.getString(2);
                    wh.addProfile(GameProfile.of(UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")), name));
                }
            }
        }
        getLog().info("The whitelist was successfully synced!");
    }

    public void reloadTask()
    {
        if(syncTask != null)
            syncTask.cancel();
        if(getSecondsSyncDelay() >= 1)
        {
            syncTask = Task.builder().execute(
                    () ->
                    {
                        try
                        {
                            syncWhitelist();
                        }
                        catch (Exception e1)
                        {
                            getLog().error("An exception occurred while syncing whitelist!", e1);
                        }
                    }
            ).interval(getSecondsSyncDelay(), TimeUnit.SECONDS).submit(this);
        }
    }

    public void reload() throws IOException, SQLException
    {
        reloadConfig();
        reloadTask();
    }
}