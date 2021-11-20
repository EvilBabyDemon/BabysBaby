package BabyBaby.Command.commands.Public;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SourceCMD implements PublicCMD{

    @Override
    public boolean getWhiteListBool(){
        return true;
    }

    @Override
    public String getName() {
        return "source";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        ctx.getChannel().sendMessage("<https://github.com/EvilBabyDemon/BabysBaby>").queue();
        ctx.getMessage().addReaction(Data.check).queue();
    }   

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get the link to my github page.");
    }
    
}
