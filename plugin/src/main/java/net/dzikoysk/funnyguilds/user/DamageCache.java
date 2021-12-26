package net.dzikoysk.funnyguilds.user;

public class DamageCache {

    private final User attacker;

    private double damage;
    private long lastTime;

    public DamageCache(User attacker, double damage, long lastTime) {
        this.attacker = attacker;
        this.damage = damage;
        this.lastTime = lastTime;
    }

    public User getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void addDamage(double damage) {
        this.damage += damage;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

}
