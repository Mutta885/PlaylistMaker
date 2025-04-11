package com.example.playlistmaker.sharing.domain

class SharingInteractorImpl (private val repository : SharingRepository) : SharingInteractor {

    override fun shareApp() {
        return repository.shareApp()
    }

    override fun mailToSupport() {
        return repository.mailToSupport()
    }

    override fun readTerms() {
        return repository.readTerms()
    }


}
