package me.richtxo.command.info;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UserInfoCommand extends Command {
    public UserInfoCommand(){
        this.name = "userinfo";
        this.category = "info";
        this.help = "Displays information about the person you're stalking";
        this.aliases = new String[]{"ui"};
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.getArgs().isEmpty()) {
            ctx.getEvent().getChannel().sendMessage("You expect me to look at no one? You stupid, " +
                Objects.requireNonNull(ctx.getEvent().getMember()).getEffectiveName() + "!").queue();
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //Time formatter
        try{
            Member name = ctx.getEvent().getMessage().getMentionedMembers().get(0);
            EmbedBuilder eb = new EmbedBuilder()
                .setColor(0xad0000)
                .setThumbnail(name.getUser().getAvatarUrl())
                .setAuthor("Information on " + name.getUser().getName())
                .setDescription(name.getUser().getName() + " joined on " + name.getTimeJoined().format(fmt) + " :clock: ")
                .addField("Display Name: ", name.getEffectiveName(), false)
                .addField("Account Created:", name.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                .addField("User Id + Mention", String.format("%s (%s)", name.getUser().getId(), name.getAsMention()), false);
            ctx.getEvent().getChannel().sendMessage(eb.build()).queue();
        }catch (IndexOutOfBoundsException ex){
            ctx.getEvent().getChannel().sendMessage("You need to provide the name as a mention.").queue();
        }
    }
}
