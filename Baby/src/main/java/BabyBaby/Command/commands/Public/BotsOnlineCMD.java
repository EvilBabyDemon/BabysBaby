package BabyBaby.Command.commands.Public;

import java.util.ArrayList;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BotsOnlineCMD implements PublicCMD{

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
        return "online";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        ArrayList<Member> online = new ArrayList<>();
        ArrayList<Member> offline = new ArrayList<>();        
        for (Member var : ctx.getGuild().getMembers()) {
            if(var.getUser().isBot()){
                OnlineStatus stat = var.getOnlineStatus();
                if(stat.equals(OnlineStatus.OFFLINE)){
                    offline.add(var);
                } else{
                    online.add(var);
                }
            }        
        }

        String on = "";
        String off = "";
        String pingstr = "";

        for (Member var : online) {
            on += var.getAsMention() + "\n";
            pingstr += var.getAsMention() + " ";
        }

        for (Member var : offline) {
            off += var.getAsMention() + "\n";
            pingstr += var.getAsMention() + " ";
        }

        Message ping =  ctx.getChannel().sendMessage("wait").complete();
        ping.editMessage(pingstr).complete();


        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Bots on this server:");
        eb.addField("Online Bots: " + online.size(), on, true);
        eb.addField("Offline Bots: " + offline.size(), off, true);
        
        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

        ping.editMessage(eb.build()).complete();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all bots and see if they are online or not");
    }
    
}
