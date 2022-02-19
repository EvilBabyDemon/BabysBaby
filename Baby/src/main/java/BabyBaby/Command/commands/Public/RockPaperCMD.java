package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class RockPaperCMD implements IPublicCMD {

    @Override
    public String getName() {
        return "rock";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        // TODO Put Bad User somewhere else (not hardcoded)

        String[] baduser = {"304587987956531201"};

        List<String> args = ctx.getArgs();

        MessageChannel channel = ctx.getChannel();    

        // no subcommand -> allRoles
        if (args == null || args.isEmpty() || args.size() > 1) {
            channel.sendMessage("You have to type" + " " + "rock <rock/paper/scissors>").queue();
            return;
        }

        String content = args.get(0);

        String[] decision = { "rock", "paper", "scissors" };
        
        boolean goodString = false;
        for (int i = 0; i < decision.length; i++) {
            if (decision[i].equals(content)) {
                goodString = true;
            }
        }
        String result = "";

        if (goodString) {

            boolean badUser = false;
            String user = ctx.getAuthor().getId();

            for (int i = 0; i < baduser.length; i++) {
                if (baduser[i].equals(user))
                    badUser = true;
            }

            if (badUser) {
                if (content.equals(decision[0])) {
                    result = decision[1];
                } else if (content.equals(decision[1])) {
                    result = decision[2];
                } else {
                    result = decision[0];
                }
            } else {
                result = decision[(int) (Math.random() * 3)];
            }

            if (content.equals(result)) {
                result = "The bot has chosen: " + result + " Its a draw...";
            } else if (content.equals(decision[0]) && result.equals(decision[1])
                    || content.equals(decision[1]) && result.equals(decision[2])
                    || content.equals(decision[2]) && result.equals(decision[0])) {
                result = "The bot has chosen: " + result + "\n The bot has won against " + content;
            } else {
                result = "The bot has chosen: " + result + "\n The bot has lost against " + content;
            }

            channel.sendMessage(result).queue();
        } else {
            channel.sendMessage("You have to type rock, paper or scissors correctly!").queue();
            return;
        }

        ctx.getMessage().addReaction(Data.check).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<rock/paper/scissors>", "Play a game of Rock, Paper or Scissors against this Bot.");
    }

    
}
