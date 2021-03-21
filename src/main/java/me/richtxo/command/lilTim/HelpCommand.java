package me.richtxo.command.lilTim;

import me.richtxo.CommandManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends Command {
    private final CommandManager manager;

    public HelpCommand(CommandManager manager){
        this.name = "help";
        this.category = "Lil Tim";
        this.help = "Shows list of commands in me!";
        this.aliases = new String[]{"h", "commands", "cmds"};
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        if (args.isEmpty()){
            listCommands(ctx.getEvent());
        }
        else{
            TextChannel channel = ctx.getEvent().getChannel();
            Command command = manager.getCommand(args.get(0));

            if (command == null) {
                channel.sendMessage("Nothing found for " + args.get(0) + " in bot\n" +
                        "Use `" + System.getenv("PREFIX") + "help` for a list of commands").queue();
                return;
            }

            cmdInfo(command, ctx.getEvent());
        }
    }


    private void listCommands(GuildMessageReceivedEvent event){
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.getJDA().getSelfUser().getName() + " Help")
                .setColor(event.getMember().getColor())
                .setThumbnail(String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
                .setFooter("Lil Tim by RichtXO", String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
                .setDescription(" These are the available commands for Lil Tim.\n" +
                        "The bot prefix is: `" + System.getenv("PREFIX") + "`\n\n");

        List<String> categories = new ArrayList<>();

        manager.getCommands().forEach(
                (command) -> {
                    if (!categories.contains(command.category))
                        categories.add(command.category);
                }
        );
        Collections.sort(categories);

        categories.forEach(
                (category) -> {
                    String temp = manager.getCommands()
                            .stream()
                            .filter(cmd -> cmd.category.equals(category))
                            .map(Command::getName)
                            .collect(Collectors.joining(" "));

                    temp = temp.replaceAll(" ", "` `");
                    temp = "`" + temp + "`";

                    builder.addField("**" + category.toUpperCase() + ":**",
                            temp, false
                    );
                }
        );

        builder.addField("*Useful links:*",
                "[Invite Me](https://discord.com/oauth2/authorize?client_id=736748544706478080&scope=bot)" ,
                false);

        event.getChannel().sendMessage(builder.build()).queue();
    }

    private void cmdInfo(Command command, GuildMessageReceivedEvent event){
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.getJDA().getSelfUser().getName() + " Help")
                .setColor(0xad0000)
                .setThumbnail(String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
                .setFooter(command.getName() + " Command Page", event.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("The bot prefix is: `" + System.getenv("PREFIX") + "`\n\n")
                .addField("Command:", "`" + command.getName() + "`", false)
                .addField("Description:", command.getHelp(), false)
                .addField("Category:", command.getCategory(), false)
                .addField("Aliases:", "`" + String.join("`, `", command.getAliases()) + "`",
                        false);

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
