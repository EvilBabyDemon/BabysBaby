package BabyBaby.Command.commands.Slash;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class RoleSlashCMD implements ISlashCMD {

    @Override
    public String getName() {
        return "role";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        Role role = event.getOption("role").getAsRole();
        if(!Data.roles.contains(role.getId()) && !event.getMember().getId().equals(Data.myselfID)){
            String nope = "I can't give you that role.";
            if(failed){
                event.getUser().openPrivateChannel().complete().sendMessage(nope).complete();
            } else {
                hook.editOriginal(nope).queue();   
            }
        } else {
            Helper.roleGiving(event.getMember(), event.getGuild(), failed, role, hook);
        }
        
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl role = new CommandDataImpl(getName(), "A command to get/remove a role.");
        role.addOption(OptionType.ROLE, "role", "The Role you want to have or get removed.", true);
        return role;
    }
    
}
