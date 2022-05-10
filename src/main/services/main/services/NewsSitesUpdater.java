package main.services;

import main.robots.KpRobot;
import main.robots.VeRobot;

public class NewsSitesUpdater {

    public static void main(String[] args) {
        KpRobot kpRobot = new KpRobot();
        VeRobot veRobot = new VeRobot();


        kpRobot.updateKp();
        veRobot.updateVe();
    }
}
