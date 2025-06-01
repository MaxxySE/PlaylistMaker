package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    // Job для контроля корутины таймера
    private var timerJob: Job? = null

    init {
        interactor.setPlayerStateListener { state ->
            _playerState.postValue(state)
            // Управляем таймером в зависимости от состояния плеера
            when (state) {
                is PlayerState.Playing -> startPlayerTimer()
                is PlayerState.Paused, is PlayerState.Prepared, is PlayerState.Completed -> timerJob?.cancel()
                else -> { /* Ничего не делаем для других состояний */ }
            }
        }
    }

    private fun startPlayerTimer() {
        // Отменяем предыдущую Job, если она была
        timerJob?.cancel()
        // Запускаем новую корутину в viewModelScope
        timerJob = viewModelScope.launch {
            while (true) {
                // В задании указана задержка 300 мс
                delay(TIMER_DELAY_MS)
                val currentPosition = interactor.getCurrentPosition()
                _playerState.postValue(PlayerState.PositionUpdate(currentPosition))
            }
        }
    }

    // Этот метод больше не нужен, т.к. ViewModel сама управляет таймером
    // fun updateCurrentPosition() { ... }

    fun prepare(url: String) {
        interactor.prepare(url)
    }

    fun play() {
        interactor.play()
    }

    fun pause() {
        interactor.pause()
    }

    // stop() не нужен, так как onCleared() уже вызывается жизненным циклом ViewModel
    // и в нем мы останавливаем плеер и отменяем корутины
    // fun stop() { ... }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel() // Отменяем корутину
        interactor.stop() // Останавливаем и освобождаем плеер
    }

    companion object {
        private const val TIMER_DELAY_MS = 300L
    }
}