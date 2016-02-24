package mic82.ebusiness.hw2;

import java.io.Serializable;

/**
 * Created by chiming on 16/2/23.
 */
public class Data implements Serializable {
    private String title;
    private String content;
    private String x;
    private String y;
    private String date;   // yyyy-mm-dd
    private String time;   // hh:mm

    public Data() {}

    public Data(String title, String content, String x, String y, String date, String time) {
        this.title = title;
        this.content = content;
        this.x=x;
        this.y=y;
        this.date=date;
        this.time=time;
    }

    // get{...}
    public String getTitle() {return title;}

    public String getContent() {return content;}

    public String getX() {return x;}

    public String getY() {return y;}

    public String getDate() {return date;}

    public String getTime() {return time;}

    // set{...}
    public void setTitle(String title) {this.title = title;}

    public void setContent(String content) {this.content = content;}

    public void setX(String x) {this.x = x;}

    public void setY(String y) {this.y = y;}

    public void setDate(String date) {this.date = date;}

    public void setTime(String time) {this.time = time;}
}

