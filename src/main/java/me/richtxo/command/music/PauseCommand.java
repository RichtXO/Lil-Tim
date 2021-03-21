package me.richtxo.command.music;

import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;

public class PauseCommand extends Command {
    public PauseCommand(){
        this.name = "pause";
        this.category = "music";
        this.help = "Pauses the music so you can hear other people";
    }

    @Override
    public void handle(CommandContext ctx) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getEvent().getGuild());

        if (musicManager.player.isPaused()){
            ctx.getEvent().getChannel().sendMessage("I'm already paused; stop shutting me up!").queue();
            return;
        }

        musicManager.player.setPaused(true);
        ctx.getEvent().getChannel().sendMessage("Paused! Use `" + System.getenv("PREFIX") +
                "resume` to resume!").queue();
    }
}
