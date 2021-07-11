package BabyBaby;

import BabyBaby.Command.*;
import BabyBaby.Command.commands.Admin.*;
import BabyBaby.Command.commands.Owner.*;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

public class CmdHandler {

    private final List<PublicCMD> publicCommands = new ArrayList<>();
    private final List<AdminCMD> adminCommands = new ArrayList<>();
    private final List<OwnerCMD> ownerCommands = new ArrayList<>();

    public static HashSet<String> offCMD = new HashSet<>();

    //private final List<Command> allCommands = new ArrayList<>();

    public CmdHandler(JDA bot) throws IOException {

        

        // adding commands visible to @everyone
        addPublicCommand(new PingCMD());
        addPublicCommand(new AllRolesCMD());
        addPublicCommand(new DecryptCMD());
        addPublicCommand(new DuckyCMD());

        addPublicCommand(new EncryptCMD());
        addPublicCommand(new GetRoleCMD());

        addPublicCommand(new LearningCMD());
        addPublicCommand(new MuteCMD());
        addPublicCommand(new NoKeyCMD());
        addPublicCommand(new PolyCMD());
        addPublicCommand(new ReminderCMD());
        addPublicCommand(new RockPaperCMD());
        //addPublicCommand(new RoleMuteCMD());
        addPublicCommand(new SieveCMD());
        addPublicCommand(new SourceCMD());
        addPublicCommand(new SuggestionCMD());
        addPublicCommand(new UnmuteMeCMD());
        addPublicCommand(new PollCMD());
        addPublicCommand(new PlaceGifCMD());
        addPublicCommand(new BotsOnlineCMD());
        addPublicCommand(new BlindCMD());
        addPublicCommand(new UnBlindCMD());
        addPublicCommand(new FlashedCMD());
        addPublicCommand(new BlindForceCMD());
        addPublicCommand(new TillBlindCMD());
        addPublicCommand(new EmoteQueryCMD());
        addPublicCommand(new BlindGroupCMD());
        addPublicCommand(new BlindStatsCMD());
        addPublicCommand(new UsageCMD());

        // adding commands visible to @admin
        addAdminCommand(new AddRoleCMD());
        addAdminCommand(new DelRoleCMD());
        addAdminCommand(new RoleIdCMD());
        addAdminCommand(new UpdateRoleCMD());
        addAdminCommand(new WhoisCMD());
        addAdminCommand(new RoleAssignCMD());
        addAdminCommand(new WarnCMD());
        addAdminCommand(new GetWarnedCMD());
        addAdminCommand(new GetWarningsFromUserCMD());
        addAdminCommand(new MutePersonCMD());
        addAdminCommand(new UnmutePersonCMD());
        addAdminCommand(new EditAssignCMD());
        addAdminCommand(new KickCMD());
        addAdminCommand(new BanCMD());
        //TODO Fix that cmd
        //addAdminCommand(new AdminMuteBlindCMD());


        // adding commands visible to owner
        addOwnerCommand(new AdminHelpCMD());
        addOwnerCommand(new cleartable());
        addOwnerCommand(new ConvertPlace());
        addOwnerCommand(new PlaceDraw());
        //addOwnerCommand(new operationcheck());
        //addOwnerCommand(new operationsecret());
        addOwnerCommand(new PlebHelpCMD());
        addOwnerCommand(new SayCMD());
        //addOwnerCommand(new SendUserCMD());
        addOwnerCommand(new SetButtonCMD());
        addOwnerCommand(new SetPrefixCMD());
        addOwnerCommand(new PlaceSorter());
        //addOwnerCommand(new tick());
        //addOwnerCommand(new ucheck());
        addOwnerCommand(new WhereamiCMD());
        addOwnerCommand(new TestCMD());
        //addOwnerCommand(new toMultipixelCMD());
        addOwnerCommand(new stopdraw());
        addOwnerCommand(new SayMultiCMD());
        //addOwnerCommand(new BigSieveCMD());
        addOwnerCommand(new BubbleSortCMD());
        //addOwnerCommand(new RoleChangeCMD());
        addOwnerCommand(new TurnCMDsOff());
        addOwnerCommand(new ChangeLogCMD());
        addOwnerCommand(new SpeedCMD());
        
        
        
        addPublicCommand(new HelpCMD(this));

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


    public void handle(GuildMessageReceivedEvent event, String prefix) {

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");
        
        List<String> args = Arrays.asList(split).subList(1, split.length);
        
        CommandContext ctx = new CommandContext(event, args);

        int permissionLevel = ctx.getPermissionLevel();

        String cmdName = split[0].toLowerCase();
        switch (permissionLevel) {
            case 0:
                PublicCMD publicCommand = searchPublicCommand(cmdName);
                if (publicCommand != null && !offCMD.contains(publicCommand.getName())) {
                    runCMD(publicCommand, ctx, permissionLevel);
                }
                break;
            case 1:
                AdminCMD adminCommand = searchAdminCommand(cmdName);
                if (adminCommand != null && !offCMD.contains(adminCommand.getName())) {
                    runCMD(adminCommand, ctx, permissionLevel);
                }
                break;
            case 2:
                OwnerCMD ownerCommand = searchOwnerCommand(cmdName);
                if (ownerCommand != null && !offCMD.contains(ownerCommand.getName())) {
                    runCMD(ownerCommand, ctx, permissionLevel);
                }
                break;
        }
    }


    public void privhandle(@NotNull PrivateMessageReceivedEvent event, String prefix) {

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");
        
        List<String> args = Arrays.asList(split).subList(1, split.length);
        
        String cmdName = split[0].toLowerCase();
        
        UnmuteMeCMD cmd = new UnmuteMeCMD();
        UnBlindCMD cmd1 = new UnBlindCMD();
        TillBlindCMD cmd2 = new TillBlindCMD();

        if(cmdName.equals(cmd.getName())){
            cmd.privhandle(event.getAuthor(), args);
        } else if(cmdName.equals(cmd1.getName())){
            cmd1.privhandle(event.getAuthor(), args);
        } else if(cmdName.equals(cmd2.getName())){
            cmd2.privhandle(event.getAuthor(), args, event.getJDA());
        }
        
    }

    public OwnerCMD searchOwnerCommand(String search) {
        for (OwnerCMD cmd : ownerCommands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search)) {
                return cmd;
            }
        }
        return searchAdminCommand(search);
    }

    public AdminCMD searchAdminCommand(String search) {

        for (AdminCMD cmd : adminCommands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search)) {
                return cmd;
            }
        }
        return searchPublicCommand(search);
    }

    public PublicCMD searchPublicCommand(String search) {
        for (PublicCMD cmd : publicCommands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search)) {
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


    private void runCMD (Command cmd, CommandContext ctx, int permissionLevel){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch(permissionLevel){
                        case 0:
                            ((PublicCMD) cmd).handlePublic(ctx);
                            break;
                        case 1:
                            ((AdminCMD) cmd).handleAdmin(ctx);
                            break;
                        case 2:
                            long time = System.currentTimeMillis();
                            ((OwnerCMD) cmd).handleOwner(ctx);
                            System.out.println(System.currentTimeMillis()-time);
                            break;
                    }

                } catch(Exception e){
                    ctx.getMessage().addReaction(Data.xmark).queue();
                    System.out.println(ctx.getMessage().getContentRaw());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Data.cmdUses.putIfAbsent(cmd.getName(), 0);
        Data.cmdUses.computeIfPresent(cmd.getName(), (name, x) -> ++x);
        Data.users.add(ctx.getAuthor().getId());
    }

}
