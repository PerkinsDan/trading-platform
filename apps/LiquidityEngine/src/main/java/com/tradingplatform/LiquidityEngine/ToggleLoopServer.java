package com.tradingplatform.LiquidityEngine;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class ToggleLoopServer extends AbstractVerticle{
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread workerThread;

    @Override
    public void start(){
        Vertx vertx = getVertx();

        workerThread = new Thread(()->{
            while(true){
                if (running.get()){
                    OrderPoster poster = new OrderPoster(20);
                    try {
                        poster.startOrderStream();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        System.out.println("Liquidity Engine thread is toggled to off");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
        workerThread.setDaemon(true);
        workerThread.start();

        vertx.createHttpServer()
            .requestHandler(request -> {
                if("/LiquidityEngine/toggle".equals(request.path())){
                    boolean newState = !running.get();
                    running.set(newState);
                    request.response()
                        .putHeader("content-type","text/plain")
                        .end("Liquidity Engine is now " + (newState ? "running" : "stopped"));

                } else {
                    request.response().setStatusCode(404).end("Invlaid adress homie");
                }
            })
            .listen(8888, http -> {
                if(http.succeeded()){
                    System.out.println("Http server started on port 8888");
                } else {
                    System.out.println("Https server failed to start - Error : " + http.cause());
                }
            });
    }

    public static void main(String[] args){
        Vertx.vertx().deployVerticle(new ToggleLoopServer());                
    }

}
