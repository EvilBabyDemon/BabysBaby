package BabyBaby.Command.commands.Owner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SpeedCMD implements IOwnerCMD {

    @Override
    public String getName() {
        return "speed";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        long all = Data.otherdel + Data.mydel;
        double per = 0;
        try {
            per = Data.mydel / (all + 0.0);
        } catch (Exception e) {
        }
        ctx.getChannel()
                .sendMessage(
                        "All deletes: " + all + " Others: " + Data.otherdel + " Mine: " + Data.mydel + "\n" + per + "%")
                .queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "",
                "Get the number of deletes in <#815881148307210260> from my bot.");
    }

}
