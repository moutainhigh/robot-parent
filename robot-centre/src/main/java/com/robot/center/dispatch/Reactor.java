package com.robot.center.dispatch;

/**
 * Created by mrt on 10/31/2019 12:26 PM
 */
public interface Reactor {

    void handleEvents();

    void registerEvents(RegisterBody registerBody);

}
