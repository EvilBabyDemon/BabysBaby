package BabyBaby.Command.commands.Owner;

import java.util.LinkedList;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CovidGuesserCMD implements IOwnerCMD {

    @Override
    public String getName() {
        return "covid";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        LinkedList<String> args =  new LinkedList<>(ctx.getArgs());


        switch (args.getFirst()) {
            case "add":
                for (int i = 1; i < Data.covid.length; i++) {
                    Data.covid[i-1] = Data.covid[i];
                }
                Data.covid[Data.covid.length-1] = Integer.parseInt(args.get(1));
                break;
            case "fix":
                Data.covid[Integer.parseInt(args.get(1))] = Integer.parseInt(args.get(2));
                break;
            default:
                double avg = 0.0;
                double low = Double.MAX_VALUE;
                double high = 0.0;
                
                for (int i = 0; i < Data.covid.length-6; i++) {
                    // no data
                    if(Data.covid[i] == 0)
                        continue;

                    // x and y are each the same weekday. x being the weekday you wanna calculate. x1 being 7 days ago. Difference in days between |x2-x1| = |y2-y1|  
                    // x2 = x1 / y1 * y2 
                    double temp = Data.covid[Data.covid.length-6] / ((double) Data.covid[i]) * Data.covid[Data.covid.length-(Data.covid.length-7)-i];
                    avg += temp;
                    if (temp > high) {
                        high = temp;
                    }
                    if (temp < low) {
                        low = temp;
                    }
                } 
                avg = avg / Data.covid.length-6;

                // the usual calculation I used
                double normal = Data.covid[Data.covid.length-6] / ((double) Data.covid[Data.covid.length-7]) * Data.covid[Data.covid.length-1];   
                ctx.getChannel().sendMessage("Guess: " + normal + "\nAvg: " + avg + "\nHighest: " + high + "Lowest: " + low).queue();
        }
        
        ctx.getMessage().addReaction(Data.check).queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "[add <cases> | fix <array location> <cases>]", "Calculate covid guess.");
        
    }
    
}
