package core.graphics;

import core.utils.Config;

import java.awt.image.BufferedImage;

public class UserInterface implements Runnable {
    private final Texture[] firstDigit;
    private final Texture[] secondDigit;
    private final Texture[] thirdDigit;

    private Thread thread;

    public UserInterface() {
        firstDigit = new Texture[10];
        secondDigit = new Texture[10];
        thirdDigit = new Texture[10];

        thread = new Thread(this);

        for(int i = 0; i < 10; i++){
            firstDigit[i] = new Texture("src/textures/ui/numbers/firstDigit" + i + ".png");
            secondDigit[i] = new Texture("src/textures/ui/numbers/secondDigit" + i + ".png");
            thirdDigit[i] = new Texture("src/textures/ui/numbers/thirdDigit" + i + ".png");
        }
    }

    public BufferedImage getHealthDigits(int health){
        health = Math.max(health, 0);
        int firstDigit = health % 10;
        int secondDigit = (health / 10) % 10;
        int thirdDigit = (health / 100) % 10;

        return combineDigits(firstDigit, secondDigit, thirdDigit);
    }

    private BufferedImage combineDigits(int firstDigit, int secondDigit, int thirdDigit) {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(this.firstDigit[firstDigit].getImage(), 0, 0, null);
        image.getGraphics().drawImage(this.secondDigit[secondDigit].getImage(), 0, 0, null);
        image.getGraphics().drawImage(this.thirdDigit[thirdDigit].getImage(), 0, 0, null);
        return image;
    }

    public Texture getFirstDigit(int idx){
        return firstDigit[idx];
    }

    public Texture getSecondDigit(int idx){
        return secondDigit[idx];
    }

    public Texture getThirdDigit(int idx){
        return thirdDigit[idx];
    }

    @Override
    public void run() {
        while(true){

        }
    }

    public void start(){
        thread.start();
    }
}
