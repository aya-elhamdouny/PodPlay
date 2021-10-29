package com.example.podplay.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import java.text.DateFormat
import java.util.*

object HtmlUtils  {

    fun htmlToSpannable(htmlDesc: String): Spanned {
        var newHtmlDesc = htmlDesc.replace("\n".toRegex(), "")
        newHtmlDesc = newHtmlDesc.replace("(<(/)img>)|(<img.+?>)".
        toRegex(), "")

        val descSpan: Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            descSpan = Html.fromHtml(newHtmlDesc,
                Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            descSpan = Html.fromHtml(newHtmlDesc)
        }
        return descSpan
    }




}