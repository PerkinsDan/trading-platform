package com.tradingplatform.LiquidityEngine;

import java.util.concurrent.atomic.AtomicBoolean;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class ToggleLoopServer extends AbstractVerticle{
    private AtomicBoolean running = new AtomicBoolean(false);
    private Thread workerThread;

    @Override
    public void start(){
        Vertx vertx = getVertx();

        workerThread = new Thread(()->{
            OrderPoster poster = new OrderPoster(60);
            while(true){
                if (running.get()){
                        try {
                            poster.postOrder();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                } else {
                        System.out.println("Liquidity Engine thread is toggled to off");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
