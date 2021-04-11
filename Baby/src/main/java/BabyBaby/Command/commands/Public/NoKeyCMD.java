package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import CryptPart.VWA_MainEntschluesseln;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class NoKeyCMD implements PublicCMD {

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
        return "nokey";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        
        MessageChannel channel = ctx.getChannel();
        //Message message = ctx.getMessage();
        List<String> args = ctx.getArgs();
        int x = Integer.parseInt(args.remove(0));
		String content = "";

        for (String var : args) {
            content += var + " ";
        }

        //message.addReaction(check).queue();
        channel.sendMessage(VWA_MainEntschluesseln.Viginere(content, x)).queue();

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        // TODO Standard Help
        return null;
    }
    
}
