package BabyBaby;

import BabyBaby.Command.*;
import BabyBaby.Command.commands.Admin.GetWarningsFromUser;
import BabyBaby.Command.commands.Admin.MutePersonCMD;
import BabyBaby.Command.commands.Admin.RoleAssignCMD;
import BabyBaby.Command.commands.Admin.UnmutePersonCMD;
import BabyBaby.Command.commands.Admin.addrole;
import BabyBaby.Command.commands.Admin.delrole;
import BabyBaby.Command.commands.Admin.getWarned;
import BabyBaby.Command.commands.Admin.removeRoles;
import BabyBaby.Command.commands.Admin.roleid;
import BabyBaby.Command.commands.Admin.updaterole;
import BabyBaby.Command.commands.Admin.warnCMD;
import BabyBaby.Command.commands.Admin.whois;
import BabyBaby.Command.commands.Owner.AdminHelpCMD;
import BabyBaby.Command.commands.Owner.BigSiebCMD;
import BabyBaby.Command.commands.Owner.PlaceSorter;
import BabyBaby.Command.commands.Owner.PlebHelpCMD;
import BabyBaby.Command.commands.Owner.SayCMD;
import BabyBaby.Command.commands.Owner.SayMultiCMD;
import BabyBaby.Command.commands.Owner.SetButtonCMD;
import BabyBaby.Command.commands.Owner.cleartable;
import BabyBaby.Command.commands.Owner.convert;
import BabyBaby.Command.commands.Owner.draw;
import BabyBaby.Command.commands.Owner.operationcheck;
import BabyBaby.Command.commands.Owner.operationsecret;
import BabyBaby.Command.commands.Owner.sendollie;
import BabyBaby.Command.commands.Owner.setPrefix;
import BabyBaby.Command.commands.Owner.stopdraw;
import BabyBaby.Command.commands.Owner.testCMD;
import BabyBaby.Command.commands.Owner.tick;
import BabyBaby.Command.commands.Owner.toMultipixelCMD;
import BabyBaby.Command.commands.Owner.ucheck;
import BabyBaby.Command.commands.Owner.whereami;
import BabyBaby.Command.commands.Public.DecryptCMD;
import BabyBaby.Command.commands.Public.DuckyCMD;
import BabyBaby.Command.commands.Public.EncryptCMD;
import BabyBaby.Command.commands.Public.HelpCMD;
import BabyBaby.Command.commands.Public.LearningCMD;
import BabyBaby.Command.commands.Public.MuteCMD;
import BabyBaby.Command.commands.Public.NoKeyCMD;
import BabyBaby.Command.commands.Public.PingCMD;
import BabyBaby.Command.commands.Public.PollCMD;
import BabyBaby.Command.commands.Public.PolyCMD;
import BabyBaby.Command.commands.Public.ReminderCMD;
import BabyBaby.Command.commands.Public.RockPaperCMD;
import BabyBaby.Command.commands.Public.RoleMuteCMD;
import BabyBaby.Command.commands.Public.SuggestionCMD;
import BabyBaby.Command.commands.Public.allroles;
import BabyBaby.Command.commands.Public.getrole;
import BabyBaby.Command.commands.Public.sieb;
import BabyBaby.Command.commands.Public.source;
import BabyBaby.Command.commands.Public.UnmuteMeCMD;
import BabyBaby.data.data;
//import BabyBaby.Command.commands.admin.SetPrefixCommand;
//import BabyBaby.Command.commands.nonAdmin.*;
//import BabyBaby.Command.commands.owner.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

public class CmdHandler {

    private final List<PublicCMD> publicCommands = new ArrayList<>();
    private final List<AdminCMD> adminCommands = new ArrayList<>();
    private final List<OwnerCMD> ownerCommands = new ArrayList<>();

    //private final List<Command> allCommands = new ArrayList<>();

    public CmdHandler(JDA bot) throws IOException {

        // adding commands visible to @everyone
        addPublicCommand(new PingCMD());
        addPublicCommand(new allroles());
        addPublicCommand(new DecryptCMD());
        addPublicCommand(new DuckyCMD());

        addPublicCommand(new EncryptCMD());
        addPublicCommand(new getrole());

        addPublicCommand(new LearningCMD());
        addPublicCommand(new MuteCMD());
        addPublicCommand(new NoKeyCMD());
        addPublicCommand(new PolyCMD());
        addPublicCommand(new ReminderCMD());
        addPublicCommand(new RockPaperCMD());
        addPublicCommand(new RoleMuteCMD());
        addPublicCommand(new sieb());
        addPublicCommand(new source());
        addPublicCommand(new SuggestionCMD());
        addPublicCommand(new UnmuteMeCMD());
        addPublicCommand(new PollCMD());
        //addPublicCommand(new WallpaperCMD());




        // adding commands visible to @admin
        addAdminCommand(new addrole());
        addAdminCommand(new delrole());
        addAdminCommand(new removeRoles());
        addAdminCommand(new roleid());
        addAdminCommand(new updaterole());
        addAdminCommand(new whois());
        addAdminCommand(new RoleAssignCMD());
        addAdminCommand(new warnCMD());
        addAdminCommand(new getWarned());
        addAdminCommand(new GetWarningsFromUser());
        addAdminCommand(new MutePersonCMD());
        addAdminCommand(new UnmutePersonCMD());
        

        // adding commands visible to owner
        //addOwnerCommand(new TestCommand());
        addOwnerCommand(new AdminHelpCMD());
        addOwnerCommand(new cleartable());
        addOwnerCommand(new convert());
        addOwnerCommand(new draw());
        addOwnerCommand(new operationcheck());
        addOwnerCommand(new operationsecret());
        addOwnerCommand(new PlebHelpCMD());
        //addOwnerCommand(new RoleCMD());
        addOwnerCommand(new SayCMD());
        addOwnerCommand(new sendollie());
        addOwnerCommand(new SetButtonCMD());
        addOwnerCommand(new setPrefix());
        addOwnerCommand(new PlaceSorter());
        addOwnerCommand(new tick());
        addOwnerCommand(new ucheck());
        addOwnerCommand(new whereami());
        addOwnerCommand(new testCMD());
        addOwnerCommand(new toMultipixelCMD());
        addOwnerCommand(new stopdraw());
        addOwnerCommand(new SayMultiCMD());
        addOwnerCommand(new BigSiebCMD());
        //addOwnerCommand(new );
        addPublicCommand(new HelpCMD(this));
        //addPublicCommand(new EasterEggCMD());

    }

    private void addPublicCommand(PublicCMD cmd) {
        //addAdminCommand(cmd); // admins can use @everyone commands as well

        boolean nameFound = this.publicCommands.stream().anyMatch(
                (it) -> it.getName().equalsIgnoreCase(cmd.getName())
        );

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present in Public");
        }

        publicCommands.add(cmd);
    }

    public void addAdminCommand(AdminCMD cmd) {
        //addOwnerCommand(cmd); // owner can use @admin commands as well

        boolean nameFound = this.adminCommands.stream().anyMatch(
                (it) -> it.getName().equalsIgnoreCase(cmd.getName())
        );

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present in Admin");
        }

        adminCommands.add(cmd);
    }

    public void addOwnerCommand(OwnerCMD cmd) {
        boolean nameFound = this.ownerCommands.stream().anyMatch(
                (it) -> it.getName().equalsIgnoreCase(cmd.getName())
        );

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already in Owner");
        }

        ownerCommands.add(cmd);
    }


    void handle(GuildMessageReceivedEvent event, String prefix) {

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");
        
        List<String> args = Arrays.asList(split).subList(1, split.length);
        
        CommandContext ctx = new CommandContext(event, args);

        int permissionLevel = ctx.getPermissionLevel();

        String cmdName = split[0].toLowerCase();
        try { 
            switch (permissionLevel) {
                case 0:
                    PublicCMD publicCommand = searchPublicCommand(cmdName);
                    if (publicCommand != null) {
                        publicCommand.handlePublic(ctx);
                    }
                    break;
                case 1:
                    AdminCMD adminCommand = searchAdminCommand(cmdName);
                    if (adminCommand != null) {
                        adminCommand.handleAdmin(ctx);
                    }
                    break;
                case 2:
                    OwnerCMD ownerCommand = searchOwnerCommand(cmdName);
                    if (ownerCommand != null) {
                        ownerCommand.handleOwner(ctx);
                    }
                    break;
            }
        } catch(Exception e){
            ctx.getMessage().addReaction(data.xmark).queue();
            e.printStackTrace();
        }
    }


    public void privhandle(@NotNull PrivateMessageReceivedEvent event, String prefix) {

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");
        
        List<String> args = Arrays.asList(split).subList(1, split.length);
        
        String cmdName = split[0].toLowerCase();
        UnmuteMeCMD cmd = new UnmuteMeCMD();
        if(cmdName.equals(cmd.getName())){
            cmd.privhandle(event.getAuthor(), args);
        }
        
    }

    public OwnerCMD searchOwnerCommand(String search) {
        String searchLower = search.toLowerCase();

        for (OwnerCMD cmd : ownerCommands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        for (AdminCMD cmd : adminCommands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        for (PublicCMD cmd : publicCommands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        return null;
    }

    public AdminCMD searchAdminCommand(String search) {
        String searchLower = search.toLowerCase();

        for (AdminCMD cmd : adminCommands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        for (PublicCMD cmd : publicCommands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        return null;
    }

    public PublicCMD searchPublicCommand(String search) {
        String searchLower = search.toLowerCase();

        for (PublicCMD cmd : publicCommands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }

        return null;
    }



    public List<PublicCMD> getPublicCommands() {
        return publicCommands;
    }

    public List<AdminCMD> getAdminCommands() {
        return adminCommands;
    }

    public List<OwnerCMD> getOwnerCommands() {
        return ownerCommands;
    }
}
