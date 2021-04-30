package BabyBaby.Command.commands.Owner;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SayCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public void handleOwner(CommandContext ctx) {

        List<String> contentList = ctx.getArgs();
        if(contentList == null || contentList.size()==0)
            return;
        
        String content = ctx.getMessage().getContentRaw().substring(4);
        ctx.getChannel().deleteMessageById(ctx.getMessage().getId()).queue();
        ctx.getChannel().sendMessage(content).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<txt>", "A simple say command.");
    }
    
}
