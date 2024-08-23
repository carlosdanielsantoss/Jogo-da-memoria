package com.example.gameofthememory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MemoryGameViewModel : ViewModel() {

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards

    private var flippedCards = mutableListOf<Card>()

    init {
        resetGame()
    }

    fun resetGame() {
        val initialCards = (1..8).map { Card(it, it.toString()) }
        val shuffledCards = (initialCards + initialCards).shuffled()
        _cards.value = shuffledCards
    }

    fun onCardClicked(card: Card) {
        if (card.isFaceUp || card.isMatched) return

        flippedCards.add(card)
        _cards.value = _cards.value.map {
            if (it.id == card.id) it.copy(isFaceUp = true) else it
        }

        if (flippedCards.size == 2) {
            checkForMatch()
        }
    }

    private fun checkForMatch() {
        viewModelScope.launch {
            delay(1000)
            if (flippedCards[0].content == flippedCards[1].content) {
                _cards.value = _cards.value.map {
                    if (flippedCards.contains(it)) it.copy(isMatched = true) else it
                }
            } else {
                _cards.value = _cards.value.map {
                    if (flippedCards.contains(it)) it.copy(isFaceUp = false) else it
                }
            }
            flippedCards.clear()
        }
    }
}
