package core.misc;

public class HighscoreEntry {

    private final String name;
    private final int score;

    public HighscoreEntry(){
        name = "";
        score = 0;
    }

    public HighscoreEntry(String name, int score){
        this.name = name;
        this.score = score;
    }

    public int getScore(){ return score; }
    public String getName(){ return name; }

    @Override
    public String toString(){
        return String.format(
                "%s\t%d",name, score
        );
    }
}
