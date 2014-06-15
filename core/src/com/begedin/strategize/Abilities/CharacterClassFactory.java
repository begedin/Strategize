package com.begedin.strategize.Abilities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Nikola Begedin on 01.01.14..
 */
public class CharacterClassFactory {

    private static CharacterClass mechInfantry_instance, artillery_instance, technician_instance;

    public static CharacterClass mechInfantry() {
        if (mechInfantry_instance == null) {
            mechInfantry_instance = new CharacterClass("Mechanized Infantry", "Your plain old tank");
            mechInfantry_instance.actions.add(ActionFactory.pointBlankNormal("Berserk", 3, 70));
            mechInfantry_instance.actions.add(ActionFactory.rangedNormal("Long shot", 2, 95, 2));
            mechInfantry_instance.actions.add(ActionFactory.pointBlankSkill("Aimed berserk", 3, 2, 100, 1));
            mechInfantry_instance.actions.add(ActionFactory.repair("Emergency repair", 2, 1, 0, 1));
        };
        return mechInfantry_instance;
    }

    public static CharacterClass artillery() {
        if (artillery_instance == null) {
            artillery_instance = new CharacterClass("Archer", "Shoots stuff");
            artillery_instance.actions.add(ActionFactory.rangedNormal("Calculated strike", 3, 100, 9));
            artillery_instance.actions.add(ActionFactory.rangedNormal("Rampant strike", 6, 70, 9));
        }
        return artillery_instance;
    }

    public static CharacterClass technician() {
        if (technician_instance == null) {
            technician_instance = new CharacterClass("Healer", "...heals?");
            technician_instance.actions.add(ActionFactory.repair("Repair", 5, 5, 4, 1));
            technician_instance.actions.add(ActionFactory.repair("Group repair", 5, 10, 4, 2));
        }
        return technician_instance;
    }

    public static class CharacterClass {
        // To become this class, you need certain level proficiency in these other classes...
        public ObjectMap<CharacterClass,Integer> requirements;

        // Each class has a list of actions they can learn, a name, and a description
        public Array<CombatAction> actions;
        public String name;
        public String description;

        public CharacterClass(String name, String description) {
            this.name = name;
            this.description = description;
            actions = new Array<CombatAction>();
            requirements = new ObjectMap<CharacterClass, Integer>();
        }
    }
}
