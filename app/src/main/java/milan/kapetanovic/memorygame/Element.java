package milan.kapetanovic.memorygame;

import android.database.Cursor;

public class Element {
    private String name;
    private String email;
    private int    bestScore;
    private int    worstScore;

    public Element(String name, String email, int bestScore, int worstScore)
    {
        this.name = name;
        this.email = email;
        this.bestScore = bestScore;
        this.worstScore = worstScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getWorstScore() {
        return worstScore;
    }

    public void setWorstScore(int worstScore) {
        this.worstScore = worstScore;
    }
}
