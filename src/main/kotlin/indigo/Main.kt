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

    fun remove(card: Card): Deck =
        Deck(cards.filterNot { it == card} )

    operator fun get(cardIndex: Int): Card =
        if (cardIndex < 0 || cardIndex >= cards.size)
            throw IndexOutOfBoundsException("${cards.size} cards: attempted to take card $cardIndex")
        else
            cards[cardIndex]

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

sealed interface Strategy {
    fun selectCard(table: Deck, hand: Deck): Card?
}

private data object HumanStrategy: Strategy {
    override fun selectCard(table: Deck, hand: Deck): Card? {
        val cardString = hand.withIndex().map { "${it.index + 1})${it.value}" }.toSpaceSeparatedString()
        println("Cards in hand: $cardString")
        val cardIndex = getNumber(1, hand.size, "Choose a card to play (1-${hand.size}):\n> ")
            ?.let { it - 1 }
        if (cardIndex == null) return null
        return hand[cardIndex]
    }
}

private data object ComputerStrategy: Strategy {
    override fun selectCard(table: Deck, hand: Deck): Card {
        val card = hand[0]
        println("Computer plays $card")
        return card
    }
}

enum class Player(val strategy: Strategy) {
    HUMAN(HumanStrategy),
    COMPUTER(ComputerStrategy);

    companion object {
        fun flip(player: Player) =
            when (player) {
                HUMAN -> COMPUTER
                COMPUTER -> HUMAN
            }
    }
}

tailrec fun playRound(player: Player, deck: Deck, playerHand: Deck, otherHand: Deck, table: Deck): Player? {
    if (playerHand.isNotEmpty() || otherHand.isNotEmpty() || deck.isEmpty())
        println("${table.size} cards on the table, and the top card is ${table.last()}")

    // Check to see if the player and computer have empty hands.
    if (playerHand.isEmpty() && otherHand.isEmpty()) {
        // If the deck is empty, the game is over.
        if (deck.isEmpty()) return player

        val (newPlayerHand, deck2) = deck.take(6)
        val (newOtherHand, newDeck) = deck2.take(6)
        return playRound(player, newDeck, newPlayerHand, newOtherHand, table)
    }

    val card = player.strategy.selectCard(table, playerHand) ?: return player
    val newHand = playerHand.remove(card)
    println()
    return playRound(Player.flip(player), deck, otherHand, newHand, table.append(card))
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
    val (firstHand, deck3) = deck2.take(6)
    val (secondHand, deck) = deck3.take(6)

    println("Initial cards on the table: ${table.toSpaceSeparatedString()}\n")
    return playRound(player, deck, firstHand, secondHand, table)
}

fun main() {
    play()
    println("Game Over")
}
