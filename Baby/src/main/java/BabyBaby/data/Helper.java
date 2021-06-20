package BabyBaby.data;

public class Helper {
    public static Object[] getUnits(String unit, String strUnit, long rounder, long time){
        if(unit == null) {
            strUnit = "minutes";
            rounder = (long) (time*60);
        } else {
            unit = unit.toLowerCase();
            if (unit.startsWith("h")){
                strUnit = "hours";
                rounder = (long) (time*3600);
            } else if(unit.startsWith("m")){
                strUnit = "minutes";
                rounder = (long) (time*60);
            } else if(unit.startsWith("d")){
                strUnit = "days";
                rounder = (long) (time*24*3600);
            } else {
                strUnit = "seconds";
                rounder = (long) (time);
            }
        }
        return new Object[]{};
    }
}
