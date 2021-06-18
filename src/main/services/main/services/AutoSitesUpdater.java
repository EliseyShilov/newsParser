package main.services;

import main.robots.ZrRobot;

import java.io.IOException;

public class AutoSitesUpdater {

    public static void main(String[] args) throws IOException {
        ZrRobot zrRobot = new ZrRobot();

        zrRobot.updateZr();

    }
}
