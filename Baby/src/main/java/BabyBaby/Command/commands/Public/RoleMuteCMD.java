package BabyBaby.Command.commands.Public;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class RoleMuteCMD implements PublicCMD {

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        // TODO Auto-generated method stub
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "rolemute";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        // TODO Auto-generated method stub
        
        //get Roles from user

        //save roles

        // remove roles


    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
