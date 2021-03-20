package me.richtxo.command;

public abstract class Command {
    // General Information about Command
    public String name = "null";
    public String category = null;
    public String help = "no help available";
    public String[] aliases = new String[0];
    public boolean ownerCommand = true;

    // Handle Event for Command
    public abstract void handle(CommandContext ctx);


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
