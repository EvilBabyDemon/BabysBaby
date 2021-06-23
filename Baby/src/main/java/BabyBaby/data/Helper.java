package BabyBaby.data;

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

}
