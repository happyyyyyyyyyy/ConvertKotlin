package com.deepfine.android.kotlinstudy.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.deepfine.android.kotlinstudy.R
import com.deepfine.android.kotlinstudy.activity.MainActivity
import com.deepfine.android.kotlinstudy.adapter.HomeAdapter
import com.deepfine.android.kotlinstudy.databinding.FragmentHomeBinding
import com.deepfine.android.kotlinstudy.dto.DogDto
import com.deepfine.android.kotlinstudy.dto.DogImage
import com.deepfine.android.kotlinstudy.room.DogData
import com.deepfine.android.kotlinstudy.room.DogDataDatabase
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

  private lateinit var ct: Context
  private lateinit var db: DogDataDatabase
  private var pageOffset = 0
  private var pageNumber = 1
  private lateinit var apiDataList: MutableList<DogDto>
  private var searchList: MutableList<DogDto> = mutableListOf()
  private lateinit var homeAdapter: HomeAdapter
  private lateinit var binding: FragmentHomeBinding
  private var searchFlag = false
  private var isLoading = false
  override fun onAttach(context: Context) { //생명 주기를 이용한 context 받아 오기
    super.onAttach(context)
    if (context is MainActivity) ct = context
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    db = Room.databaseBuilder(ct, DogDataDatabase::class.java, "DogData").build()
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

    initRecyclerView()
    initSearchView()
    setDataRecyclerView()
    return binding.root
  }

  private fun initRecyclerView() {
    binding.dogRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    binding.dogRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        //recyclerView 가장 마지막 index
        val lastPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        //받아온 recyclerView 카운트
        val totalCount = recyclerView.adapter?.itemCount
        if (totalCount != null) {
          if (lastPosition == (totalCount - 1)) {
            //스크롤 끝까지 하면 작동
            if (!searchFlag) if (!isLoading) {
              loadMoreData()
            }
          }
        }
      }
    })
  }

  private fun initSearchView() {
    binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String): Boolean {
        searchFlag = true
        search(query)
        return false
      }

      override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty())
          searchbarEmpty()
        else {
          searchFlag = true
          search(newText)
        }
        return false
      }
    })
  }

  private fun setDataRecyclerView() {
    homeAdapter = HomeAdapter()
    apiDataList = ArrayList()
    setRecyclerView(binding)
  }

  private fun setRecyclerView(binding: FragmentHomeBinding) {
    pageOffset = (pageNumber++ - 1) * PAGE_SIZE
    thread {
      val dogDataList = db.getDogDao().getItemsByPage(PAGE_SIZE, pageOffset)
      for (dogData in dogDataList) {
        val dogDto: DogDto? = returnDogDto(dogData).join()
        if (dogDto != null) {
          // 비동기 작업이 끝난 후에 결과를 사용하는 작업
          // dogDto를 사용하여 UI 업데이트 등을 수행할 수 있습니다.
          apiDataList.add(dogDto)
        }
      }
      apiDataList.add(DogDto.createLoadingData())
      homeAdapter.setItems(apiDataList)
      homeAdapter.notifyItemInserted(apiDataList.size - 1)
      binding.dogRecyclerView.adapter = homeAdapter
    }
  }

  private fun search(query: String) {
    searchList.clear()
    thread {
      val searchDataList = db.getDogDao().search("%$query%", query)
      requireActivity().runOnUiThread {
        if (searchDataList.size == 0) {
          binding.noDataText.visibility = View.VISIBLE
          binding.dogRecyclerView.visibility = View.INVISIBLE
        } else {
          binding.dogRecyclerView.visibility = View.VISIBLE
          binding.noDataText.visibility = View.INVISIBLE
        }
        for (dogData in searchDataList) {
          val dogDto: DogDto? = returnDogDto(dogData).join()
          if (dogDto != null) {
            // 비동기 작업이 끝난 후에 결과를 사용하는 작업
            // dogDto를 사용하여 UI 업데이트 등을 수행할 수 있습니다.
            searchList.add(dogDto)
          }
        }
        homeAdapter.setItems(searchList)
      }
    }
  }

  private fun searchbarEmpty() {
    searchFlag = false
    binding.dogRecyclerView.visibility = View.VISIBLE
    binding.noDataText.visibility = View.INVISIBLE
    thread {
      val tempList: MutableList<DogDto> = mutableListOf()
      for (dog in apiDataList) {
        if (db.getDogDao().checkData(dog.id))
          dog.bookmarkImg = R.drawable.selected_bookmark_icon
        else
          dog.bookmarkImg = R.drawable.unselected_bookmark_icon
        tempList.add(dog)
      }
      requireActivity().runOnUiThread {
        homeAdapter.setItems(tempList)
      }
    }
  }

  private fun returnDogDto(dogData: DogData): CompletableFuture<DogDto> {
    val futureResult = CompletableFuture<DogDto>()
    thread {
      val resultData = DogDto(dogData.id, dogData.bredFor, dogData.name, dogData.bookmarkCheck, viewType = HomeAdapter.ViewType.ITEM)
      resultData.image = DogImage(url = dogData.img)
      if (db.getDogDao().checkData(dogData.id)) resultData.bookmarkImg = R.drawable.selected_bookmark_icon
      futureResult.complete(resultData)
    }
    return futureResult
  }

  private fun loadMoreData() {
    isLoading = true
    pageOffset = (pageNumber++ - 1) * PAGE_SIZE //페이징 offset
    var dogDataList: MutableList<DogData>
    runBlocking {
      thread {
        dogDataList = db.getDogDao().getItemsByPage(PAGE_SIZE, pageOffset)
        thread {
          if (dogDataList.isEmpty()) {
            requireActivity().runOnUiThread {
              apiDataList.removeAt(apiDataList.size - 1)
              homeAdapter.setItems(apiDataList)
              Toast.makeText(ct, "No Data", Toast.LENGTH_SHORT).show()
            }
          } else {
            apiDataList.removeAt(apiDataList.size - 1)
            val scrollPosition = apiDataList.size
            requireActivity().runOnUiThread {
              homeAdapter.notifyItemRemoved(scrollPosition)
              //room에 저장된 dogData를 recyclerView에 set
              //for문을 통해 DogDto 객체에 저장
              for (one in dogDataList) {
                val futureResult = returnDogDto(one)
                val dogDto: DogDto? = futureResult.join()
                if (dogDto != null) {
                  // 비동기 작업이 끝난 후에 결과를 사용하는 작업
                  // dogDto를 사용하여 UI 업데이트 등을 수행할 수 있습니다.
                  apiDataList.add(dogDto)
                }
              }
              apiDataList.add(DogDto.createLoadingData())
              homeAdapter.setItems(apiDataList)
              isLoading = false
            }
          }
        }
      }
    }
  }

  companion object {
    const val PAGE_SIZE = 15
  }
}