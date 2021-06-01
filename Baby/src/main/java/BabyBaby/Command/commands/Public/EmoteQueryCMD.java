package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmoteQueryCMD implements PublicCMD{

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
        return "emotestats";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        
        List<Emote> emo = ctx.getGuild().getEmotes();
        String ids = "";
        if(ctx.getArgs().size()>0 && ctx.getArgs().get(0).startsWith("a")){
            for (Emote var : emo) {
                if(var.isAnimated())
                    ids += "\"" +var.getId() + "\",";
            }
        } else {
            for (Emote var : emo) {
                if(!var.isAnimated())
                    ids += "\"" +var.getId() + "\",";
            }
        }
        ids = ids.substring(0, ids.length()-1);

        
        String first = "```.sql query select concat('<:', EmoteName, ':', DiscordEmoteId, '>'), count(*) as c from DiscordEmoteHistory join DiscordEmotes using (DiscordEmoteId) where DiscordEmoteId IN (";
        
        String seco = ") group by EmoteName order by c ```";

        int leng = first.length() + seco.length();

        while(ids.length() + leng >2000){
            String[] arrid = ids.substring(0, 2000 - leng).split(",");
            String tmp = ids.substring(0, 2000 - leng - arrid[arrid.length-1].length());
            ctx.getChannel().sendMessage(first + tmp + seco).queue();
            ids = ids.substring(tmp.length());
        }
        
        ctx.getChannel().sendMessage(first + ids + seco).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get the query cmd to get emote stats from BRH.");
    }
    
}
