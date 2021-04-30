package BabyBaby.Command.commands.Owner;

import java.io.IOException;

import BabyBaby.CmdHandler;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ProletHelpCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "prolethelp";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        
        CmdHandler handler;
        try {
            handler = new CmdHandler(null);
            PublicCMD cmd = handler.searchPublicCommand("help");
            cmd.handleAdmin(ctx);   
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get Help of a Admin.");
    }
    
}
