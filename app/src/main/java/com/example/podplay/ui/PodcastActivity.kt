package com.example.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.SearchView
import com.example.podplay.R
import com.example.podplay.repository.ItunesRepository
import com.example.podplay.service.ItunesService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PodcastActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)




    }


    private fun performSearch(term : String){
        val itunesService = ItunesService.instance
        val repository = ItunesRepository(itunesService)

        GlobalScope.launch{
            val result  = repository.searchByTerm(term)
            Log.i(TAG , "RESULT = ${result.body()}")
        }
    }


    //2-check of intent action = search take string from intent and pass it to search action
    private fun handleIntent(intent: Intent){
        if(Intent.ACTION_SEARCH == intent.action){
            val qurey = intent.getStringExtra(SearchManager.QUERY) ?:
            return
            performSearch(qurey)
        }
    }



    //1-to be updated with search bar
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search , menu)

        //set el search as actionview
        val searchmenuItem = menu?.findItem(R.id.search_item)
        val searchview = searchmenuItem?.actionView as SearchView

        //3ashan a3rf ageb el text aly fl search bar
        val searchmanger = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        //assign el manger bl view
        searchview.setSearchableInfo(searchmanger.getSearchableInfo(componentName))
        return true


    }
}