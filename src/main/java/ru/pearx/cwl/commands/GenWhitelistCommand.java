/*
 *     Copyright © 2018 mrAppleXZ.
 *     This file is part of Custom Whitelist.
 *     Custom Whitelist is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Custom Whitelist is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Custom Whitelist.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    private CWL cwl;

    public GenWhitelistCommand(CWL cwl)
    {
        this.cwl = cwl;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        try
        {
            cwl.syncWhitelist();
        }
        catch (Exception e)
        {
            throw new CommandException(Text.of("An exception occurred while syncing the whitelist!"), e);
        }

        return CommandResult.success();
    }
}
