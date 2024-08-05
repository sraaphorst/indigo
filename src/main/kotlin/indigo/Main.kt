package indigo

enum class Suit(val symbol: String) {
    CLUB("♣"),
    DIAMOND("♦"),
    HEART("♥"),
    SPADE("♠");

    override fun toString(): String = symbol
}

enum class Rank(val number: Int, val symbol: String) {
    ACE(1, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K");

    override fun toString(): String = symbol
}

enum class YesNo(val string: String) {
    YES("yes"),
    NO("no");

    companion object {
        tailrec fun getYesNo(prompt: String): YesNo {
            print(prompt)
            return when (readlnOrNull()?.lowercase()) {
                YES.string -> YES
                NO.string -> NO
                else -> getYesNo(prompt)
            }
        }
    }
}

enum class Player {
    HUMAN,
    COMPUTER;

    companion object {
        fun flip(player: Player) =
            when (player) {
                HUMAN -> COMPUTER
                COMPUTER -> HUMAN
            }
    }
}

data class Card(val rank: Rank, val suit: Suit) {
    override fun toString() =
        "$rank$suit"
}

data class Deck(private val cards: List<Card> = SortedCards): Iterable<Card> {
    override fun iterator(): Iterator<Card> =
        cards.iterator()

    fun shuffle() = Deck(cards.shuffled())

    fun isEmpty() = cards.isEmpty()
    fun isNotEmpty() = cards.isNotEmpty()

    fun take(numCards: Int = 1): Pair<Deck, Deck> =
        Pair(Deck(cards.take(numCards)), Deck(cards.drop(numCards)))

    fun takeIndex(cardIndex: Int): Pair<Card, Deck> =
        if (cardIndex < 0 || cardIndex >= cards.size)
            throw IndexOutOfBoundsException("${cards.size} cards: attempted to take card $cardIndex")
        else
            Pair(cards[cardIndex], Deck(cards.take(cardIndex) + cards.drop(cardIndex + 1)))

    fun append(card: Card): Deck =
        Deck(cards + card)

    fun append(deck: Deck): Deck =
        Deck(cards + deck.cards)

    fun last() = cards.last()

    val size: Int =
        cards.size

    companion object {
        // Initialize the deck to the default order specified in the problem.
        private val SortedCards = Suit.entries.flatMap { suit ->
            Rank.entries.reversed().map { rank ->
                Card(rank, suit)
            }
        }
        val DefaultDeck = Deck(SortedCards)
    }
}

fun <T> Iterable<T>.toSpaceSeparatedString(): String =
    this.joinToString(separator = " ")

private tailrec fun getNumber(min: Int, max: Int, prompt: String): Int? {
    print(prompt)
    val strValue = readlnOrNull()?.lowercase()

    if (strValue == "exit")
        return null

    val value = strValue?.toIntOrNull()
    return when {
        value == null || value < min || value > max ->
            getNumber(min, max, prompt)
        else ->
            value
    }
}

tailrec fun playRound(player: Player, deck: Deck, humanHand: Deck, computerHand: Deck, table: Deck): Player? {
    if (humanHand.isNotEmpty() || computerHand.isNotEmpty() || deck.isEmpty())
        println("${table.size} cards on the table, and the top card is ${table.last()}")

    // Check to see if the player and computer have empty hands.
    if (humanHand.isEmpty() && computerHand.isEmpty()) {
        // If the deck is empty, the game is over.
        if (deck.isEmpty()) return player

        val (newHumanHand, deck2) = deck.take(6)
        val (newComputerHand, deck3) = deck2.take(6)
        return playRound(player, deck3, newHumanHand, newComputerHand, table)
    }

    return when (player) {
        Player.COMPUTER -> {
            val (card, newComputerHand) = computerHand.takeIndex(0)
            println("Computer plays $card\n")
            playRound(Player.flip(player), deck, humanHand, newComputerHand, table.append(card))
        }
        Player.HUMAN -> {
            val cardString = humanHand.withIndex().map { "${it.index+1})${it.value}"}.toSpaceSeparatedString()
            println("Cards in hand: $cardString")
            val cardIndex = getNumber(1, humanHand.size, "Choose a card to play (1-${humanHand.size}):\n> ")
                ?.let { it - 1 }
            if (cardIndex == null) null
            else {
                val (card, newHumanHand) = humanHand.takeIndex(cardIndex)
                println()
                playRound(Player.flip(player),deck, newHumanHand, computerHand, table.append(card))
            }
        }
    }
}

fun play(): Player? {
    println("Indigo Card Game")
    val player = when (YesNo.getYesNo("Play first?\n> ")) {
        YesNo.YES -> Player.HUMAN
        YesNo.NO -> Player.COMPUTER
    }

    // Deal the initial cards.
    val deck1 = Deck.DefaultDeck.shuffle()
    val (table, deck2) = deck1.take(4)
    val (playerHand, deck3) = deck2.take(6)
    val (computerHand, deck) = deck3.take(6)

    println("Initial cards on the table: ${table.toSpaceSeparatedString()}\n")
    return playRound(player, deck, playerHand, computerHand, table)
}

fun main() {
    play()
    println("Game Over")
}
