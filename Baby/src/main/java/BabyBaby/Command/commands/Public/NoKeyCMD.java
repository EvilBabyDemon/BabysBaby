package BabyBaby.Command.commands.Public;


import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import CryptPart.VWA_MainEntschluesseln;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class NoKeyCMD implements IPublicCMD {

    @Override
    public String getName() {
        return "nokey";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        
        MessageChannel channel = ctx.getChannel();
		String content = "";
        boolean first = true;
        int x = 0;
        for (String arg : ctx.getArgs()) {
            if(first){
                x = Integer.parseInt(arg);
                first = false;
            }
            else
                content += arg + " ";
        }
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Encrypted text with method: " + ((x==1) ? "Friedmann" : "Kasiski"));
        eb.setDescription(VWA_MainEntschluesseln.Viginere(content, x));
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<text encrypted with Caesar or Viginere>", "Tries to encrypt text that was encrypted with Caesar or Viginere where you dont know the key.");
    }
    
}
