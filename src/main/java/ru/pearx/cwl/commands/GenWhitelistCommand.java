package ru.pearx.cwl.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.Text;
import ru.pearx.cwl.CWL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/*
 * Created by mrAppleXZ on 14.01.18 12:16.
 */
public class GenWhitelistCommand implements CommandExecutor
{
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        WhitelistService wh = Sponge.getServiceManager().provide(WhitelistService.class).get();
        wh.getWhitelistedProfiles().clear();
        try(Connection conn = CWL.INSTANCE.getDbConnection().getConnection())
        {
            try(PreparedStatement st = conn.prepareStatement(CWL.INSTANCE.getDbQuery()))
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
        catch (SQLException e)
        {
            CWL.INSTANCE.getLog().error("An SQLException occurred!", e);
            throw new CommandException(Text.of("An SQLException occurred!"), e);
        }
        return CommandResult.success();
    }
}
