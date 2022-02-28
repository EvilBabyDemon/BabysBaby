package BabyBaby.Command.commands.Slash;

import java.util.Arrays;
import java.util.LinkedList;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.Command.commands.Admin.AdminCMDs;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class AdminSlashCMD implements ISlashCMD{

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        String sub = event.getSubcommandName();
        
        switch (sub) {
            case "ban": 
                AdminCMDs.ban(event, hook, failed);
                break;
            case "kick": 
                AdminCMDs.kick(event, hook, failed);
                break;
            case "timeout":
                AdminCMDs.timeout(event, hook, failed);
                break;
            case "warnings":
                AdminCMDs.getWarnings(event, hook, failed);
                break;
            case "warn":
                AdminCMDs.warn(event, hook, failed);
                break;
            case "addrole":
                AdminCMDs.addrole(event, hook, failed);
                break;
            case "delrole":
                AdminCMDs.delRole(event, hook, failed);
                break;
            case "editassign":
                AdminCMDs.editAssign(event, hook, failed);
                break;
            case "rolebutton":
                AdminCMDs.newRole(event, hook, failed);
                break;
            case "roleassign":
                AdminCMDs.roleAssign(event, hook, failed);
                break;
            case "roleid":
                AdminCMDs.roleID(event, hook, failed);
                break;
            case "updaterole":
                AdminCMDs.updateRole(event, hook, failed);
                break;
            default:
                Helper.unhook("Smth went really wrong. Pls tell my Owner.", failed, hook, event.getUser());
                break;
        }
        

    }
    //timeout ban kick warn warnings whois rolebutton addrole assign editassign delrole roleid updaterole
    @Override
    public CommandDataImpl initialise(Guild eth) {
        //admin slash Cmds
        CommandDataImpl admin = new CommandDataImpl("admin", "All admin commands.");
        LinkedList<SubcommandData> subc = new LinkedList<>();
        
        //timeout
        SubcommandData timeout = new SubcommandData("timeout", "Cmd to timeout a user.");
        timeout.addOption(OptionType.USER, "user", "The user to timeout.", true);
        timeout.addOption(OptionType.NUMBER, "time", "The duration of the time out", true);
        timeout.addOption(OptionType.STRING, "unit", "Seconds, minutes, hours, days, years", true);
        timeout.addOption(OptionType.STRING, "reason", "Reason why user got a time out. User doesn't see that.", false);   
        subc.add(timeout);

        //ban
        SubcommandData ban = new SubcommandData("ban", "Cmd to ban a user.");
        ban.addOption(OptionType.USER, "user", "The user to ban.", true);
        ban.addOption(OptionType.STRING, "reason", "Reason why user got a ban. User doesn't see that.", false);   
        subc.add(ban);

        //kick
        SubcommandData kick = new SubcommandData("kick", "Cmd to kick a user.");
        kick.addOption(OptionType.USER, "user", "The user to kick.", true);
        kick.addOption(OptionType.STRING, "reason", "Reason why user got a kick. User doesn't see that.", false);   
        subc.add(kick);

        //warn
        SubcommandData warn = new SubcommandData("warn", "Cmd to warn a user.");
        warn.addOption(OptionType.USER, "user", "The user to warn.", true);
        warn.addOption(OptionType.STRING, "reason", "Reason why user got a warning. User gets this message dmed.", true);  
        subc.add(warn);

        //warnings
        SubcommandData warnings = new SubcommandData("warnings", "Cmd to see warnings of users. If no user is provided all users with warnings are shown");
        warnings.addOption(OptionType.USER, "user", "Warnings of user.", false);
        warnings.addOption(OptionType.STRING, "userid", "Id of user for the case they left the server.", false);
        warnings.addOption(OptionType.BOOLEAN, "ephemeral", "True if message should be ephemeral. Default is false", false);
        subc.add(warnings);

        //rolebutton
        SubcommandData rolebutton = new SubcommandData("rolebutton", "Cmd to send a button for a role");
        rolebutton.addOption(OptionType.ROLE, "role", "Select assignable Role.", true);
        subc.add(rolebutton);

        //addrole 
        SubcommandData addrole = new SubcommandData("addrole", "Command to add a selfassignable role.");
        addrole.addOption(OptionType.ROLE, "role", "Select assignable Role.", true);
        addrole.addOption(OptionType.STRING, "emote", "Connect emote", true); //not sure if that works with emotes
        addrole.addOption(OptionType.STRING, "category", "Add role to a category", false);
        subc.add(addrole);

        //assign
        SubcommandData assign = new SubcommandData("assign", "Command to send message for roleassignment channel.");
        subc.add(assign);
        
        //editassign
        SubcommandData editassign = new SubcommandData("editassign", "Command to update role messages.");
        subc.add(editassign);

        //delrole
        SubcommandData delrole = new SubcommandData("delrole", "Command to remove a selfassignable role.");
        delrole.addOption(OptionType.ROLE, "role", "Select Role to delete from Bot.", false);
        delrole.addOption(OptionType.STRING, "roleid", "Enter Role id to delete from Bot.", false);
        subc.add(delrole);

        //roleid
        SubcommandData roleid = new SubcommandData("roleid", "Command to get all ID's of selfassignable role.");
        subc.add(roleid);

        //updaterole  
        SubcommandData updaterole = new SubcommandData("updaterole", "Command to update a selfassignable role. If optional field is left empty, it doesn't change.");
        updaterole.addOption(OptionType.STRING, "roleid", "Role id at the moment.", true);
        updaterole.addOption(OptionType.ROLE, "newrole", "New ID/role", false);
        updaterole.addOption(OptionType.STRING, "emote", "New emote", false); //not sure if that works with emotes
        updaterole.addOption(OptionType.STRING, "category", "New category", false);
        subc.add(updaterole);
        
        admin.addSubcommands(subc);
        
        return admin;
    }

    public void load(CommandDataImpl cmd, Guild eth) {
        cmd.setDefaultEnabled(false);

        String adminID = eth.upsertCommand(cmd).complete().getId();
        
        Role adminrole = eth.getRoleById(Data.ADMIN_ID);
        Role modrole = eth.getRoleById(Data.MODERATOR_ID);
        
        eth.updateCommandPrivilegesById(adminID, Arrays.asList(CommandPrivilege.enable(adminrole), CommandPrivilege.enable(modrole))).complete();
    }
    
}
