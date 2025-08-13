package com.example.playlistmaker.library.data

import android.content.Context
import com.example.playlistmaker.library.domain.StrContext

class StrContextImpl(private val context: Context) : StrContext {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}