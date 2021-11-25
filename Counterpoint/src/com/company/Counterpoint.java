package com.company;

import java.util.ArrayList;
import java.util.List;

public class Counterpoint extends CantusFirmus{
    private CantusFirmus cantusFirmus;


    //constructor
    public Counterpoint(CantusFirmus cantusFirmus){
        /** set the CantusFirmus **/
        this.cantusFirmus = cantusFirmus;

        /** set the length of the notes array**/
        setArrayOfNotes(new int[cantusFirmus.getArrayOfNotes().length]); // initialize the CP arrayOfNotes with an empty array, length matching that of the passed-in cantus.


        /** set the mode **/
        setMode(cantusFirmus.getMode());
        if (getMode().equals("minor")) {
            setScaleArray(new int[] {-5, -4, -2, 0, 2, 3, 5, 7, 8, 10, 12});
        } else {
            setScaleArray( new int[] {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19});
        }

        /** set the notes **/
        generateFirstNote();
        generateLastNote();
        generatePenultimateNote();
        generateAllNotes();
    }

    @Override
    public void generateFirstNote() {
         // initialize array of cp notes with length of CF;
        int[] firstNotesArray = new int[] {0, 7, 12};

        if (cantusFirmus.getArrayOfNotes()[0] == 0) { // if CF starts with zero
            int randomIndex = randomizer.nextInt(firstNotesArray.length);
            getArrayOfNotes()[0] = firstNotesArray[randomIndex];
        } else {
            getArrayOfNotes()[0] = 12;
        }
    }

    public void setSecondToLastNote(int secondToLast) {
        getArrayOfNotes()[getArrayOfNotes().length - 2] = secondToLast;
    }


    @Override
    public void generatePenultimateNote() {
        int[] arrayOfPenultimateNotes;
        int randomIndex;
        if (cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == 9 || cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == -1) { // and if CF 2nd to last note is 7
            arrayOfPenultimateNotes = new int[]{2, 14};
            randomIndex = randomizer.nextInt(arrayOfPenultimateNotes.length);
            getArrayOfNotes()[getArrayOfNotes().length - 2] = arrayOfPenultimateNotes[randomIndex]; // second to last note is 2 or 14
        } else if (cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == 2 || cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == 14) { // or if CF 2nd to last note is 2
            arrayOfPenultimateNotes = new int[]{-1, 11};
            randomIndex = randomizer.nextInt(arrayOfPenultimateNotes.length);
            getArrayOfNotes()[getArrayOfNotes().length - 2] = arrayOfPenultimateNotes[randomIndex];// second to last note is 7;
        }
    }

    public void generateLastNote() {
        int penultimateNote = getArrayOfNotes()[getArrayOfNotes().length - 2];
        if (penultimateNote == 2 || penultimateNote == -1) {
            getArrayOfNotes()[getArrayOfNotes().length - 1] = 0;
        } else if (penultimateNote == 14 || penultimateNote == 11) {
            getArrayOfNotes()[getArrayOfNotes().length - 1] = 12;
        }
    }

    public void generateThirdToLastNote() { // make sure that third to last note is 6^ if 2nd to last is 7^
        int penultimateNote = getArrayOfNotes()[getArrayOfNotes().length - 2];
        if (penultimateNote == 11) {
            getArrayOfNotes()[getArrayOfNotes().length-3] = 9;
        } else if (penultimateNote == -1) {
            getArrayOfNotes()[getArrayOfNotes().length-3] = -3;
        }
    }

    public int randomIndex(int[] legalIntervals) {
        return randomizer.nextInt(legalIntervals.length);
    }


    public int calculateLastInterval(int counterpointNote, int cantusNote) {
        return counterpointNote - cantusNote;
    }

    public int calculateCurrentInterval(int counterpointNote, int cantusNote) {
        return counterpointNote - cantusNote;
    }

    public int generateLegalInterval(int counterpointNote, int cantusNote, int i, int[] availableNotes) {
        // it might be impossible to write counterpoint that adheres to every rule, if you aren't able to go back and make changes.
        // Figure out a way to let the computer know when it should start over.
        // For example, "If this rule can't be followed, then start from scratch"
        // In the meantime, put the least-important rules first, and then the most important rules at the end.
        // Maybe randomly isn't the way to generate notes

        // if statement prevents voice crossing.
        if (calculateCurrentInterval(counterpointNote, cantusNote) <0) {
            getArrayOfNotes()[i] = getScaleArray()[randomIndex(availableNotes)]; // generate a new random note
            generateLegalInterval(getArrayOfNotes()[i], cantusNote, i, availableNotes); // run the method again
        }


        // Enforces law of departure // presently doesn't seem to be working
        if (i > 1) {
            if (getArrayOfNotes()[i-1] - getArrayOfNotes()[i - 2] >= 5) {
                List<Integer> listOfAvailables = new ArrayList<>();
                for (int j = 0; j<getArrayOfNotes().length; j++) {
                    listOfAvailables.add(getScaleArray()[j]);
                }
                int indexOfCurrentNote = listOfAvailables.indexOf(getArrayOfNotes()[i]);
                if (indexOfCurrentNote > 1 && indexOfCurrentNote < getArrayOfNotes().length-1) {
                    getArrayOfNotes()[i] = listOfAvailables.get(indexOfCurrentNote - 1);
                }
            } else if (getArrayOfNotes()[i-1] - getArrayOfNotes()[i - 2] <= -5) {
                List<Integer> listOfAvailables = new ArrayList<>();
                for (int j = 0; j<getArrayOfNotes().length; j++) {
                    listOfAvailables.add(getScaleArray()[j]);
                }
                int indexOfCurrentNote = listOfAvailables.indexOf(getArrayOfNotes()[i]);
                if (indexOfCurrentNote > 1 & indexOfCurrentNote < getArrayOfNotes().length-1) {
                    getArrayOfNotes()[i] = listOfAvailables.get(indexOfCurrentNote + 1);


                }
            }
        }


        // switch case checks for previous intervals to prevent parallel perfect intervals.
        switch (calculateLastInterval(getArrayOfNotes()[i - 1], cantusFirmus.getArrayOfNotes()[i - 1])) {
            case 7: // if the last interval was a perfect fifth
                if (counterpointNote - cantusNote == 7) {  // if the current interval is a fifth
                    getArrayOfNotes()[i] = availableNotes[randomIndex(availableNotes)]; // generate a new random note
                    generateLegalInterval(getArrayOfNotes()[i], cantusNote, i, availableNotes); // run the method again
                    break;
                }
            case 12: // if the last interval was a perfect octave
                if (counterpointNote - cantusNote == 12) {  // if the current interval is an octave
                    getArrayOfNotes()[i] = availableNotes[randomIndex(availableNotes)]; // generate a new random note
                    generateLegalInterval(getArrayOfNotes()[i], cantusNote, i, availableNotes); // run the method again.
                    break;
                }
            case 0: // if the last interval was a perfect unison
                if (counterpointNote - cantusNote == 0) {  // if the current interval is a unison
                    getArrayOfNotes()[i] = getScaleArray()[randomIndex(availableNotes)]; // generate a new random note
                    generateLegalInterval(getArrayOfNotes()[i], cantusNote, i, availableNotes); // run the method again.
                    break; // forbidding parallel unisons caused infinite recursion at 15 notes. Stack Overflow exception.
                    //need to come up with a rule that says if you're boxed in and have tried 15 times, then just pick a random note. Or change the previous note...
                }
        }

        //switch case checks for illegal dissonances
        switch (calculateCurrentInterval(counterpointNote, cantusNote)) {
            case 1: // m2
            case 2: // M2
            case 5: // P4
            case 6: // TT
            case 10: // m7
            case 11: // M7
            case 13: // m9
            case 14: // M9
            case 17: // P4 (compound)
            case 18: // just too wide
            case 19: // too wide
                getArrayOfNotes()[i] = getScaleArray()[randomIndex(availableNotes)]; // generate a new random note
                generateLegalInterval(getArrayOfNotes()[i], cantusNote, i, availableNotes); // run the method again
                break;
        }

        while (!hasOneApex() || leapsIllegallyToLastNote() || isObliqueMotion(getArrayOfNotes().length-2) || hasIllegalDeparture() ){
            generateLegalInterval(counterpointNote,cantusNote,i,availableNotes);
        }
        return getArrayOfNotes()[i];
    }

    public boolean hasCorrectCadence() {
        boolean hasCorrectCadence = true;
        if (cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == 11) {
            if (getArrayOfNotes()[getArrayOfNotes().length-2] != 14) {
                hasCorrectCadence = false;
            }
        } else if (cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == -1) {
            if (getArrayOfNotes()[getArrayOfNotes().length-2] != 2 || getArrayOfNotes()[getArrayOfNotes().length-2] != 14) {
                hasCorrectCadence = false;
            }
        } else if (cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == 2) {
            if (getArrayOfNotes()[getArrayOfNotes().length-2] != 11) {
                hasCorrectCadence = false;
            }
        }
        if (getArrayOfNotes()[0] == 0) {
            if (cantusFirmus.getArrayOfNotes()[getArrayOfNotes().length-2] == -1) {
                if (getArrayOfNotes()[getArrayOfNotes().length-2] !=2) {
                    hasCorrectCadence = false;
                }
            }
        }
        return hasCorrectCadence();

    }

    public void generateNextNote() {
        int[] cantusNotes = cantusFirmus.getArrayOfNotes();
        int randomIndex = 0;
        try {
            if (cantusFirmus.getMode().equals("minor")) {
                int[] availableNotes = super.getScaleArray(); // don't forget the super keyword
                int[] availableNotesAsInts = new int[availableNotes.length]; // new int with length of available notes, still empty at this point.
                for (int j = 0; j < availableNotes.length; j++) { // add all Integers to new int array
                    availableNotesAsInts[j] = (int) availableNotes[j];
                }
//            for (int j = 0; j < availableNotes.length; j++) { // why did I need this loop
                if (getArrayOfNotes()[getArrayOfNotes().length - 3] == 9 || getArrayOfNotes()[getArrayOfNotes().length - 3] == -3) { //if 3rd-to-last is raised 6^
                    for (int i = 1; i < getArrayOfNotes().length - 3; i++) {
                        getArrayOfNotes()[i] = availableNotes[randomIndex(availableNotesAsInts)]; //   needs to be ints array, not Integer[])]; // generate a random note
                        getArrayOfNotes()[i] = generateLegalInterval(getArrayOfNotes()[i], cantusFirmus.getArrayOfNotes()[i], i, availableNotesAsInts); // get a
                    }
                } else {
                    for (int i = 1; i < getArrayOfNotes().length - 2; i++) {
                        getArrayOfNotes()[i] = availableNotes[randomIndex(availableNotesAsInts)]; // generate a random note
                        getArrayOfNotes()[i] = generateLegalInterval(getArrayOfNotes()[i], cantusFirmus.getArrayOfNotes()[i], i, availableNotesAsInts);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("You threw an exception: " + e);
        }
    }

}




