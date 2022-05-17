package it.polimi.ingsw;


import it.polimi.ingsw.Constants.*;
import it.polimi.ingsw.Exceptions.EndGameException;
import it.polimi.ingsw.Model.Model;

import java.util.Random;

public class Controller {
    private int firstPlayer;
    private final Model model;
    private boolean end;
    private final GameHandler gameHandler;

    /**
     * Controller's constructor
     * it initializes controller's attributes and calls model's constructor
     */
    public Controller(int numberPlayers, boolean gameMode, GameHandler gh) {
        int[] gameRules = new int[5];
        gameRules[0] = numberPlayers;
        //2 players rules
        if (numberPlayers == 2) {
            gameRules[1] = 7;
            gameRules[2] = 8;
            gameRules[3] = 0;
        }
        //3 players rules
        if (numberPlayers == 3) {
            gameRules[1] = 9;
            gameRules[2] = 6;
            gameRules[3] = 0;
        }
        //gameMode hard
        if (gameMode) {
            gameRules[4] = 1;
        } else {
            gameRules[4] = 0;
        }
        model = new Model(gameRules);
        gameHandler = gh;

        end = false;
    }

    /**
     * Calls the method round until we have a winner
     */
    public void startGame() {
        model.initializeGame();

        //selection of the first player
        Random rand = new Random();
        firstPlayer = rand.nextInt(model.gameRules[0]);
        gameHandler.messageToAll("the first player will be: " + firstPlayer);

        while (!end) {
            round();
        }

        gameHandler.messageToAll("END GAME, GG");
    }

    /**
     *
     */
    public void round() {
        int[] cards = new int[model.gameRules[0]];
        int[] playerOrder;

        //fill the clouds with the new students
        model.getBagNClouds().studentsBagToCloud();

        //play assistant card
        for (int i = 0; i < model.gameRules[0]; i++) {
            cards[i] = -1;
        }

        for (int i = 0; i < model.gameRules[0]; i++) {
            int player = (firstPlayer + i) % model.gameRules[0];
            gameHandler.newMessage(player, "Your assistant cards:");
            gameHandler.printAssistantCards(player);
            gameHandler.newMessage(player,"player " + player + " play your assistant card:");

            cards[player] = Integer.parseInt(gameHandler.requestInformation(ObjectsToSelect.ASSISTANT_CARD, cards, player));
        }
        playerOrder = model.getPlayerInteraction().playAssistantCard(cards);
        firstPlayer = playerOrder[0];

        gameHandler.messageToAll("the player order in this round will be:");
        for (int i = 0; i < model.gameRules[0]; i++) {
            gameHandler.messageToAll(playerOrder[i] + " ");
        }

        //action phase
        //for the number of players
        Colors color = null;
        String temp;
        int index;


        //for the number of players
        for (int i = 0; i < model.gameRules[0]; i++) {
            gameHandler.newMessage(playerOrder[i],"Player " + playerOrder[i] + ": it's your turn");

            //1) student movement
            //for the students to move
            for (int j = 0; j < model.gameRules[0] + 1; j++) {
                boolean result = true;
                //select the color of the student to move
                while (result) {
                    gameHandler.newMessage(playerOrder[i],"Select the color of the student you want to move:");
                    gameHandler.printStudents(playerOrder[i], playerOrder[i]);
                    temp = gameHandler.requestInformation(ObjectsToSelect.COLOR, cards, playerOrder[i]);
                    color = Colors.valueOf(temp.toUpperCase());
                    if (model.getPlayerInteraction().getPlayer(playerOrder[i]).getBoard().getStudEntrance().get(color) > 0) {
                        result = false;
                    } else {
                        gameHandler.newMessage(playerOrder[i], "you don't have " + color + " students in Entrance");
                    }
                }

                //select the place
                gameHandler.newMessage(playerOrder[i],"Where do you want to put the " + color + " student (Hall or Island)");
                temp = gameHandler.requestInformation(ObjectsToSelect.PLACE, cards, playerOrder[i]);
                while (temp.equals("false")) {
                    temp = gameHandler.requestInformation(ObjectsToSelect.PLACE, cards, playerOrder[i]);
                }
                if (temp.equalsIgnoreCase("Hall")) {
                    model.moveFromEntranceToHall(color, playerOrder[i]);
                    gameHandler.newMessage(playerOrder[i], "One " + color + " student moved from entrance to hall");
                    gameHandler.printTeachers(playerOrder[i]);
                }
                if (temp.equalsIgnoreCase("Island")) {
                    gameHandler.newMessage(playerOrder[i], "Select the island:");
                    temp = gameHandler.requestInformation(ObjectsToSelect.ISLAND, cards, playerOrder[i]);
                    index = Integer.parseInt(temp);
                    model.moveFromEntranceToIsland(color, playerOrder[i], index);
                    gameHandler.newMessage(playerOrder[i], "One " + color + " student moved from entrance to island " + index);
                    gameHandler.printIslands(playerOrder[i]);
                }
            }

            //MN movement
            gameHandler.newMessage(playerOrder[i], "Player" + playerOrder[i] + ": How many steps you want MN to do?");
            gameHandler.newMessage(playerOrder[i], "you can choose from 1 to " + model.getPlayerInteraction().getPlayer(playerOrder[i]).getAssistants().get(cards[playerOrder[i]]).getSteps());
            temp = gameHandler.requestInformation(ObjectsToSelect.STEPS, cards, playerOrder[i]);
            index = Integer.parseInt(temp);
            try {
                model.moveMN(index);
            } catch (EndGameException e) {
                end = true;
                int winner = model.getWinner();
                if (winner == -1) {
                    gameHandler.messageToAll("The game ended with a tie");
                } else {
                    gameHandler.messageToAll("The winner is player " + winner);
                }
                return;
            }
            gameHandler.newMessage(playerOrder[i], "MN moved " + index + "steps");

            //cloud selection
            gameHandler.newMessage(playerOrder[i], "You have to select the cloud with the students you want in your entrance");
            gameHandler.printClouds(playerOrder[i]);
            gameHandler.newMessage(playerOrder[i], "Select the cloud index:");
            temp = gameHandler.requestInformation(ObjectsToSelect.CLOUD, cards, playerOrder[i]);
            index = Integer.parseInt(temp);
            model.studentsCloudToEntrance(playerOrder[i], index);
            gameHandler.newMessage(playerOrder[i], "Cloud " + index + " students moved in the Entrance of player " + playerOrder[i]);

            if (model.endGame()) {
                end = true;
                int winner = model.getWinner();
                if (winner == -1) {
                    gameHandler.messageToAll("The game ended with a tie");
                } else {
                    gameHandler.messageToAll("The winner is player " + winner);
                }
            }
        }
    }


    /**
     * get methods
     */
    public Model getModel() {
        return model;
    }
}
