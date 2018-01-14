package ru.pearx.cwl;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.pearx.cwl.commands.GenWhitelistCommand;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


/**
 * Created by me on 23.02.17.
 */
@Plugin(id = "cwl", name = "Custom Whitelist", authors = "mrAppleXZ", description = "Customize your whitelist message & generate the whitelist!", version = "1.0.0")
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
    private String wlMessage;
    private DataSource dbConnection;
    private String dbQuery;

    public Logger getLog()
    {
        return log;
    }

    public String getWhitelistMessage()
    {
        return wlMessage;
    }

    public DataSource getDbConnection()
    {
        return dbConnection;
    }

    public String getDbQuery()
    {
        return dbQuery;
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

    @Listener
    public void start(GameStartedServerEvent e) throws IOException, SQLException
    {
        INSTANCE = this;
        if (!cfgPath.exists())
        {
            Sponge.getAssetManager().getAsset(this, "cwl.conf").get().copyToFile(cfgPath.toPath());
        }
        cfg = configManager.load();
        wlMessage = cfg.getNode("main", "whitelist").getString();
        dbConnection = Sponge.getServiceManager().provide(SqlService.class).get().getDataSource(cfg.getNode("main", "db_connection").getString());
        dbQuery = cfg.getNode("main", "db_query").getString();
        Sponge.getCommandManager().register(this,
                CommandSpec.builder().permission("cwl.command").child(
                        CommandSpec.builder().permission("cwl.command.whitelist").executor(new GenWhitelistCommand()).build(), "whitelist", "gen-whitelist"
                ).child(
                        CommandSpec.builder().permission("cwl.command.ops").executor(new GenWhitelistCommand()).build(), "ops", "gen-ops"
                ).build(), "cwl");
    }
}