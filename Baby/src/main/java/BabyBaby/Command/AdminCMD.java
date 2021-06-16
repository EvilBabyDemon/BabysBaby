package BabyBaby.Command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface AdminCMD extends OwnerCMD {

    default void handleOwner(CommandContext ctx){
        handleAdmin(ctx);
    }

    void handleAdmin(CommandContext ctx);

    default MessageEmbed getOwnerHelp(String prefix){
        return getAdminHelp(prefix);
    }

    MessageEmbed getAdminHelp(String prefix);
}