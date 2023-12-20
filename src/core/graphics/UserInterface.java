package core.graphics;

import java.awt.image.BufferedImage;

public class UserInterface implements Runnable {

    private final Texture[] digits;

    //private final Thread thread;

    public UserInterface() {
        int MAX = 10;
        digits = new Texture[MAX];

       // thread = new Thread(this);

        for(int i = 0; i < MAX; i++){
           digits[i] = new Texture("src/textures/ui/digit/digit" + i + ".png");
        }
    }

    public BufferedImage getCombinedImage(int health, int ammo, int ammoPack, int score, boolean isKey){
        health = Math.max(health, 0);
        ammo = Math.max(ammo, 0);
        score = Math.max(score, 0);
        ammoPack = Math.max(ammoPack, 0);

        int healthFirst = health % 10;
        int healthSecond = (health / 10) % 10;
        int healthThird = (health / 100) % 10;

        int ammoFirst = ammo % 10;
        int ammoSecond = (ammo / 10) % 10;
        int ammoThird = (ammo / 100) % 10;

        int scoreFirst = score % 10;
        int scoreSecond = (score / 10) % 10;
        int scoreThird = (score / 100) % 10;
        int scoreFourth = (score / 1000) % 10;

        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(getHealthImage(healthFirst, healthSecond, healthThird), 0, 0, null);
        image.getGraphics().drawImage(getAmmoImage(ammoFirst, ammoSecond, ammoThird), 0, 0, null);
        image.getGraphics().drawImage(getScoreImage(scoreFirst, scoreSecond, scoreThird, scoreFourth), 0, 0, null);
        image.getGraphics().drawImage(getAmmoPackImage(ammoPack), 0, 0, null);

        if(isKey){
            image.getGraphics().drawImage(new Texture("src/textures/ui/key.png").getImage(), 0, 0, null);
        }

        return image;
    }

    private BufferedImage getAmmoPackImage(int ammoPack) {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(this.digits[ammoPack].getImage(), 55, 0, null);
        return image;
    }

    private BufferedImage getAmmoImage(int... values){
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(this.digits[values[0]].getImage(), 42,0 , null);
        image.getGraphics().drawImage(this.digits[values[1]].getImage(), 37, 0, null);
        image.getGraphics().drawImage(this.digits[values[2]].getImage(), 32, 0, null);

        return image;
    }

    private BufferedImage getScoreImage(int... values){
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(this.digits[values[0]].getImage(), 92,0 , null);
        image.getGraphics().drawImage(this.digits[values[1]].getImage(), 87, 0, null);
        image.getGraphics().drawImage(this.digits[values[2]].getImage(), 82, 0, null);
        image.getGraphics().drawImage(this.digits[values[3]].getImage(), 77, 0, null);

        return image;
    }

    private BufferedImage getHealthImage(int ... values) {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(this.digits[values[0]].getImage(), 10, 0, null);
        image.getGraphics().drawImage(this.digits[values[1]].getImage(), 5, 0, null);
        image.getGraphics().drawImage(this.digits[values[2]].getImage(), 0, 0, null);

        return image;
    }

    @Override
    public void run() {
        while(true){

        }
    }

    public void start(){
      //  thread.start();
    }
}
