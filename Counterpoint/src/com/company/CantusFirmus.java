package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CantusFirmus {
    /** instance variables **/
    Random randomizer = new Random();
    private int[] arrayOfNotes;
    private String mode;
    private int[] scaleArray;


    /** setters and getters **/
    public int[] getScaleArray() { return scaleArray; }

    public void setScaleArray(int[] scale) { this.scaleArray = scale.clone(); }

    public int[] getArrayOfNotes() { return arrayOfNotes; }

    public void setArrayOfNotes(int[] arrayOfNotes) {
        this.arrayOfNotes = arrayOfNotes.clone();
    }
    public void setMode(String mode) { this.mode = mode; }

    public String getMode() { return mode; }


    /** constructors **/
    public CantusFirmus(String mode, int numberOfNotes) {
        this.mode = mode;

        initializeNotesArray(numberOfNotes);
        initializeScaleArray(mode);

        generateFirstNote();
        generateLastNote();
        generatePenultimateNote();
        // todo: generate thirdToLastNote for minor melodies, and alter "genereateAllNotes" and other methods. Make sure six is raised at cadence
        // todo: forbid repetition of the same note or pattern of notes more than two times. Maybe force a leap somewhere?
        // todo: I like the formula that puts a leap somewhere in the first 4 notes, and descends/ascends by step from there
        // todo: Sometimes the melody gets stuck in a range of 5 notes. Maybe a simple rule that says the highest note must be at least an octave higher than the lowest.
        generateAllNotes();

    }

    public CantusFirmus() { } // empty constructor ... needed this for building constructor in child class Counterpoint.



    /** methods extracted from constructor **/
    private void initializeScaleArray(String mode) {
        if (mode.equals("minor")) {
            scaleArray = new int[]{-5, -4, -2, 0, 2, 3, 5, 7, 8, 10, 12};
        } else {
            scaleArray = new int[]{-5, -3, -1, 0, 2, 4, 5, 7, 9, 11, 12};
        }
    }

    private void initializeNotesArray(int numberOfNotes) {
        if (numberOfNotes >4) {
            arrayOfNotes = new int[numberOfNotes];
        } else {
            arrayOfNotes = new int[9];
        }
    }

    public void generateFirstNote() {
        int[] firstNotesArray = new int[] {0,12};
        int randomIndex = randomizer.nextInt(2);
        int firstNote = firstNotesArray[randomIndex];
        arrayOfNotes[0] = firstNote;
    }

    public void generateLastNote() {
        int lastNote;
        if (getArrayOfNotes()[0] == 0) {
            lastNote = 0;
        } else {
            lastNote = 12;
        }
        arrayOfNotes[arrayOfNotes.length-1] = lastNote;
    }

    public void generatePenultimateNote() {
        if (arrayOfNotes[arrayOfNotes.length-1] == 0) {
            int[] firstNotesArray = new int[]{-1, 2};
            int randomIndex = randomizer.nextInt(2);
            int penultimateNote = firstNotesArray[randomIndex];
            arrayOfNotes[arrayOfNotes.length - 2] = penultimateNote;
        } else {
            int penultimateNote = 11;
            arrayOfNotes[arrayOfNotes.length - 2] = penultimateNote;
        }
    }


    public void generateAllNotes() {
        List<Integer> scaleList = initializeScaleList();
        /** Starting at index 1, generate a new note to replace the default 0 */
        arrayOfNotes[1] = generateRandomNote();
        boolean madeChange = true; // any time a change is made to a note, madeChange is toggled true.

        /** For loop iterates through notesArray, and at each new index, generates a new random note.
         * Then it checks that note against all of the voice leading rules **/
        for (int i = 1; i < arrayOfNotes.length - 2; i++) {

            /** If statement checks to see if a note was changed during the last iteration through the loop.
             * If a note was changed, instead of generating another new note, you should skip this if statement and go on to check the newly-changed note against all the rules.
             * The first time through the loop, madeChange is always true, as a new note at index 1 was generated before starting the loop.
             * if a note was NOT changed during the last iteration of the loop, that means the last note passed all of the voice-leading rules,
             * index i was incremented by 1 at the very end of the loop, and you will now go into the if statement, where the new note for current index is generated.*/
            if (!madeChange) {
                arrayOfNotes[i] = generateRandomNote();
            }
            madeChange = false; // before checking each rule, madeChange is reset to false. Every time through the beginning of the loop,

            /** Following methods check that the note at your current index doesn't break any voice-leading rules.
             * If a rule is broken, a new note is generated, and madeChange = true **/
            madeChange = hasObliqueMotion(i) || hasMultipleLeaps(scaleList, i) || isTooLargeLeap(i,isIllegalLeap(i))
                    || isIllegalDescendingLeap(scaleList, i) || isIllegalAscendingLeap(scaleList, i); // returns true

            /** If a change was made (madeChange = true) when checking any of the above rules, i is decremented.
             * Next time through the loop, you won't go to the next index. Instead, you will go through the loop on the current one more time,
             * in order to check that the newly-selected note passes all the rules in the loop.
             * The loop will iterate through on the same index until the note at that index passes all of the rules**/
            i = isChangeMade(madeChange, i); //
        }

        /** After all notes have been selected, check to make sure the whole Cantus--including first and last notes--passes all the voice leading rules.
         * If any one rule fails, generateAllNotes() is called recursively and the inner notes are all selected again from scratch,
         * and the method will continue to be called recursively, until the Cantus passes all rules. **/
        replaceIllegalApex(); // this method is called after all notes have been chosen. It will replace illegal climax notes 2 or 11 with 0 and 12, respectively
        startOver();
    }

    /** Methods that check all the rules **/
    private boolean hasObliqueMotion(int i) {
        if (isObliqueMotion(i)) {
            arrayOfNotes[i] = generateRandomNote();
            return true;
        }
        return false;
    }

    private boolean hasMultipleLeaps(List<Integer> scaleList, int i) {
        if (hasTooManyLeaps(i)) {
            int previousNote = arrayOfNotes[i -1];
            int indexOfOneNoteAgoInScale = scaleList.indexOf(previousNote);
            if (indexOfOneNoteAgoInScale < scaleArray.length-1) {
                arrayOfNotes[i] = scaleList.get(indexOfOneNoteAgoInScale + 1);
                return true;
            } else {
                arrayOfNotes[i] = scaleList.get(indexOfOneNoteAgoInScale + -1);
                return true;
            }
        }
        return false;
    }

    private boolean isTooLargeLeap(int i, boolean illegalLeap) {
        if (illegalLeap) {
            arrayOfNotes[i] = generateRandomNote();
            return true;
        }
        return false;
    }

    private boolean isIllegalDescendingLeap(List<Integer> scaleList, int i) {
        if (i > 1 && isLargeLeapDown(i) && !departedByUpwardStep(i)) {
            int indexOfLastNote = scaleList.indexOf(arrayOfNotes[i - 1]);
            arrayOfNotes[i] = scaleArray[indexOfLastNote + 1];// that index in the scale of possible notes + 1
            return true;
        }
        return false;
    }

    private boolean isIllegalAscendingLeap(List<Integer> scaleList, int i) {
        if (i > 1 && isLargeLeapUp(i) && !departedByDownwardStep(i)) {
            try {
                int indexOfLastNote = scaleList.indexOf(arrayOfNotes[i - 1]);
                arrayOfNotes[i] = scaleArray[indexOfLastNote - 1];// that index in the scale of possible notes - 1
            } catch (Exception e) {
                System.out.println("You've caught an exception at " + arrayOfNotes[i] + " trying to get the -1 index of the scale array");
            }
            return true;
        }
        return false;
    }


    public List initializeScaleList() {
        List<Integer> scaleList = new ArrayList<>(); // create a LIST of array notes so I can get the index notes to use in the treatment of large leaps.
        for (int note : scaleArray) {
            scaleList.add(note);
        }
        return scaleList;
    }
    public int generateRandomNote() {
        Random indexRandomizer = new Random();
        int randomIndex = indexRandomizer.nextInt(scaleArray.length); // get a random index, between 0 and the length of the scaleArray
        int nextNote = scaleArray[randomIndex];
        return nextNote;

    }

    public boolean isObliqueMotion(int currentIndex) {

        //check if current note is same as last note
        boolean isObliqueMotion = false;
        if (arrayOfNotes[currentIndex] == arrayOfNotes[currentIndex-1]) {
            isObliqueMotion = true;
        }

        return isObliqueMotion;
    }

    public boolean hasTooManyLeaps(int indexOfCurrentNote) {
        boolean hasTooManyLeaps = false;
        int numOfLeaps = 0;
        int currentNote = arrayOfNotes[indexOfCurrentNote];
        int lastNote = arrayOfNotes[indexOfCurrentNote-1];
        int sizeOfLeap = currentNote - lastNote;

        for (int i = 1; i<=indexOfCurrentNote; i++) {
            boolean isLeap = false;
            if (arrayOfNotes[i] - arrayOfNotes[i-1] >=5) {
                isLeap = true;
                numOfLeaps++;
            }
            if (arrayOfNotes[i] - arrayOfNotes[i-1] <=-5) {
                sizeOfLeap = arrayOfNotes[i] - arrayOfNotes[i-1];
                isLeap = true;
                numOfLeaps++;
            }
        }

        if (numOfLeaps > 1) {
            hasTooManyLeaps = true;
        }
        return hasTooManyLeaps;
    }

    public boolean isLargeLeapUp(int currentIndex) {
        boolean isLargeLeapUp = false;
        try {
            if (arrayOfNotes[currentIndex - 1] - arrayOfNotes[currentIndex - 2] > 5) { // if last melodic interval was a leap up greater than a fifth
                isLargeLeapUp = true;
            }
        } catch (Exception e) {
            System.out.println("You've caught an exception while checking for large Leap Up: " + e);
        }
        return isLargeLeapUp;

    }

    public boolean isLargeLeapDown(int currentIndex) {
        boolean isLargeLeapDown = false;
        try {
            if (arrayOfNotes[currentIndex - 1] - arrayOfNotes[currentIndex - 2] < -5) {
                isLargeLeapDown = true;
            }
        } catch (Exception e) {
            System.out.println("You've caught an exception while checking isLargeLeapDown: " + e);
        }
        return isLargeLeapDown;
    }


    public boolean isIllegalLeap(int currentIndex) {
        try {
            if (arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] > 12 || arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] < -12) {
                return true;
            } else if (arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] == 11 ||
                    arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] == 10 ||
                    arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] == -11 ||
                    arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] == -10 ||
                    arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] == 6 ||
                    arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex - 1] == -6) {
                return true;
            }
        } catch (Exception e) {
            System.out.print("You've had an exception " + e);
        }
        return false;
    }

    public void replaceIllegalApex() {
        int[] arrayMinusFirstLast = new int[arrayOfNotes.length - 3];
        for (int i = 1; i <= arrayMinusFirstLast.length; i++) {
            arrayMinusFirstLast[i - 1] = arrayOfNotes[i];
        }
        List<Integer> listOfNotes = new ArrayList<>();
        Arrays.sort(arrayMinusFirstLast);
        if (arrayMinusFirstLast[0] == 2) {
            for (int note : arrayOfNotes) {
                listOfNotes.add(note);
            }
            int lowestNoteIndex = listOfNotes.indexOf(2);
            arrayOfNotes[lowestNoteIndex] = 0;

        }


    }
//    public void replaceIllegalApex() { // do this at the end, but before checking all other voice leading errors.
//        boolean hasLegalApex = false;
//        int[] arrayMinusFirstLast = new int[arrayOfNotes.length - 2];
//        for (int i = 1; i <= arrayMinusFirstLast.length; i++) {
//            arrayMinusFirstLast[i - 1] = arrayOfNotes[i];
//        }
//        List<Integer> listOfNotes = new ArrayList<>();
//        Arrays.sort(arrayMinusFirstLast);
//        if (arrayOfNotes[0] == 12) {
////            if (arrayMinusFirstLast[0] != arrayMinusFirstLast[1]) { // if the lowest note is not equal to the second lowest note
//            for (int note : arrayMinusFirstLast) {
//                listOfNotes.add(note);
//            }
//            if (listOfNotes.get(0) == 2) {
//
//                List<Integer> listOfAllNotes = new ArrayList<>();
//                for (int note : arrayOfNotes) {
//                    listOfAllNotes.add(note);
//                }
//                int indexOfLowestNote = listOfAllNotes.indexOf(2);
//                arrayOfNotes[indexOfLowestNote] = 0; // change 2 to 0
//            }
//        } else { // if the first note is 0
//
//        }
//    }


    public boolean departedByDownwardStep(int currentIndex) {
        boolean departedByDownwardStep = false;
        if (arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex-1] == -1 || (arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex-1] == -2)) {
            departedByDownwardStep = true;
        }
        return departedByDownwardStep;
    }


    public boolean departedByUpwardStep(int currentIndex) {
        boolean departedByUpwardStep = false;
        if (arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex-1] == 1 || (arrayOfNotes[currentIndex] - arrayOfNotes[currentIndex-1] == 2)) {
            departedByUpwardStep = true;
        }
        return departedByUpwardStep;
    }

//    public boolean hasOneApex() {
//        int[] arrayMinusFirstLast = new int[arrayOfNotes.length - 2];
//        for (int i = 1; i < arrayMinusFirstLast.length; i++) {
//            arrayMinusFirstLast[i - 1] = arrayOfNotes[i];
//        }
//        Arrays.sort(arrayMinusFirstLast); // sort the list from lowest to highest
//        if (arrayOfNotes[0] == 12) {
//            // then the apex should be lower than 12. If the lowest note is repeated, has more than one apex
//            if (arrayMinusFirstLast[0] == arrayMinusFirstLast[1]) {
//                return false;
//            }
//        } else { // if the starting note is 0
//            if (arrayMinusFirstLast[arrayMinusFirstLast.length - 1] == arrayMinusFirstLast[arrayMinusFirstLast.length - 2]) {
//                return false;
//            }
//        }
//        return true;
//    }

    /** restored version of public boolean hasOneApex()
     * version above isn't working so well.
     */
        public boolean hasOneApex() {
            boolean hasOneApex = false;
            int[] arrayMinusFirstLast = new int[arrayOfNotes.length-2];
            for (int i = 1; i<arrayMinusFirstLast.length; i++) {
                arrayMinusFirstLast[i-1] = arrayOfNotes[i];
            }

            List<Integer> listOfNotes = new ArrayList<>();
            Arrays.sort(arrayMinusFirstLast);
            if (arrayOfNotes[0] == 12) { // if the starting note is 12
                if (arrayMinusFirstLast[0] != arrayMinusFirstLast[1]) { // if the lowest note is not equal to the second lowest note
                    for (int note : arrayMinusFirstLast) {
                        listOfNotes.add(note);
                    }
                    if (!listOfNotes.contains(12)){
                        hasOneApex = true;
                    }
                }
            } else { // if the starting note is 0
                if (arrayMinusFirstLast[arrayMinusFirstLast.length-1] != arrayMinusFirstLast[arrayMinusFirstLast.length-2]) {
                    if (arrayMinusFirstLast[0] != arrayMinusFirstLast[1]) {
                        hasOneApex = true;
                    }
                }
            }

            return hasOneApex;
        }

    public boolean leapsIllegallyToLastNote() { // todo: make leaping a tritone also illegal.
        boolean hasIllegalLeap = false;
        if (arrayOfNotes[arrayOfNotes.length-2] == 11) {
            if (arrayOfNotes[arrayOfNotes.length-3] < 7) { // if the leap up to 7 is greater than a third
                hasIllegalLeap = true;
            }
        } else if (arrayOfNotes[arrayOfNotes.length-2] == -1) { // when penultimate note is -1
            if (arrayOfNotes[arrayOfNotes.length-3] > 7 || arrayOfNotes[arrayOfNotes.length-3] == 5) { // leap greater than fifth or leap of tritone
                hasIllegalLeap = true;
            }
        } else {
            if (arrayOfNotes[arrayOfNotes.length-3] > 6) {
                hasIllegalLeap = true;
            }
        }
        return hasIllegalLeap;
    }

    public boolean hasIllegalDeparture() {
        boolean lastNotesIncludeIllegalDeparture = false;
        if (arrayOfNotes[arrayOfNotes.length-3] - arrayOfNotes[arrayOfNotes.length-4] >= 5) {
            if (arrayOfNotes[arrayOfNotes.length-3] > arrayOfNotes[arrayOfNotes.length-4]) {
                if (arrayOfNotes[arrayOfNotes.length-2] > arrayOfNotes[arrayOfNotes.length-3]) {
                    lastNotesIncludeIllegalDeparture = true;

                }
            } else {
                if (arrayOfNotes[arrayOfNotes.length-2] < arrayOfNotes[arrayOfNotes.length-3]) {
                        lastNotesIncludeIllegalDeparture = true;
                }
            }
        }
        return lastNotesIncludeIllegalDeparture;
    }

    private int isChangeMade(boolean madeChange, int i) {
        if (madeChange) {
            i--; // if we made a change.. lower
        }
        return i;
    }


    private void startOver() {
        while (!hasOneApex() || leapsIllegallyToLastNote() || isObliqueMotion(getArrayOfNotes().length-2) || hasIllegalDeparture() ){
            generateAllNotes();
        }
    }








}











