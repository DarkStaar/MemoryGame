package milan.kapetanovic.memorygame;

public class JNIExample {
    static {
        System.loadLibrary("GameLibrary");
    }

    public native double racunajProcenat(double score);
}
