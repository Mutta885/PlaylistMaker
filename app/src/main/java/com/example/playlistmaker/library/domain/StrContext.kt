package com.example.playlistmaker.library.domain

interface StrContext {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}