package BabyBaby.Command;

import net.dv8tion.jda.api.entities.MessageEmbed;

public interface IPublicCMD extends IAdminCMD {

    default boolean getWhiteListBool() {
        return false;
    }

    default void handleOwner(CommandContext ctx) {
        handleAdmin(ctx);
    }

    default void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    void handlePublic(CommandContext ctx);

    default MessageEmbed getOwnerHelp(String prefix) {
        return getAdminHelp(prefix);
    }

    default MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    MessageEmbed getPublicHelp(String prefix);
}