public class Commutator {
    public static final int ALPHABET_LENGTH = 'Z' - 'A';


    private final int[] commutations;

    public Commutator(int[] commutations){
        this.commutations = commutations;
    }

    public char commute(char c){
        return (char)commutations[c - 'A'];

    }
}
