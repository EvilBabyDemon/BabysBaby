package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmoteQueryCMD implements IPublicCMD {

    @Override
    public String getName() {
        return "emotestats";
    }

    @Override
    public void handlePublic(CommandContext ctx) {

        List<Emote> emo = ctx.getGuild().getEmotes();
        String ids = "";
        if (ctx.getArgs().size() > 0 && ctx.getArgs().get(0).startsWith("a")) {
            for (Emote emote : emo) {
                if (emote.isAnimated())
                    ids += "\"" + emote.getId() + "\",";
            }
        } else {
            for (Emote emote : emo) {
                if (!emote.isAnimated())
                    ids += "\"" + emote.getId() + "\",";
            }
        }
        ids = ids.substring(0, ids.length() - 1);

        String first = "```.sql query select concat('<:', EmoteName, ':', DiscordEmoteId, '>'), count(*) as c from DiscordEmoteHistory join DiscordEmotes using (DiscordEmoteId) where DiscordEmoteId IN (";

        String seco = ") and DateTimePosted  >= (DATE(NOW()) - INTERVAL 20 DAY) group by EmoteName order by c ```";

        int leng = first.length() + seco.length();

        while (ids.length() + leng > 2000) {
            String[] arrid = ids.substring(0, 2000 - leng).split(",");
            String tmp = ids.substring(0, 2000 - leng - arrid[arrid.length - 1].length() - 1);
            ctx.getChannel().sendMessage(first + tmp + seco).queue();
            ids = ids.substring(tmp.length() + 1);
        }

        ctx.getChannel().sendMessage(first + ids + seco).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get the query cmd to get emote stats from BRH.");
    }

}
