package com.deepfine.android.kotlinstudy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.deepfine.android.kotlinstudy.R
import com.deepfine.android.kotlinstudy.dto.DogDto

class HomeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  val homeItemList: ArrayList<DogDto> = ArrayList<DogDto>()

//  private val listener: OnListItemSelectListener
  private lateinit var context: Context
  enum class ViewType(val value: Int) {
    ITEM(0), LOADING(1);
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    context = parent.context
    if(viewType == ViewType.ITEM.value){
      val view = LayoutInflater.from(context).inflate(R.layout.home_dog_list, parent, false)
      return ItemViewHolder(view)
    }else{
      val view = LayoutInflater.from(context).inflate(R.layout.home_dog_loading, parent, false)
      return LoadingViewHolder(view)
    }
  }

  override fun getItemCount(): Int {
    return homeItemList.size
  }

  override fun getItemViewType(position: Int): Int {
    return if (homeItemList[position].viewType.value == ViewType.LOADING.value) ViewType.LOADING.value else ViewType.ITEM.value
  }

  fun setItems(items: MutableList<DogDto>){
    homeItemList.clear()
//    items.filterNotNullTo(homeItemList)
    homeItemList.addAll(items)
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    //position 값 -> 보여지는 데이터의 위치.
    if (holder is ItemViewHolder) {
      val data = homeItemList[position]
      data?.let {
        holder.bindData(it)
      }
    }
  }

  inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val nameText: TextView = itemView.findViewById(R.id.nameText)
    private val bredForText: TextView = itemView.findViewById(R.id.bredForText)
    private val bookmarkButton: ImageButton = itemView.findViewById(R.id.bookmarkButton)
    private val dogImage: ImageView = itemView.findViewById(R.id.dogImg)

    fun bindData(item: DogDto){
      nameText.text = item.name
      bredForText.text = item.bredFor
      item.bookmarkImg?.let {
        bookmarkButton.setImageResource(it)
      }
      val circleCrop = RequestOptions().circleCrop()
      Glide.with(context)
        .load(item.image?.url)
        .apply(circleCrop)
        .thumbnail(0.1f)
        .into(dogImage)

    }
  }

  inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view){
    private val progressBar: ProgressBar = itemView.findViewById(R.id.loadingIndicator)
  }
}