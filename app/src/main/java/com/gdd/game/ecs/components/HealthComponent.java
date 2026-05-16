package com.gdd.game.ecs.components;

public class HealthComponent extends Component{
    public int health;

    public HealthComponent(int startingHealth) {
        health = startingHealth;
    }

    @Override
    public ComponentType type() {
        return ComponentType.HEALTH;
    }

    public boolean isAlive() { return health > 0; }

    public void heal(int heal) {
        health += heal;
    }
    public void takeDamage(int dmg) {
        health -= dmg;
    }
}
