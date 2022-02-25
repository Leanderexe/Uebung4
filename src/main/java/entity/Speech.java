package entity;

import java.util.Date;
import java.util.List;

public class Speech {

    String date;
    String speakerID;
    String fraktion;
    String speechID;
    List content;
    List comments;

    public Speech (String date, String speakerID,  String speechID, List content, List comments) {
        this.date = date;
        this.speakerID = speakerID;
        this.speechID = speechID;
        this.content = content;
        this.comments = comments;

    }

    public void printSpeech() {
        System.out.println(" Date: " + date );
        System.out.println(" speakerID " + speakerID );
        System.out.println(" spechID " + speechID );
        System.out.println(" content " + content );
        System.out.println(" comments" + comments );
    }

}
