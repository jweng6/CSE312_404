package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.WebSocket;
import utility.MyWebSocketActor;

import javax.inject.Inject;

public class WebsocketController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    @Inject
    public WebsocketController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    public WebSocket socket(String code) {
        return WebSocket.Json.accept(
                request -> ActorFlow.actorRef(MyWebSocketActor::props, actorSystem, materializer));
    }



}
