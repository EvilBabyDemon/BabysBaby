package BabyBaby.Command.commands.Public;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class UsageCMD implements PublicCMD {

    @Override
    public String getName() {
        return "usage";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Usage of this bot.");
        eb.setDescription("Amount of Members using the bot: " + Data.users.size());
        eb.setTimestamp(Data.startUp);
        eb.setFooter("Start up time");

        Data.cmdUses.keySet().stream().map(cmdname -> cmdname).sorted().collect(Collectors.toList());
        ArrayList<Entry<String, Integer>> sortList = new ArrayList<>(Data.cmdUses.entrySet());

        Collections.sort(sortList, new Comparator<Entry<String, Integer>>(){  
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){  
                return o2.getValue().compareTo(o1.getValue()); 
            }
        });
        
        ArrayList<String> top10Array = new ArrayList<>(); 
        for (int i = 0; i < 10 && i < sortList.size(); i++) {
            top10Array.add("`" + sortList.get(i).getKey() + "` : " + sortList.get(i).getValue());
        }

        String content = String.join("\n", top10Array);
        eb.setColor(Color.GREEN);
        eb.addField("Most used Commands", content, true);
        
        eb.addField("Button and Slash Commands", "Count: " + Data.slashAndButton, true);
        
        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
        ctx.getMessage().addReaction(Data.check).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get some stats about the usage of this bot.");
    }
    
}
