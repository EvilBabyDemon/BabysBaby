package BabyBaby.Command.commands.Owner;


import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SpeedCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "speed";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        long all = Data.otherdel+Data.mydel;
        ctx.getChannel().sendMessage("All deletes: " + all  + " Others: " + Data.otherdel + " Mine: " + Data.mydel + "\n" + (Data.mydel/all) + "%" ).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get the number of deletes in <#815881148307210260> from my bot.");
    }
    
}
