package com.deepfine.android.kotlinstudy

import android.view.View
import com.deepfine.android.kotlinstudy.dto.DogDto

interface OnListItemSelectListener {
  fun onRecyclerViewBookmarkSelected(v: View?, position: Int, arrayList: ArrayList<DogDto?>?)

  fun onRecyclerViewItemSelected(id: Int, imgUrl: String?, position: Int)
}