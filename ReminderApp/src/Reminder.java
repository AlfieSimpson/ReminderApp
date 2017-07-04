/**
 * Created by alumniCurie19 on 19/06/2017.
 */
public class Reminder {
    boolean weekC,dayC,hourC;
    int day,month,year,hour,minute;
    String desc;
    public Reminder(boolean weekC,boolean dayC, boolean hourC,
                    int day, int month, int year,
                    int hour, int minute, String desc){
        this.day=day;
        this.month=month;
        this.year=year;
        this.hour=hour;
        this.minute=minute;
        this.weekC=weekC;
        this.dayC=dayC;
        this.hourC=hourC;
        this.desc=desc;

    }
    public String toString(){
      return new String(weekC+","+dayC+","+hourC+","+day
              +","+month+","+year+","+hour+","+minute+","+desc+"\n");  }
}
