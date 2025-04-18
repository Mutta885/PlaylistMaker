package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(private val themeInteractor : ThemeInteractor,
                        private val sharingInteractor : SharingInteractor) : ViewModel() {

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