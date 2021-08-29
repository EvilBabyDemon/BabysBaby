package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BlindStatsCMD implements PublicCMD {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public void handlePublic(CommandContext ctx) {

        String arg = "";
        
        if(ctx.getArgs().size()>0)
            arg = ctx.getArgs().get(0);

        switch(arg){
            case "":
                userStats(ctx);
            break;
            case "lb": case "leaderboard":
                leaderboard(ctx);
            break;
        }



    }   

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "[lb]", "Get the stats of how many accumulated minutes you were blinded or the leaderboard of this server.");
    }

    private void userStats(CommandContext ctx) {
        long rank = 0;
        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            pstmt = c.prepareStatement("SELECT RANK FROM STATS WHERE ID=?;");
            pstmt.setString(1, ctx.getMember().getId());
            try {
                ResultSet rs = pstmt.executeQuery();
                rank = rs.getLong("RANK");
            } catch (Exception e) {
                System.out.println("This user has no stats.");
                e.printStackTrace();
            }
            
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        ctx.getChannel().sendMessage("Your points are: " + (rank/60000)).queue();
    }

    private void leaderboard(CommandContext ctx){
        ArrayList<Object[]> users = new ArrayList<>();
        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            pstmt = c.prepareStatement("SELECT * FROM STATS WHERE RANK is not NULL;");
            
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                String id = rs.getString("ID");
                long rank = rs.getLong("RANK");
                users.add(new Object[]{id, rank});
            }

            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        Comparator<Object[]> objComp = new Comparator<Object[]>(){
            @Override
            public int compare(Object[] o1, Object[] o2) {
                long comp = ((long) o2[1] - (long) o1[1]);
                return (comp >= 0) ? (comp > 0) ? 1 : 0 : -1;
            }  
        };

        users.sort(objComp);
        

        String lb = "";
        Guild guild = ctx.getGuild();
        for (int i = 0; i < 10 && i < users.size(); i++) {
            Object[] obj = users.get(i);
            lb += guild.getMemberById(obj[0]+"").getAsMention() + " : " + ((long) obj[1]/60000) + "\n";
        }

        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setTitle("Leaderboard for blind and forceblind");
        eb.setDescription(lb);
    
        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
    
}
