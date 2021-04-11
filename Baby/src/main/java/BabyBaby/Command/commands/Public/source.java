package BabyBaby.Command.commands.Public;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class source implements PublicCMD{

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "source";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        ctx.getChannel().sendMessage("Soon:tm:").queue();
    }   

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get the link to my github page.");
    }
    
}
