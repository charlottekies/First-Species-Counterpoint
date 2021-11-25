package com.company;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {


        CantusFirmus cantusFirmus = newCantusFirmus();
        while (askToPlayAgain()) {
            cantusFirmus = newCantusFirmus();
        }
//        newCounterpoint(cantusFirmus);

    }

        /** Methods **/

    public static void newCounterpoint(CantusFirmus cantusFirmus) {
        Counterpoint myCounterpoint = new Counterpoint(cantusFirmus);

        System.out.print("your counterpoint is:  [");
        for (int i = 0; i < myCounterpoint.getArrayOfNotes().length - 1; i++) {
            System.out.print(myCounterpoint.getArrayOfNotes()[i] + ", ");
        }
        System.out.println(cantusFirmus.getArrayOfNotes()[cantusFirmus.getArrayOfNotes().length - 1] + "]");

    }

    public static int askForNotes() {
        Scanner input = new Scanner(System.in);
        System.out.print("How many notes is your Cantus Firmus? ");
        int howManyNotes = 0;
        while (true) {
            try {
                String howManyNotesString = input.nextLine();
                howManyNotes = Integer.parseInt(howManyNotesString);
                if ((howManyNotes <0) || (howManyNotes >15)) {
                    System.out.println("I'll just go with 10...");
                    return 10;
                } else {
                    return howManyNotes;
                }
            } catch (Exception e) {
                System.out.print("Could you enter an integer less than 16? ");
            }
        }
    }

    public static String askForMode() {
        Scanner input = new Scanner(System.in);
        System.out.print("Is your Cantus Firmus in major, or minor? ");
        String mode = input.nextLine();
        return mode;
    }

    public static boolean askToPlayAgain() {
        Scanner input = new Scanner(System.in);
        boolean wantsToPlayAgain = false;
        System.out.print("Would you like to play again? type yes or no: ");
        if (input.nextLine().equals("yes")) {
            wantsToPlayAgain = true;
        }
        return wantsToPlayAgain;
    }

    public static CantusFirmus newCantusFirmus() {
        /** create the Cantus Firmus **/
        CantusFirmus cantusFirmus;
        boolean isMajor = (askForMode().equals("major"));
        if (isMajor) {
            cantusFirmus = new CantusFirmus("major", askForNotes());
        } else {
            cantusFirmus = new CantusFirmus("minor", askForNotes());
        }

        /** create and print the Counterpoint **/
        newCounterpoint(cantusFirmus);

        /** Print the cantus **/
        System.out.print("your Cantus Firmus is: [");
        for (int i = 0; i < cantusFirmus.getArrayOfNotes().length - 1; i++) {
            System.out.print(cantusFirmus.getArrayOfNotes()[i] + ", ");
        }
        System.out.println(cantusFirmus.getArrayOfNotes()[cantusFirmus.getArrayOfNotes().length - 1] + "]");
//        System.out.println("This melody " + ((cantusFirmus.hasOneApex()) ? "has one apex" : "has repeating extremities"));
//        System.out.println("This melody " + ((cantusFirmus.leapsIllegallyToLastNote()) ? "leaps illegally to the penultimate note" : "does not leap illegally to the penultimate note"));
//        System.out.println("This melody " + ((cantusFirmus.isObliqueMotion(cantusFirmus.getArrayOfNotes().length - 2)) ? "contains illegal oblique motion." : "does not have illegal oblique motion."));

        return cantusFirmus;
    }
}

