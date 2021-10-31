package com.example.podplay.ui

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.podplay.R
import com.example.podplay.adapter.EpisodeListAdapter
import com.example.podplay.databinding.FragmentDetailPodcastBinding
import com.example.podplay.viewmodels.PodcastViewmodel
import java.lang.RuntimeException

class PodcastDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailPodcastBinding
    private val viewmodel : PodcastViewmodel by activityViewModels()
    private lateinit var episodeListAdapter: EpisodeListAdapter
    private var listener : onPodcastDetailsListnener? = null


    companion object {
        fun newInstance(): PodcastDetailFragment {
            return PodcastDetailFragment()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  FragmentDetailPodcastBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.podcastLiveData.observe(viewLifecycleOwner,
            { viewData ->
                if (viewData != null) {
                    binding.feedtextview.text = viewData.feedTitle
                    binding.desctextview.text = viewData.feedDesc
                    activity?.let { activity ->

                        Glide.with(activity).load(viewData.imageUrl).into(binding.feedImageView)
                    }
                    binding.desctextview.movementMethod =
                        ScrollingMovementMethod()
                    binding.episodeRecylerview.setHasFixedSize(true)
                    val layoutManager = LinearLayoutManager(activity)
                    binding.episodeRecylerview.layoutManager =
                        layoutManager
                    val dividerItemDecoration = DividerItemDecoration(
                        binding.episodeRecylerview.context,
                        layoutManager.orientation)

                    binding.episodeRecylerview.addItemDecoration(dividerItemDecoration)
                    episodeListAdapter =
                        EpisodeListAdapter(viewData.episodes)
                    binding.episodeRecylerview.adapter =
                        episodeListAdapter

                    activity?.invalidateOptionsMenu()
                }
            })
        updateControlls()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_detail , menu)
    }


    private fun updateControlls(){
        val viewdata = viewmodel.activepodcastViewData ?: return

        binding.feedtextview.text = viewdata.feedTitle
        binding.desctextview.text = viewdata.feedDesc
        activity?.let {
            Glide.with(it)
                    .load(viewdata.imageUrl).circleCrop().into(binding.feedImageView)
        }

    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        viewmodel.podcastLiveData.observe(viewLifecycleOwner, {
                podcast ->
            if (podcast != null) {
                menu.findItem(R.id.menu_feed_action).title = if (podcast.subscribed)
                    getString(R.string.unsubscribe) else
                    getString(R.string.subscribed)
            }
        })
        super.onPrepareOptionsMenu(menu)
    }


    interface onPodcastDetailsListnener{
        fun onSubscribe()
        fun onUnSubscribe()
        fun onDelete()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_feed_action -> {
                if (item.title == getString(R.string.unsubscribe)) {
                    listener?.onUnSubscribe()
                } else {
                    listener?.onSubscribe()
                }
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onPodcastDetailsListnener){
            listener= context

        }else{
            throw RuntimeException(context.toString() +
            "must implemeny onPodcastDetailListener")
        }
    }

}