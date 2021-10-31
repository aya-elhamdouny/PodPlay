package com.example.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.podplay.R
import com.example.podplay.adapter.PodcastlistAdapter
import com.example.podplay.databinding.ActivityPodcastBinding
import com.example.podplay.repository.ItunesRepository
import com.example.podplay.repository.PodcastRepository
import com.example.podplay.service.ItunesService
import com.example.podplay.service.RssFeedService
import com.example.podplay.viewmodels.PodcastViewmodel
import com.example.podplay.viewmodels.SearchViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PodcastActivity : AppCompatActivity() , PodcastlistAdapter.PodcastListAdapterListener
 , PodcastDetailFragment.onPodcastDetailsListnener{

    val TAG = javaClass.simpleName
    private lateinit var binding: ActivityPodcastBinding
    private val viewmodel : SearchViewmodel by  viewModels<SearchViewmodel>()
    private lateinit var adapter : PodcastlistAdapter
    private lateinit var searchmenuItem : MenuItem
    private val podcastViewModel by viewModels<PodcastViewmodel>()

    companion object{
        private const val TAG_FRAGMENT = "fragmentDetail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupViewmodel()
        updateControl()
         handleIntent(intent)
        addBackStackListener()
        setupPodcastListView()

    }


    private fun createPodcastFragment() : PodcastDetailFragment{
        var podcastdetailfragment = supportFragmentManager.
                findFragmentByTag(TAG_FRAGMENT) as PodcastDetailFragment?


        if(podcastdetailfragment == null){
            podcastdetailfragment = PodcastDetailFragment.newInstance()
        }

        return podcastdetailfragment }




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
        podcastViewModel.repo = PodcastRepository(RssFeedService.instance , podcastViewModel.dao)

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
         searchmenuItem = menu?.findItem(R.id.search_item)!!
        val searchview = searchmenuItem.actionView as SearchView

        //3ashan a3rf ageb el text aly fl search bar
        val searchmanger = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        //assign el manger bl view
        searchview.setSearchableInfo(searchmanger.getSearchableInfo(componentName))
        if (supportFragmentManager.backStackEntryCount > 0) {
            binding.podcastRecyclerView.visibility = View.INVISIBLE
        }
        if (binding.podcastRecyclerView.visibility ==
            View.INVISIBLE) {
            searchmenuItem.isVisible = false
        }
        return true


    }

    private fun showDetailFragment(){

        val podcastFragment = createPodcastFragment()
        supportFragmentManager.beginTransaction().add(
            R.id.podcastDetailsContainer , podcastFragment, TAG_FRAGMENT)
            .addToBackStack("fragmentDetail").commit()

        binding.podcastRecyclerView.visibility = View.INVISIBLE
        searchmenuItem.isVisible = false



    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button), null)
            .create()
            .show()
    }


    override fun onShowDetail(podcastSummuryViewData: SearchViewmodel.podcastSummuryViewData) {
        podcastSummuryViewData.feedUrl ?: return
        showProgressBar()
        podcastViewModel.viewModelScope.launch (context = Dispatchers.Main) {
            podcastViewModel.getPodcast(podcastSummuryViewData)
            hideProgressBar()
            showDetailFragment()
        }

    }
    private fun createSubscription() {
        podcastViewModel.podcastLiveData.observe(this, {
            hideProgressBar()
            if (it != null) {
                showDetailFragment()
            } else {
                showError("Error loading feed")}
        })
    }

    private fun addBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.podcastRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSubscribe() {
        podcastViewModel.savePoacst()
        supportFragmentManager.popBackStack()
    }

    override fun onUnSubscribe() {
        TODO("Not yet implemented")
    }

    override fun onDelete() {
        TODO("Not yet implemented")
    }

    private fun showSubscribedPodcast(){

        val podcast = podcastViewModel.getPodcasts()?.value
        if(podcast != null){
            binding.toolbar.title = getString(R.string.subscribed)
            adapter.setSearchData(podcast)
        }
    }


    private fun setupPodcastListView(){
        podcastViewModel.getPodcasts()?.observe(this,
                {if (it != null){
                    showSubscribedPodcast()
                } })
    }



    }