package com.example.podplay.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.podplay.R
import com.example.podplay.databinding.FragmentDetailPodcastBinding
import com.example.podplay.viewmodels.PodcastViewmodel

class PodcastDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailPodcastBinding
    private val viewmodel : PodcastViewmodel by activityViewModels()


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

}