package BabyBaby.Command.commands.Public;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SuggestionCMD implements PublicCMD {

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
        return "suggestion";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        File suggestions = new File("C:\\Users\\Lukas\\Desktop\\From_Old_to_NEW\\VSCODE WORKSPACE\\BabysBaby\\Baby\\src\\suggestions.txt");
        String content = "";
        
        List<String> args = ctx.getArgs();
        for (String var : args) {
            content += var + " ";
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
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), " <Suggestions>", "Suggest anything to my bot what I should add or change!");
    }
    
}
