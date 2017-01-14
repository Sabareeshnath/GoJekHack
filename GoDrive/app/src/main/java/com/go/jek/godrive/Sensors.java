package com.go.jek.godrive;

/**
 * Created by kumardev on 11/26/2016.
 */

public class Sensors {
    private int noise;
    private int temperature;


    public Sensors() {
    }

    public int getNoise() {
        return noise;
    }

    public void setNoise(int noise) {
        this.noise = noise;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
