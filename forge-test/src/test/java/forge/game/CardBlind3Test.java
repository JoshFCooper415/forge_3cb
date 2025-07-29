package forge.game;

import forge.gamesimulationtests.util.LobbyPlayerForTests;
import forge.game.player.Player;
import forge.game.zone.ZoneType;
import forge.deck.Deck;
import forge.game.player.RegisteredPlayer;
import forge.StaticData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CardBlind3Test {

    @Test
    public void testInitialGameState() {
        // Setup players and decks
        List<RegisteredPlayer> players = new ArrayList<>();
        Deck deck1 = new Deck();
        deck1.getMain().add(StaticData.instance().getCommonCards().getCard("Grizzly Bears"), 3);
        players.add(new RegisteredPlayer(deck1).setPlayer(new LobbyPlayerForTests("Player 1")));

        Deck deck2 = new Deck();
        deck2.getMain().add(StaticData.instance().getCommonCards().getCard("Ornithopter"), 3);
        players.add(new RegisteredPlayer(deck2).setPlayer(new LobbyPlayerForTests("Player 2")));

        // Setup game rules
        GameRules rules = new GameRules(GameType.CardBlind3);
        Match match = new Match(rules, players, "Test Match");
        Game game = match.createGame();
        match.startGame(game);

        // Verify initial game state
        for (Player p : game.getPlayers()) {
            Assert.assertEquals(3, p.getCardsIn(ZoneType.Hand).size());
            Assert.assertEquals(0, p.getCardsIn(ZoneType.Library).size());
        }
    }

    @Test
    public void testDrawFromEmptyLibrary() {
        // Setup players and decks
        List<RegisteredPlayer> players = new ArrayList<>();
        Deck deck1 = new Deck();
        deck1.getMain().add(StaticData.instance().getCommonCards().getCard("Grizzly Bears"), 3);
        players.add(new RegisteredPlayer(deck1).setPlayer(new LobbyPlayerForTests("Player 1")));

        Deck deck2 = new Deck();
        deck2.getMain().add(StaticData.instance().getCommonCards().getCard("Ornithopter"), 3);
        players.add(new RegisteredPlayer(deck2).setPlayer(new LobbyPlayerForTests("Player 2")));

        // Setup game rules
        GameRules rules = new GameRules(GameType.CardBlind3);
        Match match = new Match(rules, players, "Test Match");
        Game game = match.createGame();
        match.startGame(game);

        // Verify that drawing from an empty library does not cause a loss
        Player player1 = game.getPlayers().get(0);
        player1.drawCards(1);
        Assert.assertFalse(player1.hasLost());
    }

    @Test
    public void testPerfectInformation() {
        // Setup players and decks
        List<RegisteredPlayer> players = new ArrayList<>();
        Deck deck1 = new Deck();
        deck1.getMain().add(StaticData.instance().getCommonCards().getCard("Grizzly Bears"), 3);
        players.add(new RegisteredPlayer(deck1).setPlayer(new LobbyPlayerForTests("Player 1")));

        Deck deck2 = new Deck();
        deck2.getMain().add(StaticData.instance().getCommonCards().getCard("Ornithopter"), 3);
        players.add(new RegisteredPlayer(deck2).setPlayer(new LobbyPlayerForTests("Player 2")));

        // Setup game rules
        GameRules rules = new GameRules(GameType.CardBlind3);
        Match match = new Match(rules, players, "Test Match");
        Game game = match.createGame();
        match.startGame(game);

        // Verify that each player can see the other's hand
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        Assert.assertEquals(3, player1.getOpponents().get(0).getCardsIn(ZoneType.Hand).size());
        Assert.assertEquals(3, player2.getOpponents().get(0).getCardsIn(ZoneType.Hand).size());
    }
}
