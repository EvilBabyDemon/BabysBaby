package BabyBaby.Command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface PublicCMD extends AdminCMD {

    void handlePublic(CommandContext ctx);

    MessageEmbed getPublicHelp(String prefix);
}