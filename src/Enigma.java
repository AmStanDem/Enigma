public class Enigma {
    public static final int ALPHABET_LENGTH = 'Z' - 'A';
    private static final int NUM_AVAILABLE_ROLLERS = 5;
    private static final int NUM_SELECTABLE_ROLLERS = 3;




    private Roller[] availableRollers;
    private Roller[] selectedRollers;
    private Commutator commutator;

    
    public Enigma() {
        availableRollers = new Roller[NUM_AVAILABLE_ROLLERS];
        selectedRollers = new Roller[NUM_SELECTABLE_ROLLERS];
    }

    public Roller[] getAvailableRolls(){
        return availableRollers;

    }
    public Roller[] getSelectedRollers() {
        return selectedRollers;

    }
    public Roller getAvailableRoller(int index){
        return availableRollers[index];

    }
    public Roller getSelectedRoller(int index){
        return selectedRollers[index];

    }
    public Commutator getFinalCommutator(){
        return commutator;

    }

    public void setSelectedRollers(Roller[] rollers){
        for(int i = 0 ; i < NUM_SELECTABLE_ROLLERS; i++){
            selectedRollers[i] = rollers[i];
        }
    }
    public void setSelectedRollers(int[] rollersIndex){
        for(int i = 0 ; i < NUM_SELECTABLE_ROLLERS; i++){
            selectedRollers[i] = availableRollers[rollersIndex[i]];
        }
    }


    private void advanceRolls(){
        for(int i = NUM_SELECTABLE_ROLLERS; i > 0 ; i--){
            if(!selectedRollers[i - 1].isBlockingNextRoller()){
                selectedRollers[i].advance();
            }
        }
        selectedRollers[0].advance();
    }
    private char translate(char c){
        for(int i = 0 ; i < selectedRollers.length; i++){
            c = selectedRollers[i].commute(c);
        }

        c = commutator.commute(c);

        for(int i = selectedRollers.length - 1; i >= 0; i--){
            c = selectedRollers[i].commute(c);
        }

        return c;
    }
    public char pushKey(Character c){
        c = translate(c);

        advanceRolls();
        return c;
    }
    public String writeMsg(String msg){
        String result = "";

        for(int i = 0; i < msg.length(); i++){
            result = result + pushKey(msg.charAt(i));
        }

        return result;
    }



}
