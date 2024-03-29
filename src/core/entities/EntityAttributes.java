package core.entities;

import core.utils.SoundManager;

public class EntityAttributes {
    private boolean isAlive;

    private final int maxHealth;
    private int health;
    private final double speed;
    private final double runSpeed;
    private final double rotationSpeed;
    private final int damage;

    private int currentAmmo;
    private final int maxAmmo;
    private int ammoPack;
    private final int maxAmmoPack;

    private boolean key;

    private int score;

    public EntityAttributes(
            int health,
            double speed,
            double runSpeed,
            double rotationSpeed,
            int damage,
            int maxHealth,
            int currentAmmo,
            int maxAmmo,
            int ammoPack,
            int maxAmmoPack,
            int score
    ){
        this.health = health;
        this.speed = speed;
        this.runSpeed = runSpeed;
        this.rotationSpeed = rotationSpeed;
        this.damage = damage;
        this.maxHealth = maxHealth;
        this.currentAmmo = currentAmmo;
        this.maxAmmo = maxAmmo;
        this.ammoPack = ammoPack;
        this.maxAmmoPack = maxAmmoPack;
        this.score = score;
        this.isAlive = true;
        this.key = false;
    }

    /**
     * Takes damage from entity.
     *
     * @param damage The damage to be taken.
     */
    public void takeDamage(int damage){
        health -= damage;
        if(health <= 0)
            isAlive = false;
    }

    /**
     * Adds health to the entity.
     *
     * @param health The health to be added.
     */
    public void addHealth(int health){
        this.health += health;
        if(this.health > maxHealth)
            this.health = maxHealth;
    }

    /**
     * Adds ammo to the entity.
     */
    public void addAmmo(){
        ammoPack++;
    }

    /**
     * Adds score to the player.
     *
     * @param score The score to be added.
     */
    public void addScore(int score){
        this.score += score;
    }

    /**
     * Shoots a bullet.
     */
    public void shoot(){
        currentAmmo--;
        if(currentAmmo < 0) {
            if(ammoPack > 0){
                SoundManager.getInstance().playSound("reload");
                currentAmmo = maxAmmo;
                ammoPack--;
            }
            else currentAmmo = 0;
        }
    }


    public boolean isAlive() {
        return isAlive;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRunSpeed(){
        return runSpeed;
    }

    public double getRotationSpeed(){
        return rotationSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public int getAmmoPack() {
        return ammoPack;
    }

    public int getScore() {
        return score;
    }
    public void setKey(boolean key){ this.key = key; }
    public boolean getKey() { return key; }

    public void printAttributes(){
        System.out.println("Health: " + health);
        System.out.println("Speed: " + speed);
        System.out.println("Damage: " + damage);
        System.out.println("Current Ammo: " + currentAmmo);
        System.out.println("Max Ammo: " + maxAmmo);
        System.out.println("Ammo Pack: " + ammoPack);
        System.out.println("Score: " + score);
    }
}
