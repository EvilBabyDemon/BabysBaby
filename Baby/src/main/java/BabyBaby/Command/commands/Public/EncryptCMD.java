package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.ColouredStrings.StandardHelpEmbed;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import CryptPart.VWA_Verschluesseln;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EncryptCMD implements PublicCMD {

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
        return "crypt";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        
        MessageChannel channel = ctx.getChannel();

        List<String> args = ctx.getArgs();

        String key = args.remove(0);
        String content = "";

        for (String var : args) {
            content = var + " ";
        }


        
        /*if (cryptdeleter)
            channel.deleteMessageById(message.getId()).queue();
        else
            message.addReaction(check).queue();
        */
        channel.sendMessage(VWA_Verschluesseln.encrypter(content, key)).queue();

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        StandardHelpEmbed make = new StandardHelpEmbed();
        return make.    StandardHelp(prefix, getName(), "Encrypt text with Viginere or Caesar.", "crypt <key> <text>");
    }
    
}
