package BabyBaby.Command.commands.Owner;


import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.Command.commands.Bot.button;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SetButtonCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "setbutton";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        button.firstplace = Integer.parseInt(ctx.getArgs().get(0));
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<int>", "Sets a number higher, idk.");
    }
    
}
