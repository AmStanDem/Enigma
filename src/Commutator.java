public class Commutator {
    public static final int ALPHABET_LENGTH = 'Z' - 'A' + 1;


    private final int[] commutations;

    public Commutator(int[] commutationsStr){
        this.commutations = new int[ALPHABET_LENGTH];

        for(int i = 0; i < ALPHABET_LENGTH/2; i++){
            commutations[commutationsStr[2*i]] = commutationsStr[2*i + 1];
            commutations[commutationsStr[2*i + 1]] = commutationsStr[2*i];
        }
    }

    public char commute(char c){
        return (char)commutations[c - 'A'];

    }
}
