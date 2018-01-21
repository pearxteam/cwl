package ru.pearx.cwl.commands;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import ru.pearx.cwl.CWL;

import java.io.IOException;
import java.sql.SQLException;

/*
 * Created by mrAppleXZ on 21.01.18 11:06.
 */
public class ReloadConfigCommand implements CommandExecutor
{
    private CWL cwl;

    public ReloadConfigCommand(CWL cwl)
    {
        this.cwl = cwl;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        try
        {
            cwl.reload();
        }
        catch (IOException | SQLException | UncheckedExecutionException e)
        {
            throw new CommandException(Text.of("An exception occurred while reloading the CWL plugin!"), e);
        }
        return CommandResult.success();
    }
}
