package BabyBaby.Command.commands.Owner;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class RoleChangeCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "rolechange";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();

        if(Boolean.parseBoolean(cmds.get(0))){
            ctx.getGuild().addRoleToMember(ctx.getMember(), ctx.getGuild().getRoleById(cmds.get(1))).queue();
        } else {
            ctx.getGuild().removeRoleFromMember(ctx.getMember(), ctx.getGuild().getRoleById(cmds.get(1))).queue();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
    return StandardHelp.Help(prefix, getName(), "<boolean> <roleID>", "Add or remove a Role.");
    }
    
}
