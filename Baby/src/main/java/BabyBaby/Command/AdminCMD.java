package BabyBaby.Command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface AdminCMD extends OwnerCMD {

    void handleAdmin(CommandContext ctx);

    MessageEmbed getAdminHelp(String prefix);
}