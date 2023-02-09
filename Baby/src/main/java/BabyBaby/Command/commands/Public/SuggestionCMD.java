package BabyBaby.Command.commands.Public;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class SuggestionCMD implements IPublicCMD {

    @Override
    public boolean getWhiteListBool() {
        return true;
    }

    @Override
    public String getName() {
        return "suggestion";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        
        File suggestions = new File(Data.SUGGESTION);
        String content = "";

        List<String> args = ctx.getArgs();
        for (String arg : args) {
            content += arg + " ";
        }

        content = ctx.getAuthor().getAsTag() + " " + content;

        ctx.getMessage().delete().queue();
        try {
            FileWriter fr = new FileWriter(suggestions, true);
            fr.write(content + "\n");
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        channel.sendMessage("Thx for the suggestion!").queue();

        ctx.getJDA().getUserById(Data.myselfID).openPrivateChannel().complete().sendMessage(content).queue();

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), " <Suggestions>",
                "Suggest anything to my bot what I should add or change!");
    }

}
