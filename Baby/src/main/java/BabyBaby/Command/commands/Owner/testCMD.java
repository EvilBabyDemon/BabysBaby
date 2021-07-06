package BabyBaby.Command.commands.Owner;



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
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu.Builder;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class TestCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        /*
        LinkedList<Member> allmem = new LinkedList<>(); 
        
        Role student = ctx.getGuild().getRoleById(Data.ethstudent);
        for (Member mem : ctx.getGuild().getMembers()) {   
            if(mem.getRoles().contains(student))
                allmem.add(mem);
        }

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            for (Member mem : allmem) {
                pstmt = c.prepareStatement("INSERT OR IGNORE INTO VERIFIED (ID) VALUES (?);");
                pstmt.setString(1, mem.getId());
                pstmt.execute();    
                pstmt.close();
            }
            
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
        ctx.getMessage().reply("Done!").complete();
        */
        /*
        String desc = "";
        EmbedBuilder eb = new EmbedBuilder();
        
        
        eb.setTitle("Pruneable Members");
        
        for (int i = 1; i < 30; i+=2) {
            desc += i + " Days: " + ctx.getGuild().retrievePrunableMemberCount(i).complete() + "\n"; 
        }
        

        eb.setDescription((desc.equals("")?"None":desc));

        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
        */
        
        //System.out.println(ctx.getChannel().retrieveMessageById(ctx.getArgs().get(0)).complete().getContentRaw());

        MessageAction msg = ctx.getChannel().sendMessage("test");

        SelectionMenu menu = SelectionMenu.create("menu:class")
            .setPlaceholder("Choose your class") // shows the placeholder indicating what this menu is for
            .setRequiredRange(1, 2) // only one can be selected
            .addOption("mage-arcane", "Arcane Mage")
            .addOption("mage-fire", "Fire Mage")
            .addOption("mage-frost", "Frost Mage")
            .build();

        Builder test = SelectionMenu.create("test");
        test.addOption("Test", "tester", "This is the Description", Emoji.fromEmote(ctx.getGuild().getEmotes().get(0)));
        
        msg.setActionRows(ActionRow.of(menu), ActionRow.of(test.build()));
        msg.queue();
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
