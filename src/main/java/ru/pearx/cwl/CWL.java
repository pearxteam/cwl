package ru.pearx.cwl;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;


/**
 * Created by me on 23.02.17.
 */
@Plugin(id = "cwl", name = "Custom Whitelist Message", authors = "mrAppleXZ", description = "Customize your whitelist message!", version = "1.0.0")
public class CWL
{
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File cfgPath;

    public String wlMessage;

    @Listener
    public void join(ClientConnectionEvent.Auth e)
    {
        if(Sponge.getServer().hasWhitelist())
        {
            Optional<WhitelistService> wls = Sponge.getServiceManager().provide(WhitelistService.class);
            if(wls.isPresent())
            {
                if (!wls.get().isWhitelisted(e.getProfile()))
                {
                    e.setCancelled(true);
                    e.setMessage(TextSerializers.FORMATTING_CODE.deserialize(wlMessage));
                }
            }
        }
    }

    @Listener
    public void start(GameStartedServerEvent e) throws IOException
    {
       if(!cfgPath.exists())
       {
           Sponge.getAssetManager().getAsset(this, "cwl.conf").get().copyToFile(cfgPath.toPath());
       }
       wlMessage = configManager.load().getNode("main", "whitelist").getString();
    }

}
