package BabyBaby.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Helper {
    public static Object[] getUnits(String unit, double time){
        //default minutes
        String strUnit = "";
        long rounder = 0;
        if(unit == null) {
            strUnit = "minutes";
            rounder = (long) (time*60);
        } else {
            unit = unit.toLowerCase();
            if (unit.equals("ps")) {
                strUnit = "picoseconds";
                rounder = (long) (time/1_000_000_000_000L);
            } else if (unit.equals("ns")) {
                strUnit = "nanoseconds";
                rounder = (long) (time/1_000_000_000);
            } else if (unit.equals("μs")) {
                strUnit = "microseconds";
                rounder = (long) (time/1_000_000);
            } else if (unit.equals("ms")) {
                strUnit = "milliseconds";
                rounder = (long) (time/1000);
            } else if(unit.startsWith("m")){
                strUnit = "minutes";
                rounder = (long) (time*60);
            } else if (unit.startsWith("h")){
                strUnit = "hours";
                rounder = (long) (time*3600);
            } else if(unit.startsWith("d")){
                strUnit = "days";
                rounder = (long) (time*24*3600);
            } else if (unit.startsWith("w")) {
                strUnit = "weeks";
                rounder = (long) (time*7*24*3600);
            } else if (unit.startsWith("y")) {
                strUnit = "years";
                rounder = (long) (time*365*24*3600);
            } else { // if unit not found, use seconds
                strUnit = "seconds";
                rounder = (long) (time);
            }
        }
        long endOfTime = Long.MAX_VALUE/1000 - System.currentTimeMillis();
        if(rounder > endOfTime){
            rounder = endOfTime - 100000;
        }
        //returning Unit which was used and input in seconds
        return new Object[]{strUnit, rounder};
    }

    public static String[] splitUnitAndTime (String str){
        String unit = null;
        String amount = str;  
        
        for(int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                amount = str.substring(0, i);
                unit = str.substring(i, i+1);
                break;
            }
        }
    
        return new String[]{unit, amount};
    }

    public static String addFieldSieve (EmbedBuilder eb, LinkedList<String> cacherefresh, int dooku, String mention){
        
        String submention = mention.substring(0, 1024);
        String[] part = submention.split("\n");
        submention = mention.substring(0, 1024 - part[part.length-1].length() - 1);
        eb.addField(""+ dooku, submention, true);
        mention = mention.substring(submention.length());
        dooku++;
        cacherefresh.add(submention);
        
        return mention;
    }

    public static int minDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];
     
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
     
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
     
        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            int minimal = 100;
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);
     
                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;
                    
                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
                if(minimal>dp[i + 1][j + 1])
                    minimal = dp[i + 1][j + 1];
            }
            if(minimal >= (2 + Math.min(len1, len2)/5*2))
                return 100;
        }
        
        return (dp[len1][len2]<(2 + Math.min(len1, len2)/5*2)? dp[len1][len2]:100);
    }

    // Giving/Removing roles from an Interaction
    public static void roleGiving (Member member, Guild guild, boolean failed, Role role,  InteractionHook msgHook){
        List<Role> memRole = member.getRoles();
        Role student = guild.getRoleById(Data.ethstudent);
        Role external = guild.getRoleById(Data.ethexternal);
        if(memRole.contains(role)){
            if((role.equals(student) && !memRole.contains(external)) || (role.equals(external) && !memRole.contains(student))){
                String oneneeded = "You need at least  " + student.getAsMention() + " or " + external.getAsMention();
                Helper.unhook(oneneeded, failed, msgHook, member.getUser());
                return;
            }
            guild.removeRoleFromMember(member, role).complete();
            String remove = "I removed " + role.getAsMention() + " from you.";
            Helper.unhook(remove, failed, msgHook, member.getUser());
        } else {
            if(role.equals(student)){
                
                if(!verifiedUser(member.getId())){
                    String doverify = "You have to get verified to get the role ";
                    String suffix = ". You can do that here: https://dauth.spclr.ch/ and write the token to <@306523617188118528>";
                    Helper.unhook(doverify + role.getAsMention() + suffix, failed, msgHook, member.getUser());
                    return;
                }
                
                notBoth(role, external, guild, member, memRole, failed, msgHook);


            } else if (role.equals(external)){
                notBoth(role, student, guild, member, memRole, failed, msgHook);
            } else {
                guild.addRoleToMember(member, role).complete();
                String added = "I gave you " + role.getAsMention() + ".";
                Helper.unhook(added, failed, msgHook, member.getUser());
            }            
        }
    }

    //Check if Verified
    public static boolean verifiedUser(String id){
        boolean verified = false;
        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            pstmt = c.prepareStatement("SELECT ID FROM VERIFIED WHERE ID=?;");
            pstmt.setString(1, id);
            try {
                ResultSet  rs = pstmt.executeQuery();
                verified = rs.getString("ID").equals(id);   
            } catch (Exception e) {
            }
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            return false;
        }
        return verified;
    }

    //Check if member has not both roles
    private static void notBoth(Role change, Role other, Guild guild, Member member, List<Role> memRole, boolean failed, InteractionHook msgHook){
        guild.addRoleToMember(member, change).complete();

        String added = "I gave you ";
        String suffix = "";
        boolean rem = false; 

        if(memRole.contains(other)){
            guild.removeRoleFromMember(member, other).complete();
            suffix = " and removed ";
            rem = true;
        }

        if(rem) suffix += other.getAsMention();
        Helper.unhook(added + change.getAsMention() +  suffix + ".", failed, msgHook, member.getUser());
    }

    public static void unhook(String message, boolean failed, InteractionHook hook, User user){
        if(failed){
            user.openPrivateChannel().complete().sendMessage(message).complete();
        } else {
            hook.editOriginal(message).queue();
        }
    }
    
    public static void unhook(MessageEmbed message, boolean failed, InteractionHook hook, User user){
        if(failed){
            user.openPrivateChannel().complete().sendMessageEmbeds(message).queue();
        } else {
            hook.editOriginalEmbeds(message).queue();
        }
    }
}
