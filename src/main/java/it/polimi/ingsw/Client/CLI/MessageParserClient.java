package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.CLI.Parser.*;
import it.polimi.ingsw.Constants.MessageType;
import java.util.HashMap;
import java.util.Map;

public class MessageParserClient {
    private final Map<String, Parser> parseMessages;

    public MessageParserClient() {
        this.parseMessages = new HashMap<>();

        Parser allIslandsParser = new AllIslandsParser();
        this.parseMessages.put(MessageType.PRINT_ALL_ISLANDS.getType(), allIslandsParser);

        Parser islandParser = new IslandParser();
        this.parseMessages.put(MessageType.PRINT_ISLAND.getType(), islandParser);

        Parser allBoardsParser = new AllBoardsParser();
        this.parseMessages.put(MessageType.PRINT_ALL_BOARDS.getType(), allBoardsParser);

        Parser boardParser = new BoardParser();
        this.parseMessages.put(MessageType.PRINT_BOARD.getType(), boardParser);

        Parser teachersParser = new TeachersParser();
        this.parseMessages.put(MessageType.PRINT_TEACHERS.getType(), teachersParser);

        Parser cloudsParser = new CloudsParser();
        this.parseMessages.put(MessageType.PRINT_ALL_CLOUDS.getType(), cloudsParser);

        Parser assistantCards = new AssistantCardsParser();
        this.parseMessages.put(MessageType.PRINT_ASSISTANT_CARDS.getType(), assistantCards);

        Parser defaultParser = new DefaultParser();
        this.parseMessages.put(MessageType.EASY_MESSAGE.getType(), defaultParser);
    }

    public Map<String, Parser> getParseMessages() {
        return parseMessages;
    }
}