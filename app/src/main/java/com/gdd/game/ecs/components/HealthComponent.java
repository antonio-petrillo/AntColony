package com.gdd.game.ecs.components;

public class HealthComponent extends Component{

    public int health, maxHealth;

    public HealthComponent(int health) {
        this.health = health;
        maxHealth = health;
    }

    @Override
    public ComponentType type() {
        return ComponentType.HEALTH;
    }

    public boolean isAlive() { return health > 0; }

    public void heal(int heal) {
        health += heal;
        if (health > maxHealth) health = maxHealth;
    }
    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isLow() {
        return ( health <= maxHealth/2 );
    }
}
