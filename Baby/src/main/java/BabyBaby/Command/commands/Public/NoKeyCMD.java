package BabyBaby.Command.commands.Public;


import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import CryptPart.VWA_MainEntschluesseln;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class NoKeyCMD implements PublicCMD {

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
            if(first)
                x = Integer.parseInt(arg);
            else
                content += arg + " ";
        }

        channel.sendMessage(VWA_MainEntschluesseln.Viginere(content, x)).queue();

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<text encrypted with Caesar or Viginere>", "Tries to encrypt text that was encrypted with Caesar or Viginere where you dont know the key.");
    }
    
}
