package BabyBaby.Command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface IOwnerCMD extends ICommand{

    void handleOwner(CommandContext ctx);

    MessageEmbed getOwnerHelp(String prefix);
}