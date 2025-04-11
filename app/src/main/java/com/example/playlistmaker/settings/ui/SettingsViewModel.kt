package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator

class SettingsViewModel : ViewModel() {

    private val themeInteractor = Creator.provideThemeInteractor()
    private val sharingInteractor = Creator.provideSharingInteractor()

    private var themeStateLiveDataMutable = MutableLiveData<Boolean>()
    val themeStateLiveData : LiveData<Boolean> = themeStateLiveDataMutable

    init {
        themeStateLiveDataMutable.value = themeInteractor.getTheme()
    }

    fun setTheme(checked : Boolean){
        themeStateLiveDataMutable.postValue(checked)
        themeInteractor.setTheme(checked)
    }

    fun share(){
        sharingInteractor.shareApp()
    }

    fun mail(){
        sharingInteractor.mailToSupport()
    }

    fun readTerms(){
        sharingInteractor.readTerms()
    }

}