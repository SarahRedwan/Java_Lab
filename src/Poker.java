import java.util.*;

public class Poker {

    static class Card {

        String rank;
        String suit;

        Card(String rank, String suit) {
            this.rank = rank;
            this.suit = suit;
        }

        public String toString() {
            return rank + suit;
        }
    }

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        playGame();
    }

    static void playGame() {

        String[] ranks = {
                "A", "2", "3", "4", "5", "6", "7",
                "8", "9", "10", "J", "Q", "K"
        };

        String[] suits = {"♠", "♥", "♦", "♣"};

        ArrayList<Card> deck = new ArrayList<>();

        for (String r : ranks) {
            for (String s : suits) {
                deck.add(new Card(r, s));
            }
        }

        Collections.shuffle(deck);
        ArrayList<Card> player = new ArrayList<>();
        ArrayList<Card> cpu = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            player.add(deck.remove(0));
            cpu.add(deck.remove(0));
        }

        System.out.println("\nPLAYER HAND: " + player);
        System.out.println("CPU HAND   : " + cpu);

        String p = evaluate(player);
        String c = evaluate(cpu);

        System.out.println("\nPlayer: " + p);
        System.out.println("CPU   : " + c);

        if (rank(p) > rank(c)) {

            System.out.println("\nPLAYER WINS");

        } else if (rank(c) > rank(p)) {

            System.out.println("\nCPU WINS");

        } else {

            System.out.println("\nTIE!");

            waitForShuffle();
        }
    }

    static void waitForShuffle() {
        while (true) {
            System.out.println("Type 'shuffle' to reshuffle:");
            String cmd = input.nextLine();

            if (cmd.equals("shuffle")) {
                break;
            }
            System.out.println("Wrong input!\n");
        }
        System.out.println("\nReshuffling...\n");
        playGame();
    }

    static String evaluate(List<Card> hand) {

        HashMap<String, Integer> map = new HashMap<>();
        HashSet<String> suits = new HashSet<>();

        for (Card c : hand) {

            map.put(c.rank,
                    map.getOrDefault(c.rank, 0) + 1);

            suits.add(c.suit);
        }

        Collection<Integer> count = map.values();

        if (suits.size() == 1)
            return "Flush";

        if (count.contains(4))
            return "Four of a Kind";

        if (count.contains(3) && count.contains(2))
            return "Full House";

        if (count.contains(3))
            return "Three of a Kind";

        int pairs = 0;

        for (int x : count) {
            if (x == 2)
                pairs++;
        }

        if (pairs == 2)
            return "Two Pair";

        if (pairs == 1)
            return "One Pair";

        return "High Card";
    }

    static int rank(String h) {

        return switch (h) {

            case "Flush" -> 5;
            case "Four of a Kind" -> 4;
            case "Full House" -> 3;
            case "Three of a Kind" -> 2;
            case "Two Pair" -> 1;
            case "One Pair" -> 0;
            default -> -1;
        };
    }
}
