package BabyBaby;

import BabyBaby.Command.*;
import BabyBaby.Command.commands.Owner.*;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CmdHandler {

    private final List<IPublicCMD> publicCommands = new ArrayList<>();
    private final List<IAdminCMD> adminCommands = new ArrayList<>();
    private final List<IOwnerCMD> ownerCommands = new ArrayList<>();

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

        addPublicCommand(new NoKeyCMD());
        addPublicCommand(new PolyCMD());
        //addPublicCommand(new ReminderCMD());
        addPublicCommand(new RockPaperCMD());
        //addPublicCommand(new RoleMuteCMD());
        addPublicCommand(new SieveCMD());
        addPublicCommand(new SourceCMD());
        addPublicCommand(new SuggestionCMD());
        //addPublicCommand(new PollCMD());
        addPublicCommand(new PlaceGifCMD());
        addPublicCommand(new BotsOnlineCMD());
        //addPublicCommand(new BlindCMD());
        addPublicCommand(new UnBlindCMD());
        addPublicCommand(new FlashedCMD());
        //addPublicCommand(new BlindForceCMD());
        addPublicCommand(new TillBlindCMD());
        
        //at the moment completely useless
        //addPublicCommand(new EmoteQueryCMD());
        addPublicCommand(new UsageCMD());

        // adding commands visible to @admin
        /* Everything slash now
        addAdminCommand(new AddRoleCMD());
        addAdminCommand(new DelRoleCMD());
        addAdminCommand(new RoleIdCMD());
        addAdminCommand(new UpdateRoleCMD());
        addAdminCommand(new WhoisCMD());
        addAdminCommand(new RoleAssignCMD());
        addAdminCommand(new WarnCMD());
        addAdminCommand(new GetWarnedCMD());
        addAdminCommand(new GetWarningsFromUserCMD());
        addAdminCommand(new EditAssignCMD());
        addAdminCommand(new KickCMD());
        addAdminCommand(new BanCMD());
        addAdminCommand(new NewRoleCMD());
        */
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
        //addOwnerCommand(new SetButtonCMD());
        addOwnerCommand(new SetPrefixCMD());
        addOwnerCommand(new PlaceSorter());
        //addOwnerCommand(new tick());
        //addOwnerCommand(new ucheck());
        addOwnerCommand(new WhereamiCMD());
        addOwnerCommand(new TestCMD());
        //addOwnerCommand(new toMultipixelCMD());
        addOwnerCommand(new SayMultiCMD());
        //addOwnerCommand(new BigSieveCMD());
        addOwnerCommand(new BubbleSortCMD());
        //addOwnerCommand(new RoleChangeCMD());
        addOwnerCommand(new TurnCMDsOff());
        addOwnerCommand(new ChangeLogCMD());
        addOwnerCommand(new SpeedCMD());
        addOwnerCommand(new CovidGuesserCMD());
        
        
        
        addPublicCommand(new HelpCMD(this));

    }

    private void addPublicCommand(IPublicCMD cmd) {
        //addAdminCommand(cmd); // admins can use @everyone commands as well

        boolean nameFound = this.publicCommands.stream().anyMatch(
                (it) -> it.getName().equalsIgnoreCase(cmd.getName())
        );

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present in Public");
        }

        publicCommands.add(cmd);
    }

    public void addAdminCommand(IAdminCMD cmd) {
        //addOwnerCommand(cmd); // owner can use @admin commands as well

        boolean nameFound = this.adminCommands.stream().anyMatch(
                (it) -> it.getName().equalsIgnoreCase(cmd.getName())
        );

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present in Admin");
        }

        adminCommands.add(cmd);
    }

    public void addOwnerCommand(IOwnerCMD cmd) {
        boolean nameFound = this.ownerCommands.stream().anyMatch(
                (it) -> it.getName().equalsIgnoreCase(cmd.getName())
        );

        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already in Owner");
        }

        ownerCommands.add(cmd);
    }


    public void handle(MessageReceivedEvent event, String prefix) {

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");
        
        List<String> args = Arrays.asList(split).subList(1, split.length);
        
        CommandContext ctx = new CommandContext(event, args);

        int permissionLevel = ctx.getPermissionLevel();

        String cmdName = split[0].toLowerCase();
        switch (permissionLevel) {
            case 0:
                IPublicCMD publicCommand = searchPublicCommand(cmdName);
                if (publicCommand != null && !offCMD.contains(publicCommand.getName())) {
                    runCMD(publicCommand, ctx, permissionLevel);
                }
                break;
            case 1:
                IAdminCMD adminCommand = searchAdminCommand(cmdName);
                if (adminCommand != null && !offCMD.contains(adminCommand.getName())) {
                    runCMD(adminCommand, ctx, permissionLevel);
                }
                break;
            case 2:
                IOwnerCMD ownerCommand = searchOwnerCommand(cmdName);
                if (ownerCommand != null && !offCMD.contains(ownerCommand.getName())) {
                    runCMD(ownerCommand, ctx, permissionLevel);
                }
                break;
        }
    }

    public IOwnerCMD searchOwnerCommand(String search) {
        for (IOwnerCMD cmd : ownerCommands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search)) {
                return cmd;
            }
        }
        return searchAdminCommand(search);
    }

    public IAdminCMD searchAdminCommand(String search) {

        for (IAdminCMD cmd : adminCommands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search)) {
                return cmd;
            }
        }
        return searchPublicCommand(search);
    }

    public IPublicCMD searchPublicCommand(String search) {
        for (IPublicCMD cmd : publicCommands) {
            if (cmd.getName().equals(search) || cmd.getAliases().contains(search)) {
                return cmd;
            }
        }
        return null;
    }
 


    public List<IPublicCMD> getPublicCommands() {
        return publicCommands;
    }

    public List<IAdminCMD> getAdminCommands() {
        return adminCommands;
    }

    public List<IOwnerCMD> getOwnerCommands() {
        return ownerCommands;
    }


    private void runCMD (ICommand cmd, CommandContext ctx, int permissionLevel){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //check if blinded and then just ignore cmd
                if(ctx.getGuild().getId().equals(Data.ETH_ID) && ctx.getMember().getRoles().contains(ctx.getGuild().getRoleById(Data.BLIND_ID))){
                    ctx.getAuthor().openPrivateChannel().complete().sendMessage("Unblind yourself and don't try to cheat!").queue();
                    return;
                }
                
                try {
                    switch(permissionLevel){
                        case 0:
                            if(ctx.getGuild() == null || !ctx.getGuild().getId().equals(Data.ETH_ID) || ctx.getChannel().getParentCategory() == null 
                            || ctx.getChannel().getParentCategoryId().equals(Data.BOTS_BATTROYAL) || ((IPublicCMD) cmd).getWhiteListBool()){
                                ((IPublicCMD) cmd).handlePublic(ctx);
                            } else{
                                ctx.getChannel().sendMessage("Please use the dedicated bot channels for this command.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                            }
                            break;
                        case 1:
                            ((IAdminCMD) cmd).handleAdmin(ctx);
                            break;
                        case 2:
                            long time = System.currentTimeMillis();
                            ((IOwnerCMD) cmd).handleOwner(ctx);
                            System.out.println(System.currentTimeMillis()-time);
                            break;
                    }

                } catch(Exception e){
                    System.out.println(ctx.getMessage().getContentRaw());
                    e.printStackTrace();
                    ctx.getMessage().addReaction(Data.xmark).queue();
                }
            }
        });
        thread.start();
        Data.cmdUses.putIfAbsent(cmd.getName(), 0);
        Data.cmdUses.computeIfPresent(cmd.getName(), (name, x) -> ++x);
        Data.users.add(ctx.getAuthor().getId());
    }

}
