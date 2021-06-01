package BabyBaby.Command.commands.Public;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BlindGroupCMD implements PublicCMD {
    public static HashMap<Integer, ArrayList<String>> groups = new HashMap<>();

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
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
        return "groupblind";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(data.ethid))
            return;


        String cmd;

        try {
            cmd = ctx.getArgs().get(0);
        } catch (Exception e) {
            cmd = "help";
        }

        String id = ctx.getAuthor().getId();

        sw: switch (cmd) {
            case "create": case "c":
                createGroup(ctx);
                ctx.getMessage().addReaction(data.check).queue();
                break;
            case "join": case "j":
                int group;
                try {
                    group = Integer.parseInt(ctx.getArgs().get(1));
                } catch (Exception e) {
                    ctx.getChannel().sendMessage("This is not a number! Pls give a number to join the respective group!").queue();
                    break;
                }
                for (int ids : groups.keySet()) {
                    ArrayList<String> var = groups.get(ids);
                    if(var.contains(id)){
                        ctx.getChannel().sendMessage("You are still in a group. Pls leave that one first.").queue();
                        break sw;
                    }
                }
                
                if(!groups.containsKey(group)){
                    ctx.getChannel().sendMessage("This is group does not exist!").queue();
                    break;
                }
                groups.get(group).add(ctx.getAuthor().getId());    
            
                ctx.getMessage().addReaction(data.check).queue();   
                break;
            case "leave": case "l":
                for (int ids : groups.keySet()) {
                ArrayList<String> var = groups.get(ids);
                    if(var.contains(id)){
                        var.remove(id);
                        break;
                    }
                }
                ctx.getMessage().addReaction(data.check).queue();
                break;
            case "help":
                String pre = ctx.getMessage().getContentRaw().split(" ")[0];
                getPublicHelp(pre.substring(0, pre.length() - getName().length()));
                ctx.getMessage().addReaction(data.check).queue();
                break;
            default:
                String tmp = "";

                for (Integer var : groups.keySet()) {
                    tmp += "" + var;
                    for (String var2 : groups.get(var)) {
                        tmp += " " + var2; 
                    }
                    tmp += "\n";
                }
                ctx.getChannel().sendMessage((tmp == "") ? "no content" : tmp).queue();    
                
                break;
        }
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time to mute> <breaktime>", "Start a group to get together blinded");
    }

    public void createGroup(CommandContext ctx){
        List<String> cmds = ctx.getArgs();
        int blind;
        int breaks;

        try {
            blind = Integer.parseInt(cmds.get(1));
            breaks = Integer.parseInt(cmds.get(2));
        } catch (Exception e) {
            ctx.getChannel().sendMessage("The command is" + getName() + " create <blind time> <break time>").queue();
            return;
        }
        int id = 0;
        for (int i = 0; i < 1000; i++) {
            if(groups.containsKey(i)){
                continue;
            }
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(ctx.getMember().getId());
            id = i;
            groups.put(id, tmp);
            break;
        }
        
        GroupBlindEx creat = new GroupBlindEx(id, ctx.getGuild(), true, breaks, blind, 0);
        
        ScheduledExecutorService startgroup = Executors.newScheduledThreadPool(100);
        ctx.getChannel().sendMessage("You joined Group : " + id + " The breaktime is " + breaks + " the learning time is " + blind + " minutes long. You will get blinded in 5 minutes from now!").complete();
        startgroup.schedule(creat, 5, TimeUnit.SECONDS);
    }
    
}
