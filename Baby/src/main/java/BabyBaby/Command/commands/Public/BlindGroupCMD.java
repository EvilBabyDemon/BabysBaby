package BabyBaby.Command.commands.Public;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.data.Data;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BlindGroupCMD implements PublicCMD {
    public static HashMap<Integer, ArrayList<String>> groups = new HashMap<>();
    public static HashMap<Integer, int[]> times = new HashMap<>();
    public static ScheduledExecutorService skedula = Executors.newScheduledThreadPool(20);

    @Override
    public String getName() {
        return "groupblind";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(Data.ethid))
            return;


        String cmd;

        try {
            cmd = ctx.getArgs().get(0).toLowerCase();
        } catch (Exception e) {
            cmd = "g";
        }

        String id = ctx.getAuthor().getId();

        sw: switch (cmd) {
            case "create": case "c":
                createGroup(ctx);
                ctx.getMessage().addReaction(Data.check).queue();
            break;
            case "join": case "j":
                int group;
                try {
                    group = Integer.parseInt(ctx.getArgs().get(1));
                } catch (Exception e) {
                    ctx.getChannel().sendMessage("This is not a number! Pls give a number to join the respective group!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
                    break;
                }
                for (int ids : groups.keySet()) {
                    ArrayList<String> var = groups.get(ids);
                    if(var.contains(id)){
                        ctx.getChannel().sendMessage("You are still in a group. Pls leave that one first.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
                        break sw;
                    }
                }
                
                if(!groups.containsKey(group)){
                    ctx.getChannel().sendMessage("This group does not exist!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
                    break;
                }
                groups.get(group).add(ctx.getAuthor().getId());    
            
                ctx.getMessage().addReaction(Data.check).queue();   
            break;
            case "leave": case "l":
                for (int ids : groups.keySet()) {
                ArrayList<String> var = groups.get(ids);
                    if(var.contains(id)){
                        var.remove(id);
                        break;
                    }
                }
                ctx.getMessage().addReaction(Data.check).queue();
            break;
            case "groups": case "g":            
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Groups you can join!", null);
                eb.setColor(1);

                for (Integer var : groups.keySet()) {
                    String tmp = "";
                    for (String var2 : groups.get(var)) {
                        try {
                            tmp += ctx.getGuild().getMemberById(var2).getAsMention() + " "; 
                        } catch (Exception e) {
                            System.out.println("User left guild while in Group?");
                        }
                    }
                    eb.addField("ID: " + var + ", Learntime: " + times.get(var)[0] + ", Breaktime: " + times.get(var)[1], tmp, false);
                }

                String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname() : ctx.getMember().getEffectiveName();
                eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

                ctx.getChannel().sendMessage(eb.build()).complete().delete().queueAfter(60, TimeUnit.SECONDS);;
            break;
            default:
                String pre = ctx.getMessage().getContentRaw().split(" ")[0];
                ctx.getChannel().sendMessage(getPublicHelp(pre.substring(0, pre.length() - getName().length()))).complete().delete().queueAfter(60, TimeUnit.SECONDS);;
                ctx.getMessage().addReaction(Data.check).queue();
            break;
        }
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName() +"`");
        embed.setDescription("Make a group to be blinded together!");
        embed.addField("", "This works on the principle of working/learning 45 minutes and having a break for 15 minutes. "+
        "You can set the specific time you want to learn and have break yourself and that will happen repeatedly in a cycle. "+
        "People then can join you and be blinded together. You can always unblind yourself with +" + new UnBlindCMD().getName() + " The unit is minutes.", false);

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addNormal(prefix + getName() + " " + "<create | c> " + "<time to mute> <breaktime>  [delay in minutes]")
                .addNormal(prefix + getName() + " " + "<join | j> " + "<group id>")
                .addNormal(prefix + getName() + " " + "<leave | l> " + "<group id>")
                .addNormal(prefix + getName() + " " + "[groups | g] (to see all groups)")
                .build(), false);
        
        return embed.build();
    }

    public void createGroup(CommandContext ctx){
        List<String> cmds = ctx.getArgs();
        int blind;
        int breaks;

        try {
            blind = Integer.parseInt(cmds.get(1));
            breaks = Integer.parseInt(cmds.get(2));
        } catch (Exception e) {
            ctx.getChannel().sendMessage("The command is" + getName() + " create <blind time> <break time>").complete().delete().queueAfter(30, TimeUnit.SECONDS);
            return;
        }

        if(breaks<1 || blind < 1){
            ctx.getChannel().sendMessage("Pls use numbers above 0.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
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
        times.put(id, new int[] {blind, breaks});
        ctx.getChannel().sendMessage("You joined Group : " + id + " The learning is " + blind + " the break time is " + breaks + " minutes long. You will get blinded in 5 minutes from now!").complete().delete().queueAfter(180, TimeUnit.SECONDS);
        try {
            int x = Integer.parseInt(cmds.get(3));
            if(x<0 || x> 1000)
                throw new Error("Bad Argument Exception");
            skedula.schedule(creat, x, TimeUnit.MINUTES);
        } catch (Exception e) {
            skedula.schedule(creat, 5, TimeUnit.MINUTES);
        }
    }
    
}
