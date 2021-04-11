package BabyBaby.Command.commands.Admin;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MutePersonCMD implements AdminCMD {

    @Override
    public void handleOwner(CommandContext ctx) {
        handleAdmin(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getAdminHelp(prefix);
    }

    @Override
    public String getName() {
        return "ffs";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        // TODO Auto-generated method stub

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<User Ping> [Time]", "Command to mute a person.");
    }
    
}
