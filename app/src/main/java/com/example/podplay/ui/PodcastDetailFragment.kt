package com.example.podplay.ui

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.podplay.R
import com.example.podplay.adapter.EpisodeListAdapter
import com.example.podplay.databinding.FragmentDetailPodcastBinding
import com.example.podplay.service.PodplaymediaService
import com.example.podplay.viewmodels.PodcastViewmodel
import java.lang.RuntimeException

class PodcastDetailFragment : Fragment() {


    private lateinit var binding: FragmentDetailPodcastBinding
    private val viewmodel : PodcastViewmodel by activityViewModels()
    private lateinit var episodeListAdapter: EpisodeListAdapter
    private var listener : onPodcastDetailsListnener? = null

    private lateinit var mediaBrowser : MediaBrowserCompat
    private var mediaControllerCallback : MediaControllerCallback? = null


    companion object {
        fun newInstance(): PodcastDetailFragment {
            return PodcastDetailFragment()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initMediaBrowser()
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
                    binding.episodeRecylerview.layoutManager = layoutManager

                    val dividerItemDecoration = DividerItemDecoration(
                        binding.episodeRecylerview.context,
                        layoutManager.orientation)

                    binding.episodeRecylerview.addItemDecoration(dividerItemDecoration)

                    episodeListAdapter =
                        EpisodeListAdapter(viewData.episodes)
                    binding.episodeRecylerview.adapter = episodeListAdapter

                    activity?.invalidateOptionsMenu()
                }
            })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_detail , menu)
    }

    override fun onStart() {
        super.onStart()
        if (mediaBrowser.isConnected){
            val fragmentActivity =  activity as FragmentActivity
            if(MediaControllerCompat.getMediaController(fragmentActivity) == null){
                registerMediaController(mediaBrowser.sessionToken)
            }
        } else{
            mediaBrowser.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        val fragmentActivity= activity as FragmentActivity
        if(MediaControllerCompat.getMediaController(fragmentActivity) != null){
            mediaControllerCallback?.let {
                MediaControllerCompat.getMediaController(fragmentActivity).unregisterCallback(it)
            }
        }




    }






    override fun onPrepareOptionsMenu(menu: Menu) {
        viewmodel.podcastLiveData.observe(viewLifecycleOwner, { podcast ->
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


    private fun registerMediaController(token : MediaSessionCompat.Token){
        val fragmentActivity = activity as FragmentActivity
        val mediaController = MediaControllerCompat(fragmentActivity , token)

        MediaControllerCompat.setMediaController(fragmentActivity , mediaController)

        mediaControllerCallback = MediaControllerCallback()
        mediaController.registerCallback(mediaControllerCallback!!)
    }


    inner class MediaControllerCallback : MediaControllerCompat.Callback(){
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            println("metadata changed to ${metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)}")

        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            println("state changed to $state")

        }
    }

    inner class MediaBrowserCallbacks : MediaBrowserCompat.ConnectionCallback(){


        override fun onConnected() {
            super.onConnected()
            registerMediaController(mediaBrowser.sessionToken)
            println("connected")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            println("onConnectedFailed")
        }


        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            println("onConnectionSuspended")
        }
    }

    private fun initMediaBrowser(){
        val fragmentActivity = activity as FragmentActivity
        mediaBrowser= MediaBrowserCompat(fragmentActivity ,
                ComponentName(fragmentActivity, PodplaymediaService::class.java) ,
                  MediaBrowserCallbacks() , null)
    }

}