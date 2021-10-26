package com.example.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.podplay.R
import com.example.podplay.adapter.PodcastlistAdapter
import com.example.podplay.databinding.ActivityPodcastBinding
import com.example.podplay.repository.ItunesRepository
import com.example.podplay.service.ItunesService
import com.example.podplay.viewmodels.SearchViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher


class PodcastActivity : AppCompatActivity() , PodcastlistAdapter.PodcastListAdapterListener {

    val TAG = javaClass.simpleName
    private lateinit var binding: ActivityPodcastBinding
    private val viewmodel : SearchViewmodel by  viewModels<SearchViewmodel>()
    private lateinit var adapter : PodcastlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupViewmodel()
        updateControl()
         handleIntent(intent)
    }




    private fun setupToolbar(){
        setSupportActionBar(binding.toolbar)

    }
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun setupViewmodel(){
        val service = ItunesService.instance
        viewmodel.itunesRepository = ItunesRepository(service)

    }

    private fun updateControl(){
        binding.podcastRecyclerView.setHasFixedSize(true)

        val layoutmanger = LinearLayoutManager(this)
        binding.podcastRecyclerView.layoutManager = layoutmanger

        val dividerItemDecoration = DividerItemDecoration(
            binding.podcastRecyclerView.context,
            layoutmanger.orientation)


        binding.podcastRecyclerView.addItemDecoration(dividerItemDecoration)

        adapter = PodcastlistAdapter(null ,this , this)
        binding.podcastRecyclerView.adapter = adapter

    }


    private fun performSearch(term: String){
        showProgressBar()
        GlobalScope.launch{
            val result  = viewmodel.searchPodcast(term)
            withContext(Dispatchers.Main){
                hideProgressBar()
                binding.toolbar.title = term
                adapter.setSearchData(result)
            }

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
        inflater.inflate(R.menu.menu_search, menu)

        //set el search as actionview
        val searchmenuItem = menu?.findItem(R.id.search_item)
        val searchview = searchmenuItem?.actionView as SearchView

        //3ashan a3rf ageb el text aly fl search bar
        val searchmanger = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        //assign el manger bl view
        searchview.setSearchableInfo(searchmanger.getSearchableInfo(componentName))
        return true


    }

    override fun onShowDetail(podcastSummuryViewData: SearchViewmodel.podcastSummuryViewData) {
        TODO("Not yet implemented")
    }
}