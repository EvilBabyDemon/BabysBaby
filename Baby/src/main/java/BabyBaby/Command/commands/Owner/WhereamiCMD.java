package BabyBaby.Command.commands.Owner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class WhereamiCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "whereami";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        for (Guild guild : ctx.getJDA().getGuilds())
            ctx.getChannel().sendMessage(guild.getName()).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Find out on which servers I am.");
    }
    
}
