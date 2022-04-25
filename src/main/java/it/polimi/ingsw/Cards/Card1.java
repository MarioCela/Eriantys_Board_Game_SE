package it.polimi.ingsw.Cards;

import it.polimi.ingsw.*;
import it.polimi.ingsw.Constants.Colors;
import it.polimi.ingsw.Constants.Constants;
import it.polimi.ingsw.Constants.Indexes;

import java.util.HashMap;
import java.util.Map;


public class Card1 extends CharacterCards {
    private final hasAddToIsland hasAddToIsland;
    private final Map<Colors, Integer> students;
    private final BagNClouds bagNClouds;

    /**
     * Card1 constructor
     *
     * @param bagNClouds bagNClouds reference used to draw students in useEffect
     * @param students   students located on the card
     */
    public Card1(int cost, IslandInteraction islandInteraction, BagNClouds bagNClouds, Map<Colors, Integer> students) {
        super(cost);
        this.hasAddToIsland = islandInteraction;
        this.bagNClouds = bagNClouds;

        this.students = new HashMap<>();
        for (Colors c : Colors.values()) {
            this.students.put(c, 0);
        }

        for (Colors c : Colors.values()) {
            this.students.put(c, students.get(c));
        }
    }

    /**
     * The method removes the student in input, puts it on the chosen island
     * and substitutes it with a new student drawn from the bag
     *
     * @param variables     island's index where the student will be placed
     * @param studentColor  not used
     * @param studentArray1 student that will be removed from the card and will be put on the chosen island
     * @param studentArray2 not used
     */
    @Override
    public boolean useEffect(Map<Indexes, Integer> variables, Colors studentColor, Map<Colors, Integer> studentArray1, Map<Colors, Integer> studentArray2) {
        //removing student chosen from the card
        for (Colors c : Colors.values()) {
            students.put(c, students.get(c) - studentArray1.get(c));
        }

        //adding student to the chosen island
        this.hasAddToIsland.addToIsland(variables.get(Indexes.ISLAND_INDEX), studentArray1);

        //drawing a student from the bag and putting it on the card
        Map<Colors, Integer> temp = bagNClouds.drawStudents(Constants.CARD1_STUDENTS_TO_MOVE);
        for (Colors c : Colors.values()) {
            students.put(c, students.get(c) + temp.get(c));
        }

        return true;
    }
}
