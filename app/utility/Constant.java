package utility;

import akka.actor.ActorRef;
import java.util.ArrayList;
import java.util.List;
import domain.socketActor;

public class Constant {
    public static String SALT="404";
    public static socketActor list = new socketActor();
    public static String currentServer = "";
    public static List<socketActor> ClientList = new ArrayList<>();

    public static String injection(String word){
        return word.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("/","&#47;")
                .replace("@","&#64");
    }
}
