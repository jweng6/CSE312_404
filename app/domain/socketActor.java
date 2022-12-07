package domain;

import akka.actor.ActorRef;

import java.util.List;

public class socketActor {
    private String code;
    private List<ActorRef> actorList;

    public socketActor(String code, List<ActorRef> actorList) {
        this.code = code;
        this.actorList = actorList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ActorRef> getActorList() {
        return actorList;
    }

    public void setActorList(List<ActorRef> actorList) {
        this.actorList = actorList;
    }
}
