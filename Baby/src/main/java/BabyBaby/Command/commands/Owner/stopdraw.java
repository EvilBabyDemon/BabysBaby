package BabyBaby.Command.commands.Owner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class stopdraw implements IOwnerCMD{

    @Override
    public String getName() {
        return "stopdraw";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        PlaceDraw.on = !PlaceDraw.on;
        if(PlaceDraw.on){
            new PlaceDraw().drawing();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Turn draw on and off.");
    }
    
}
