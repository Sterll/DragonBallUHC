package fr.yanis.dragonballuhc.list;

public enum EffectList {

    BabySpeed(5, "§bBaby Speed"),
    BabyResistance(5, "§9Baby Resistance"),
    BabyStrength(5, "§cBaby Force"),
    SPEED1(20, "§bSpeed 1"),
    SPEED2(40, "§bSpeed 2"),
    SPEED3(60, "§bSpeed 3"),
    RESISTANCE1(20, "§9Resistance 1"),
    RESISTANCE2(30, "§9Resistance 2"),
    RESISTANCE3(40, "§9Resistance 3"),
    STRENGTH1(15, "§cForce 1"),
    STRENGTH2(25, "§cForce 2"),
    STRENGTH3(35, "§cForce 3");

    private int multiplier;
    private String display;
    EffectList(int multiplier, String display){
        this.multiplier = multiplier;
        this.display = display;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public String getDisplay() {
        return display;
    }
}
