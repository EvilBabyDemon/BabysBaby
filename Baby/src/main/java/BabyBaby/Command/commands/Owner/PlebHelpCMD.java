package BabyBaby.Command.commands.Owner;

import java.io.IOException;

import BabyBaby.CmdHandler;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PlebHelpCMD implements IOwnerCMD {

    @Override
    public String getName() {
        return "plebhelp";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        
        CmdHandler handler;
        try {
            handler = new CmdHandler(null);
            IPublicCMD cmd = handler.searchPublicCommand("help");
            cmd.handlePublic(ctx);   
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get Help of a mormal User.");
    }
    
}
