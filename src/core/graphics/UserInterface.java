package core.graphics;

public class UserInterface {
    private final Texture[] firstDigit;
    private final Texture[] secondDigit;
    private final Texture[] thirdDigit;

    public UserInterface(){
        firstDigit = new Texture[10];
        secondDigit = new Texture[10];
        thirdDigit = new Texture[10];

        for(int i = 0; i < 10; i++){
            firstDigit[i] = new Texture("src/textures/ui/numbers/firstDigit" + i + ".png");
            secondDigit[i] = new Texture("src/textures/ui/numbers/secondDigit" + i + ".png");
            thirdDigit[i] = new Texture("src/textures/ui/numbers/thirdDigit" + i + ".png");
        }
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
}
