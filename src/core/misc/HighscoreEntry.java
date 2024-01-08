package core.misc;

public class HighscoreEntry {

    private final String name;
    private final int score;
    private final String timeVal;

    public HighscoreEntry(){
        name = "";
        score = 0;
        timeVal = "";
    }

    public HighscoreEntry(String name, int score){
        this.name = name;
        this.score = score;
        timeVal = "";
    }

    public HighscoreEntry(String name, int score, String timeVal) {
        this.name = name;
        this.score = score;
        this.timeVal = timeVal;
    }

    public int getScore(){ return score; }
    public String getName(){ return name; }
    public String getTimeVal() { return timeVal; }

    @Override
    public String toString(){
        return String.format(
                "%s\t%d",name, score
        );
    }
}
