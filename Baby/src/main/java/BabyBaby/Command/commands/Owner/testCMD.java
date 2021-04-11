package BabyBaby.Command.commands.Owner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class testCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        
        String output = ctx.getArgs().get(0);
        output = output.replace("<", "");
        output = output.replace(">", "");
        System.out.println(output);
        
        ctx.getMessage().addReaction(output).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        // TODO Auto-generated method stub
        return StandardHelp.Help(prefix, getName(), "(whatever it is atm)", "A cmd to test things out.");
    }
    
}
