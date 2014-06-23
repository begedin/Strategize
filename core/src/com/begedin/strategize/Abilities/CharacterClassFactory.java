package com.begedin.strategize.Abilities;

public class CharacterClassFactory {

    private static CharacterClass defaultClass_instance;

    public static CharacterClass defaultClass() {
        if (defaultClass_instance == null) {
            defaultClass_instance = new CharacterClass("Mechanized Infantry", "Your plain old tank");
            defaultClass_instance.offensiveAction = ActionFactory.offensive("Fire", 3, 70, 5);
            defaultClass_instance.defensiveAction = ActionFactory.repair("Repair", 2, 95, 2);
        };
        return defaultClass_instance;
    }

    public static class CharacterClass {

        // Each class has a list of actions they can learn, a name, and a description
        public CombatAction offensiveAction;
        public CombatAction defensiveAction;
        //public CombatAction passiveAction;
        public String name;
        public String description;

        public CharacterClass(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
