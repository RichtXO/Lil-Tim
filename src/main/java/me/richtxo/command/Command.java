package me.richtxo.command;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

public abstract class Command {
    // General Information about Command
    public String name = "null";
    public String category = null;
    public String help = "no help available";
    public String[] aliases = new String[0];
    public boolean ownerCommand = true;

    // Handle Event for Command
    public abstract void handle(CommandContext ctx) throws ParseException, IOException, SpotifyWebApiException;

    public String getName() {
        return this.name;
    }
    public String getCategory(){
        return this.category;
    }
    public String getHelp() {
        return this.help;
    }
    public String[] getAliases(){
        return aliases;
    }
}
