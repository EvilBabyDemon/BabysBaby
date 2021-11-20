package BabyBaby.Command.commands.Public;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PolyCMD implements PublicCMD{

    @Override
    public boolean getWhiteListBool(){
        return true;
    }

    @Override
    public String getName() {
        return "poly";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        String sender = "https://polybox.ethz.ch/index.php/s/WXf1p3ODpDdpnRH";
        channel.deleteMessageById(ctx.getMessage().getId()).queue();
        channel.sendMessage("<" + sender + ">").queue();
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get the link to my Polybox.");
    }
    
}
