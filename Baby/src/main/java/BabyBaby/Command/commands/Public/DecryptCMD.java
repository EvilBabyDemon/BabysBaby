package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import CryptPart.KeyDecrypt;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import BabyBaby.ColouredStrings.StandardHelpEmbed ;

public class DecryptCMD implements PublicCMD {


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
        return "decrypt";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        // TODO Auto-generated method stub



        MessageChannel channel = ctx.getChannel();
        //Message message = ctx.getMessage();

        String content = "";

        List<String> args = ctx.getArgs();

        for (String var : args) {
            content += var + " ";
        }
        

        int i = 0;
        for (; i < content.length(); i++) {
            if (content.charAt(i) == ' ') {
                break;
            }
        }

        String key = content.substring(0, i);
        content = content.substring(i + 1);

        /*if (cryptdeleter)
            channel.deleteMessageById(message.getId()).queue();
        else
            message.addReaction(check).queue();
        */
        channel.sendMessage(KeyDecrypt.decrypter(content, key)).queue();


    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        StandardHelpEmbed make = new StandardHelpEmbed();
        return make.StandardHelp(prefix, getName(), "Decrypt text with Viginere or Caesar.", "decrypt <text>");
    }
    
}
