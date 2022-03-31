package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class EditAssignCMD implements IAdminCMD{


	@Override
	public String getName() {
		return "editassign";
	}

	@Override
	public void handleAdmin(CommandContext ctx) {
		if(!ctx.getGuild().getId().equals(Data.ETH_ID)){
            return;
        }
        
    

        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();
        HashSet<String> cats = new HashSet<String>(); 

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
            while ( rs.next() ) {
                String cat = rs.getString("categories");
                cats.add(cat);
            }
            
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }
        
        //doing embeds with each category

        String msg = "";



        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        ArrayList<String> categ = new ArrayList<>();
        LinkedList<String> roles = new LinkedList<>();
        
        for (String strCateg : cats) {
            HashMap<Role, Object[]> sorting = new HashMap<>();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                stmt = c.createStatement();

                Guild called = ctx.getGuild();
                rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES WHERE categories='" + strCateg + "';");
                while ( rs.next() ) {
                    String rcat = rs.getString("ID");
                    String emoteStr = rs.getString("EMOTE");
                    String orig = emoteStr;


                    try {
                        Long.parseLong(emoteStr);
                        try {
                            emoteStr = ctx.getJDA().getEmoteById(emoteStr).getAsMention();   
                        } catch (Exception e) {
                            emoteStr = "ERROR";
                        }
                    } catch (Exception e) {
                    }


                    msg = emoteStr + " : "+ called.getRoleById(rcat).getAsMention() + "\n";
                    sorting.put(called.getRoleById(rcat), new Object[] {orig, msg});
                }
                rs.close();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }

            LinkedList<Object[]> sorted = rolesorter(sorting);
            LinkedList<String> tempo = new LinkedList<>();
            msg = "";
            for (Object[] obj : sorted) {
                tempo.add((String) obj[0]);
                msg += (String) obj[1];
            }

            emotes.add(tempo);
            categ.add(strCateg);
            roles.add(msg);
            msg = "";
        }

        ArrayList<EmbedBuilder> emb = new ArrayList<>();
        LinkedList<String> remover = new LinkedList<>();

        for (int i = 0; i < categ.size(); i++) {
            emb.add(embeds(categ.get(i), roles.get(i)));
        }

        for (int k = 0; k < emb.size(); k++) {
            EmbedBuilder eb = emb.get(k);
            LinkedList<String> tempEmo = new LinkedList<>();
            tempEmo.addAll(emotes.remove(0));

            
            ArrayList<Button> butt = new ArrayList<>();
            for (String emoID : tempEmo) {
                
                boolean gemo = false;
                
                try {
                    Long.parseLong(emoID);
                    gemo = true;
                } catch (Exception e) {
                }
                
                try{
                    butt.add(Button.primary(emoID, gemo ? Emoji.fromEmote(ctx.getJDA().getEmoteById(emoID)): Emoji.fromUnicode(emoID)));
                } catch (Exception e){
                    ctx.getChannel().sendMessage("Reaction with ID:" + emoID + " is not accessible.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
            }

            

            LinkedList<ActionRow> acR = new LinkedList<>();
            for (int i = 0; i < butt.size(); i +=5) {
                ArrayList<Button> row = new ArrayList<>();
                for (int j = 0; j < 5 && j+i < butt.size(); j++) {
                    row.add(butt.get(i+j));
                }
                acR.add(ActionRow.of(row));
            }

            MessageAction msgAct;

            if(!Data.catToMsg.containsKey(categ.get(k))){
                msgAct = channel.sendMessageEmbeds(eb.build());
                msgAct.setActionRows(acR);
                Message msgs = msgAct.complete();
                Data.msgid.add(msgs.getId());
                ArrayList<String> templist = Data.catToMsg.getOrDefault(categ.get(k), new ArrayList<String>());
                templist.add(msgs.getId());
                Data.catToMsg.put(categ.get(k), templist);
                Data.msgToChan.put(msgs.getId(), msgs.getChannel().getId());
                
                c = null;
                PreparedStatement pstmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    pstmt = c.prepareStatement("INSERT INTO MSGS (GUILDID, CHANNELID, MSGID, CATEGORY) VALUES (?, ?, ?, ?);");
                    pstmt.setString(1, ctx.getGuild().getId());
                    pstmt.setString(2, ctx.getChannel().getId());
                    pstmt.setString(3, msgs.getId());
                    pstmt.setString(4, categ.get(k)); 
                    pstmt.executeUpdate();
                    pstmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace(); 
                    return;
                }
                continue;
            }



            
            for (String msgid : Data.catToMsg.get(categ.get(k))) {
                TextChannel chan = ctx.getGuild().getTextChannelById(Data.msgToChan.get(msgid));
                try {
                    Message sent = chan.retrieveMessageById(msgid).complete();
                    msgAct = sent.editMessageEmbeds(eb.build());
                } catch (Exception e) {
                    remover.add(msgid);
                    continue;
                }
                
                msgAct.setActionRows(acR);
                msgAct.complete();
            }

        }


        
        for (String oldMsgID : remover) {
            c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                pstmt = c.prepareStatement("DELETE FROM MSGS WHERE MSGID = ?;");
                pstmt.setString(1, oldMsgID);
                pstmt.executeUpdate();
                pstmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                e.printStackTrace(); 
            }
            Data.msgid.remove(oldMsgID);
            
            Data.msgToChan.remove(oldMsgID);
        }

        ctx.getMessage().delete().queue();

		
	}

	@Override
	public MessageEmbed getAdminHelp(String prefix) {
		return StandardHelp.Help(prefix, getName(), "", "Update'" + new RoleAssignCMD().getName() + "' cmd messages. This technically can be used in any channel and it will update all saved embeds but if a new category was added it will send that embed in the channel the command was used.");
	}


    public EmbedBuilder embeds(String title, String msg){
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(1);
        eb.setDescription(msg);
        eb.setFooter("Click on the Emotes to assign yourself Roles.");

        return eb;
    }

    public LinkedList<Object[]> rolesorter (HashMap<Role, Object[]> sorting){
        LinkedList<Object[]> res = new LinkedList<>();
        while(sorting.size()!=0){
            Role highest = null;
            for (Role role : sorting.keySet()) {
                if(highest == null || role.getPosition() > highest.getPosition()){
                    highest = role;
                }
            }
            res.add(sorting.get(highest));
            sorting.remove(highest);
        }
        return res;
    }

    public boolean cont (List<String> c, String s){
        for (String str : c) {
            if(str.contains(s)){
                return true;
            }
        }
        return false;
    }
    
}
