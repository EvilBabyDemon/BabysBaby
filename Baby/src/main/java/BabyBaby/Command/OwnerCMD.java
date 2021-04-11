package BabyBaby.Command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface OwnerCMD extends Command{

    void handleOwner(CommandContext ctx);

    MessageEmbed getOwnerHelp(String prefix);
}