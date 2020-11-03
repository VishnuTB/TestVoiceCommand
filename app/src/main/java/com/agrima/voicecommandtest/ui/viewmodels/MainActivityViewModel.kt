package com.agrima.voicecommandtest.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrima.voicecommandtest.ui.models.Status
import com.agrima.voicecommandtest.ui.models.Tile

class MainActivityViewModel : ViewModel() {

    private val TAG = MainActivityViewModel::class.simpleName

    val boardTiles = MutableLiveData<ArrayList<Tile>>()
    private val selectedIndex = MutableLiveData<Int>()
        .apply {
            value = 4
        }

    fun generateBoard() {
        Log.i(TAG, "generateBoard: ")
        val tiles = ArrayList<Tile>()
        for (i in 0 until 9) {
            val tile = Tile()
            if (i == selectedIndex.value) {
                tile.status = Status.SELECTED
            } else {
                tile.status = Status.UNSELECTED
            }
            tiles.add(tile)
        }
        selectedIndex.postValue(selectedIndex.value)
        boardTiles.postValue(tiles)
    }

    private fun setSelected(selectedIndex: Int) {
        Log.i(TAG, "setSelected: $selectedIndex")
        val tiles = ArrayList<Tile>()
        for (i in 0 until 9) {
            val tile = Tile()
            if (i == selectedIndex)
                tile.status = Status.SELECTED
            tiles.add(tile)
        }
        this.selectedIndex.postValue(selectedIndex)
        boardTiles.postValue(tiles)
    }

    fun navigateRight() {
        if (selectedIndex.value!! + 1 < 9) {
            setSelected(selectedIndex.value!! + 1)
        }
    }

    fun navigateLeft() {
        if (selectedIndex.value!! - 1 >= 0) {
            setSelected(selectedIndex.value!! - 1)
        }
    }

    fun navigateDown() {
        if (selectedIndex.value!! + 3 <= 9) {
            setSelected(selectedIndex.value!! + 3)
        }
    }

    fun navigateUp() {
        if (selectedIndex.value!! - 3 >= 0) {
            setSelected(selectedIndex.value!! - 3)
        }
    }

    fun setGreen() {
        val tiles = ArrayList<Tile>()
        for (i in 0 until 9) {
            val tile = Tile()
            if (i == selectedIndex.value)
                tile.status = Status.START
            tiles.add(tile)
        }
        boardTiles.postValue(tiles)
    }

    fun blink() {
        val tiles = ArrayList<Tile>()
        for (i in 0 until 9) {
            val tile = Tile()
            if (i == selectedIndex.value)
                tile.status = Status.STOP
            tiles.add(tile)
        }
        boardTiles.postValue(tiles)
    }

    fun setSelected() {
        val tiles = ArrayList<Tile>()
        for (i in 0 until 9) {
            val tile = Tile()
            if (i == selectedIndex.value)
                tile.status = Status.SELECTED
            tiles.add(tile)
        }
        boardTiles.postValue(tiles)
    }

}