package com.begedin.strategize.Components;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;
import com.begedin.strategize.Abilities.CharacterClassFactory;
import com.begedin.strategize.Abilities.CombatAction;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class Stats extends Component {

    public int level, xp, next_xp;

    private int power, power_modifier;
    private int defense, defense_modifier;
    private int supply, supply_modifier;

    private int precision, precision_modifier;
    private int speed, speed_modifier;

    public int health, maxHealth, maxHealth_modifier;
    public int energy, maxEnergy, maxEnergy_modifier;

    public CombatAction regularAttack;
    public CharacterClassFactory.CharacterClass primaryClass, secondaryClass;

    public String name;

    public int actionPoints;

    public Stats(boolean b) {
        this();
        actionPoints = 100;
    }

    public Stats() {
        level = 1;
        xp = 0;
        next_xp = 100;

        power = 15 + MathUtils.random(-3, 3);
        defense = 15 + MathUtils.random(-3, 3);
        supply = 15 + MathUtils.random(-3,3);
        precision = 15 + MathUtils.random(-3,3);
        speed = 15 + MathUtils.random(-3,3);

        health = maxHealth = 2*getDefense();
        energy = maxEnergy = 2*getSupply();

        power_modifier = defense_modifier = supply_modifier =  maxHealth_modifier = precision_modifier = speed_modifier = 0;

        name = names[MathUtils.random(names.length-1)];

        regularAttack = new CombatAction("Attack", 7, 0, 90, 1, 1);

        primaryClass = CharacterClassFactory.technician();
        secondaryClass = CharacterClassFactory.mechInfantry();

        actionPoints = 0;
    }

    private final String[] names = {"Rodann","Ranlan","Luhiri","Serl","Polm","Boray","Ostan","Inaes"};

    public int getPower(){
        return power + power_modifier;
    }

    public int getDefense(){
        return defense + defense_modifier;
    }

    public int getSupply(){
        return supply + supply_modifier;
    }

    public int getPrecision(){
        return precision + precision_modifier;
    }

    public int getSpeed(){
        return speed + speed_modifier;
    }
}
