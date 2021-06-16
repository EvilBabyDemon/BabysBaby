package BabyBaby.Command.commands.Owner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/*
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
*/
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class TestCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<String> roles = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES;");
            while ( rs.next() ) {
                roles.add(ctx.getGuild().getRoleById(rs.getString("ID")).getName());
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            ctx.getChannel().sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }
        boolean switcher = true;
        for (int i = 0; i < 25; i++) {
            buttons.add(switcher ? Button.success("role", roles.remove((int) (Math.random()*roles.size()))) : Button.danger("role", roles.remove((int) (Math.random()*roles.size()))));
            switcher = !switcher;
        }
        
        MessageAction adder = ctx.getChannel().sendMessage("Click the buttons :");
        LinkedList<ActionRow> actionRows = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            actionRows.add(ActionRow.of(buttons.remove(0), buttons.remove(0), buttons.remove(0), buttons.remove(0), buttons.remove(0)));
        }
        adder.setActionRows(actionRows);
        
        

        Message msg = adder.complete();
        Data.buttonid.add(msg.getId());
        msg.editMessage("newContent").setActionRow(Button.success("role", "yay")).completeAfter(10, TimeUnit.SECONDS);


        /*
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<Emote> allemo = new ArrayList<>(ctx.getGuild().getEmotes());
        for (int i = 0; i < 25; i++) {
            Emote emo = allemo.remove((int) (Math.random()*allemo.size()));
            Button tmp = Button.primary(emo.getId(), Emoji.fromEmote(emo));
            //tmp = tmp.withEmoji(Emoji.fromEmote(emo));
            buttons.add(tmp);
        }
        MessageAction adder = ctx.getChannel().sendMessage("Click the buttons :");
        LinkedList<ActionRow> actionRows = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            actionRows.add(ActionRow.of(buttons.remove(0), buttons.remove(0), buttons.remove(0), buttons.remove(0), buttons.remove(0)));
        }
        adder.setActionRows(actionRows);
        data.buttonid.add(adder.complete().getId());
        */
    }   

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "(whatever it is atm)", "A cmd to test things out.");
    }
    /*
    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(),                 c.getGreen(), c.getBlue());
    } 
    */
    
}
