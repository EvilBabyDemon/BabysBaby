package BabyBaby.Command.commands.Public;

import java.util.HashSet;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.*;

public class BlindForceCMD implements PublicCMD{
    public static HashSet<GetRolesBack> force = new HashSet<>();


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
        return "forceblind";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(Data.ethid))
        return;

        ctx.getMessage().addReaction(Data.check).queue();

        List<String> cmds = ctx.getArgs();

        if(cmds.size() == 0){
            ctx.getChannel().sendMessage("The command is +" + getName() +" <time> [unit] (default unit is minutes)").queue();
            return;
        }

        String unit = null;
        if(cmds.size()>1)
            unit = cmds.get(1);


        new BlindCMD().roleRemoval(cmds.get(0), ctx.getMember(), ctx.getGuild(), unit, true, ctx.getChannel());

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time> [unit] (Default is minutes)", "This removes all your roles and you won't see the server for that time and **there is no way to manually unblind yourself earlier**.");
    }
    
}
