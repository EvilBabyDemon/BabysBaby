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
        LinkedList<String> args = new LinkedList<>(ctx.getArgs());
        String subcmd = args.size() == 0 ? "" : args.getFirst();

        switch (subcmd) {
            case "add":
                for (int i = 1; i < Data.covid.length; i++) {
                    Data.covid[i - 1] = Data.covid[i];
                }
                Data.covid[Data.covid.length - 1] = Integer.parseInt(args.get(1));
                break;
            case "addall":
                for (int j = 1; j < args.size(); j++) {
                    for (int i = 1; i < Data.covid.length; i++) {
                        Data.covid[i - 1] = Data.covid[i];
                    }
                    Data.covid[Data.covid.length - 1] = Integer.parseInt(args.get(j));
                }
                break;
            case "fix":
                Data.covid[Integer.parseInt(args.get(1))] = Integer.parseInt(args.get(2));
                break;
            default:
                double avg = 0.0;
                double low = Double.MAX_VALUE;
                double high = 0.0;
                int amount = 0;

                for (int i = 0; i < Data.covid.length - 5; i++) {
                    // no data
                    if (Data.covid[i] == 0)
                        continue;

                    // x and y are each the same weekday. x being the weekday you wanna calculate.
                    // x1 being 7 days ago. Difference in days between |x2-x1| = |y2-y1|
                    // x2 = x1 / y1 * y2
                    double temp = Data.covid[Data.covid.length - 5] / ((double) Data.covid[i])
                            * Data.covid[Data.covid.length - (Data.covid.length - 5) + i];
                    avg += temp;
                    amount++;

                    if (temp > high) {
                        high = temp;
                    }
                    if (temp < low) {
                        low = temp;
                    }
                }
                avg = avg / amount;

                // the usual calculation I used
                double normal = Data.covid[Data.covid.length - 5] / ((double) Data.covid[Data.covid.length - 6])
                        * Data.covid[Data.covid.length - 1];
                ctx.getChannel().sendMessage("Guess: " + (int) normal + "\nAvg: " + (int) avg + "\nHighest: "
                        + (int) high + "\nLowest: " + (int) low).queue();
        }

        ctx.getMessage().addReaction(Data.check).queue();

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "[add <cases> | fix <array location> <cases>]",
                "Calculate covid guess.");

    }

}
