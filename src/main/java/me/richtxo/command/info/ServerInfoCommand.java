package me.richtxo.command.info;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerInfoCommand extends Command {

    public ServerInfoCommand(){
        this.name = "serverinfo";
        this.category = "info";
        this.help = "SHows information about this server";
    }

    @Override
    public void handle(CommandContext ctx) {
        Guild guild = ctx.getGuild();

        String generalInfo = String.format(
            "**Owner**: <@%s>\n**Region**: %s\n**Creation Date**: %s\n**Verification Level**: %s",
            guild.getOwnerId(),
            guild.getRegion().getName(),
            guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
            convertVerificationLevel(guild.getVerificationLevel())
        );

        String memberInfo = String.format(
            "**Total Roles**: %s\n**Total Members**: %s\n**Online Members**: %s\n**Offline Members**: %s\n**Bot Count**: %s",
            guild.getRoleCache().size(),
            guild.getMemberCount(),
            guild.getMembers().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.ONLINE).count(),
            guild.getMembers().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.OFFLINE).count(),
            guild.getMembers().stream().filter((m) -> m.getUser().isBot()).count()
        );

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("*Server info for*" + guild.getName())
            .setThumbnail(guild.getIconUrl())
            .addField("_General Info_", generalInfo, false)
            .addField("_Role And Member Counts_", memberInfo, false)
            .setColor(0xad0000)
            .setFooter("Lil Tim by RichtXO", String.valueOf(ctx.getEvent().getJDA().getSelfUser().getAvatarUrl()));

        ctx.getEvent().getChannel().sendMessage(embed.build()).queue();
    }

    private String convertVerificationLevel(Guild.VerificationLevel lvl) {
        String[] names = lvl.name().toLowerCase().split("_");
        StringBuilder out = new StringBuilder();

        for (String name : names) {
            out.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1)).append(" ");
        }

        return out.toString().trim();
    }
}
