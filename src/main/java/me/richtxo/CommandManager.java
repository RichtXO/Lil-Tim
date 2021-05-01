package me.richtxo;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import me.richtxo.command.info.*;
import me.richtxo.command.lilTim.*;
import me.richtxo.command.music.*;
import me.richtxo.command.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<Command> commandlist = new ArrayList<>();

    public CommandManager(EventWaiter waiter){
        addCommand(new AboutCommand());
        addCommand(new HelpCommand(this));

        addCommand(new PingCommand());

        addCommand(new ServerInfoCommand());
        addCommand(new UserInfoCommand());

        addCommand(new JoinCommand());
        addCommand(new LeaveCommand());
        addCommand(new PlayCommand(waiter));
        addCommand(new PauseCommand());
        addCommand(new StopCommand());
        addCommand(new SkipCommand());
        addCommand(new NowPlaying());
        addCommand(new QueueCommand());
        addCommand(new RepeatCommand());
        addCommand(new RepeatOffCommand());
    }

    public List<Command> getCommands(){
        return commandlist;
    }

    public Command getCommand(String search){
        for (Command cmd : this.commandlist)
            if (cmd.getName().equals(search) || Arrays.asList(cmd.getAliases()).contains(search))
                return cmd;

        return null;
    }

    private void addCommand(Command cmd){
        boolean nameFound = this.commandlist.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (!nameFound)
            commandlist.add(cmd);
    }

    void handle(GuildMessageReceivedEvent event){
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(System.getenv("PREFIX")), "")
                .split("\\s+");

        Command cmd = getCommand(split[0].toLowerCase());
        if (cmd != null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);
            CommandContext ctx = new CommandContext(event, args);
            cmd.handle(ctx);
        }
    }
}
