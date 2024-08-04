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

data class Card(val rank: Rank, val suit: Suit) {
    override fun toString() =
        "$rank$suit"
}

data class Deck(private val cards: List<Card> = SortedCards): Iterable<Card> {
    override fun iterator(): Iterator<Card> =
        cards.iterator()

    fun shuffle() = Deck(cards.shuffled())

    fun take(numCards: Int = 1): Pair<List<Card>, Deck> =
        Pair(cards.take(numCards), Deck(cards.drop(numCards)))

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

private fun getNumber(min: Int, max: Int,
                      invalidMin: Int, invalidMax: Int,
                      prompt: String, invalidMessage: String, outOfRangeMessage: String): Int? {
    print(prompt)
    val value = readlnOrNull()?.toIntOrNull()
    if (value == null || value < invalidMin || value > invalidMax) {
        print(invalidMessage)
        return null
    } else if (value < min || value > max) {
        print(outOfRangeMessage)
        return null
    } else {
        return value
    }
}

tailrec fun play(deck: Deck = Deck.DefaultDeck) {
    println("Choose an action (reset, shuffle, get, exit):")
    print("> ")
    when (readlnOrNull()) {
        "reset" -> {
            println("Card deck is reset.")
            play(Deck.DefaultDeck)
        }
        "shuffle" -> {
            println("Card deck is shuffled.")
            play(deck.shuffle())
        }
        "get" -> {
            val number = getNumber(
                1, deck.size, 1, 52,
                "Number of cards:\n> ",
                "Invalid number of cards.\n",
                "The remaining cards are insufficient to meet the request.\n")
            if (number != null) {
                val (cards, newDeck) = deck.take(number)
                println(cards.toSpaceSeparatedString())
                play(newDeck)
            } else {
                play(deck)
            }
        }
        "exit" -> {
            println("Bye")
            return
        }
        else -> {
            println("Wrong action.")
            play(deck)
        }
    }
}

fun exercise1() {
    Rank.entries.joinToString(separator = " ")
    print(Rank.entries.toSpaceSeparatedString())
    println()
    println()

    print(Suit.entries.toSpaceSeparatedString())
    println()
    println()

    print(Deck.DefaultDeck.shuffle().toSpaceSeparatedString())
    println()
}

fun exercise2() {
    play()
}

fun main() {
    // exercise1()
    exercise2()
}
