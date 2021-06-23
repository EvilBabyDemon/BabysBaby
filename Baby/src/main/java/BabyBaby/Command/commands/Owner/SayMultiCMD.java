package BabyBaby.Command.commands.Owner;

import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SayMultiCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "for";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
       
        MessageChannel channel = ctx.getChannel();
        
        List<String> args = ctx.getArgs();
        LinkedList<String> cmds = new LinkedList<>();

        for (String arg : args) {
            cmds.add(arg);
        }

        int x = Integer.parseInt(cmds.get(0));

        Message message = ctx.getMessage();
        
        String content = message.getContentRaw();

        content = content.substring(1 + getName().length() + 1 + cmds.get(0).length());

        channel.deleteMessageById(message.getId()).queue();
        for (int i = 0; i < x; i++) {
            channel.sendMessage(content).queue();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<int> <text to spam>", "Iterative spam function.");
    }
    

}
