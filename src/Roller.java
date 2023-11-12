
public class Roller extends Commutator
{

    private int currentOffset;
    private int advancementOffset;
        /*
        I due vettori contengono numeri. quando bisogna
        trasformare la lettera si accede a inputs[lettera] e si vede quale indice
        di outputs ha lo stesso valore.
        */

    public Roller(int startingOffset, int advancementOffset, int[] commutations){
        super(commutations);
        this.currentOffset = startingOffset;
        this.advancementOffset = advancementOffset;
    }
    public Roller(int[] commutations){
        this(0,0,commutations);
    }

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;

    }
    public void setAdvancementOffset(int advancementOffset) {
        this.advancementOffset = advancementOffset;

    }

    public int getAdvancementOffset() {
        return advancementOffset;

    }
    public int getCurrentOffset() {
        return currentOffset;

    }

    public void advance(){
        currentOffset  = (currentOffset + 1) % ALPHABET_LENGTH;

    }
    public boolean isBlockingNextRoller(){
        return currentOffset != advancementOffset;

    }

    @Override
    public char commute(char c){
        //System.out.println(currentOffset + "???????   " + (int)c);
        char result = super.commute( (char) ( (c - 'A' + currentOffset) % ALPHABET_LENGTH + 'A') );
        return (char) ((result - currentOffset + ALPHABET_LENGTH - 'A') % ALPHABET_LENGTH + 'A');
    }
}