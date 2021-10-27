package com.example.podplay.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.podplay.databinding.SearchItemBinding
import com.example.podplay.ui.PodcastActivity
import com.example.podplay.viewmodels.SearchViewmodel

class PodcastlistAdapter(
    private var podcastViewList: List<SearchViewmodel.podcastSummuryViewData>?,
    private val podcastListAdapterListener: PodcastListAdapterListener,
    private val activity: Activity
) : RecyclerView.Adapter<PodcastlistAdapter.ViewHolder>() {




    class ViewHolder(databindig : SearchItemBinding ,
    private  val podcastListAdapterListener: PodcastListAdapterListener,) :
        RecyclerView.ViewHolder(databindig.root) {
            var podcastSummuryViewData : SearchViewmodel.podcastSummuryViewData? =null
            val name : TextView = databindig.podcastNameTextView
            val lastupdated : TextView = databindig.podcastLastUpdatedTextView
            val image : ImageView = databindig.podcastImage

        init {
            databindig.searchItem.setOnClickListener {
                podcastSummuryViewData?.let {
                    podcastListAdapterListener.onShowDetail(it)
                }
            }
        }




    }


    fun setSearchData(podcastSummaryViewData:
                      List<SearchViewmodel.podcastSummuryViewData>) {
        podcastViewList = podcastSummaryViewData
        this.notifyDataSetChanged()
    }

    interface PodcastListAdapterListener {
         fun onShowDetail(podcastSummuryViewData: SearchViewmodel.podcastSummuryViewData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SearchItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false), podcastListAdapterListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchViewList = podcastViewList ?: return
        val searchview = searchViewList[position]
        holder.podcastSummuryViewData  = searchview
        holder.name.text = searchview.name
        holder.lastupdated.text = searchview.lastupdated
       Glide.with(activity)
           .load(searchview.imageUrl)
           .circleCrop()
           .into(holder.image)




    }

    override fun getItemCount(): Int {
        return podcastViewList?.size ?: 0    }
}