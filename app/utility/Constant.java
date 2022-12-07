package utility;

import akka.actor.ActorRef;

import java.util.List;

public class Constant {
    public static String SALT="404";
    public static List<ActorRef> list;

    public static String injection(String word){
        return word.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("/","&#47;")
                .replace("@","&#64");
    }
}
