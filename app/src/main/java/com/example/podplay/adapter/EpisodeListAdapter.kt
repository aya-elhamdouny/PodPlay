package com.example.podplay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.podplay.databinding.EpisodeItemBinding
import com.example.podplay.utils.DateUtils
import com.example.podplay.utils.HtmlUtils
import com.example.podplay.viewmodels.PodcastViewmodel
import org.w3c.dom.Text

class EpisodeListAdapter(var episodeList : List<PodcastViewmodel.EpisodeViewData>,
  private val listAdapterListener: EpisodeListAdapterListener)
    : RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>() {


    class ViewHolder(binding: EpisodeItemBinding ,
    val listAdapterListener: EpisodeListAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {
            var episode : PodcastViewmodel.EpisodeViewData? = null
        val tiltle : TextView = binding.titleView
        val descTextView: TextView = binding.descView
        val durationTextView: TextView = binding.durationView
        val releaseDateTextView: TextView = binding.releaseDateView


        init {
            binding.root.setOnClickListener {
                episode?.let {
                    listAdapterListener.onselectedEpisode(it)
                }
            }
        }
}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeListAdapter.ViewHolder {

            return ViewHolder(EpisodeItemBinding.inflate(LayoutInflater.from(parent.context)
                ,parent , false ) , listAdapterListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeList = episodeList ?: return
        val episodeView  =  episodeList[position]

        holder.episode = episodeView
        holder.tiltle.text = episodeView.title
        holder.descTextView.text = HtmlUtils.htmlToSpannable(episodeView.description ?: "")
        holder.durationTextView.text = episodeView.duration
        holder.releaseDateTextView.text = episodeView.releaseDate?.let {
            DateUtils.dateToShortDate(it)
        }
    }

    override fun getItemCount(): Int {
        return episodeList?.size ?: 0
    }
    interface EpisodeListAdapterListener{
        fun onselectedEpisode(episodeviewdata : PodcastViewmodel.EpisodeViewData)

    }


}

