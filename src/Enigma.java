public class Enigma {
    public static final int ALPHABET_LENGTH = 'Z' - 'A' + 1;
    private static final int NUM_AVAILABLE_ROLLERS = 5;
    private static final int NUM_SELECTABLE_ROLLERS = 3;
    private Roller[] availableRollers;
    private Roller[] selectedRollers;
    private Commutator commutator;
    private int [] plugBoard;

    
    public Enigma() {
        plugBoard = new int[ALPHABET_LENGTH];
        for(int i = 0; i < plugBoard.length; i++){
            plugBoard[i] = 0;
        }
        /*
        cabledSwitches[2] = 1;
        cabledSwitches[5] = 1;
        cabledSwitches[3] = 2;
        cabledSwitches[8] = 2;
        cabledSwitches[9] = 3;
        cabledSwitches[23] = 3;
        cabledSwitches[11] = 4;
        cabledSwitches[12] = 4;
        cabledSwitches[7] = 5;
        cabledSwitches[21] = 5;
        cabledSwitches[0] = 6;
        cabledSwitches[18] = 6;
        cabledSwitches[4] = 7;
        cabledSwitches[19] = 7;
        cabledSwitches[20] = 8;
        cabledSwitches[13] = 8;
         */


        availableRollers = new Roller[NUM_AVAILABLE_ROLLERS];
        selectedRollers = new Roller[NUM_SELECTABLE_ROLLERS];

        commutator = new Commutator(new int[]{11,2,1,4,3,5,7,6,16,9,25,0,24,20,23,13,8,14,21,15,17,18,19,22,12,10});

        availableRollers[0] = new Roller(new int[]{9,11,2,16,4,25,5,7,17,0,3,1,6,10,12,22,13,20,21,15,18,19,23,24,14,8});
        availableRollers[1] = new Roller(new int[]{15,10,22,1,4,25,5,7,8,16,2,9,18,6,12,0,13,20,11,14,21,17,23,24,3,19});
        availableRollers[2] = new Roller(new int[]{16,11,2,4,19,8,25,5,9,6,3,10,1,13,20,14,15,17,18,22,7,12,23,0,24,21});
        availableRollers[3] = new Roller(new int[]{11,2,4,25,7,20,16,9,3,10,6,5,0,8,12,13,14,21,15,17,18,19,22,23,24,1});
        availableRollers[4] = new Roller(new int[]{19,11,2,7,0,4,25,5,8,16,9,3,10,1,24,13,20,14,15,22,17,18,23,12,21,6});

        setDefaultConfiguration();
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
    public int[] getPlugBoard(){
        return plugBoard;

    }
    public int getAvailablePlugBoardCode(){
        boolean available;
        for(int i = 1; i < plugBoard.length; i++){//check all colors
            available = true;
            for(int j = 0; j < plugBoard.length && available; j++){//check the plugBoard
                if(plugBoard[j] == i){
                    available = false;
                }
            }
            if(available){
                return i;
            }
        }
        return 0;
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
    public void setPlugBoard(int[] plugBoard){
        this.plugBoard = plugBoard;

    }


    public void setDefaultConfiguration(){
        for(int i = 0; i < plugBoard.length; i++){
            plugBoard[i] = 0;
        }

        for(int i = 0 ; i < NUM_SELECTABLE_ROLLERS; i++){
            selectedRollers[i] = availableRollers[i];
            selectedRollers[i].setCurrentOffset(0);
            selectedRollers[i].setAdvancementOffset(0);
        }

    }
    private char crossCabledSwitches(char c){
        int code = plugBoard[c - 'A'];

        if(code != 0){
            for(int i = 0; i < ALPHABET_LENGTH; i++){
                if(plugBoard[i] == code && i != (c - 'A')){
                    return (char) (i + 'A');
                }
            }
        }

        return c;
    }
    private void advanceRolls(){
        for(int i = NUM_SELECTABLE_ROLLERS - 1; i > 0 ; i--){
            if(!selectedRollers[i - 1].isBlockingNextRoller()){
                selectedRollers[i].advance();
            }
            //System.out.print(selectedRollers[i].getCurrentOffset() + "-");
        }
        selectedRollers[0].advance();
        //System.out.println(selectedRollers[0].getCurrentOffset());
    }
    private char translate(char c){
        c = crossCabledSwitches(c);

        for(int i = 0 ; i < selectedRollers.length; i++){
            c = selectedRollers[i].commute(c);
            //System.out.println("finish to commute " + i +"  "+ c + "   " + (int)c);
        }

        c = commutator.commute(c);

        for(int i = selectedRollers.length - 1; i >= 0; i--){
            c = selectedRollers[i].commute(c);
        }

        c = crossCabledSwitches(c);

        return c;
        //return commutator.commute(c);
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
    public char findPlugBoardPair(Character letter){
        int letterColor = plugBoard[letter - 'A'];
        if(letterColor == 0){
            return letter;
        }

        for(int i = 0; i < plugBoard.length; i++){
            if(plugBoard[i] == letterColor && i != (letter - 'A')){
                return (char)(i +'A');
            }

        }
        return letter;
    }
    public void modifyPlugBoard(char letter, char switchedLetter){
        letter = Character.toUpperCase(letter);
        switchedLetter = Character.toUpperCase(switchedLetter);

        if(letter >= 'A' && letter <= 'Z' && switchedLetter >= 'A' && switchedLetter <= 'Z'){
            int letterColor = plugBoard[letter - 'A'];
            int switchedLetterColor = plugBoard[switchedLetter - 'A'];

            if(letter == switchedLetter){//delete the connections
                int foundConnection = 0;
                for(int i = 0; i < plugBoard.length && foundConnection < 2; i++){
                    if(plugBoard[i] == letterColor){
                        foundConnection++;
                        plugBoard[i] = 0;
                    }
                }
            }else{//change the connections
                char letterPair = findPlugBoardPair(letter);
                char switchedLetterPair = findPlugBoardPair(switchedLetter);

                if(letterColor == 0 && switchedLetterColor == 0){
                    letterColor = getAvailablePlugBoardCode();
                    plugBoard[letter - 'A'] = letterColor;
                    plugBoard[switchedLetter - 'A'] = letterColor;
                }else if(letterColor != 0 && switchedLetterColor != 0) {
                    plugBoard[letter - 'A'] = switchedLetterColor;
                    plugBoard[switchedLetter - 'A'] = letterColor;

                }else{
                    if(letterColor == 0){
                        plugBoard[letter - 'A'] = plugBoard[switchedLetterPair - 'A'];
                        plugBoard[switchedLetter - 'A'] = 0;
                    }else{
                        plugBoard[letter - 'A'] = 0;
                        plugBoard[switchedLetter - 'A'] = plugBoard[letterPair - 'A'];
                    }
                }

            }
        }

    }



}
