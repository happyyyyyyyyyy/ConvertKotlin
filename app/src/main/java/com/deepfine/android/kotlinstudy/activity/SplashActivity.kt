package com.deepfine.android.kotlinstudy.activity

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.deepfine.android.kotlinstudy.BuildConfig
import com.deepfine.android.kotlinstudy.R
import com.deepfine.android.kotlinstudy.Utility
import com.deepfine.android.kotlinstudy.api.DogApiObject
import com.deepfine.android.kotlinstudy.databinding.ActivitySplashBinding
import com.deepfine.android.kotlinstudy.dto.DogDto
import com.deepfine.android.kotlinstudy.room.DogData
import com.deepfine.android.kotlinstudy.room.DogDataDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
    }
    lateinit var db: DogDataDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startSplashThread()
    }


    private fun startSplashThread() {
        thread {
            db = Room.databaseBuilder(this, DogDataDatabase::class.java, "DogData").build()
            val animationThread = thread(false) {
                var delayCnt = 1
                var i = 0
                try {
                    while (i <= DELAY_TIME && !Thread.currentThread().isInterrupted) {
                        runOnUiThread {
                            when (delayCnt) {
                                1 -> binding.loadingText.text =
                                    getString(R.string.splash_loading_text, " .")

                                2 -> binding.loadingText.text =
                                    getString(R.string.splash_loading_text, " . .")

                                3 -> binding.loadingText.text =
                                    getString(R.string.splash_loading_text, " . . .")
                            }
                        }
                        delayCnt = (delayCnt % DELAY_COUNT) + 1
                        Thread.sleep(500)
                        i++
                        Log.d("TAG", "시간 $i")
                    }
                    if (!Thread.currentThread().isInterrupted) {
                        Log.d("TAG", "startSplashThread: 타이머가 끝남")
                        if (db.getDogDao().getAll().isEmpty()) {
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    R.string.splash_no_data_text,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            finish()
                        } else {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.d("TAG", "애니메이션 스레드 종료")
                    Thread.currentThread().interrupt() // 인터럽트 상태를 유지
                }
            }
            animationThread.start()
            val dataThread = thread(false) {
                Log.d("TAG", "startSplashThread: " + Utility().isNetworkConnected(this@SplashActivity))
                if (Utility()isNetworkConnected(this@SplashActivity))
                    Log.d("TAG", "run: 인터넷 연결이 원활하지 않아 db의 데이터로 호출")
                else {
                    getApiData()
                    animationThread.interrupt()
                }
            }
            dataThread.start()
        }
    }

    private fun getApiData() {
        val call = DogApiObject.getRetrofitService.getApiData(BuildConfig.DOG_API_KEY, "500")
        call.enqueue(object : Callback<ArrayList<DogDto>> {
            override fun onResponse(
                call: Call<ArrayList<DogDto>>,
                response: Response<ArrayList<DogDto>>
            ) {
                val result: ArrayList<DogDto>? = response.body()
                //Dog API에서 받아온 값 room의 DogData Entity가 null이면 저장 아니면 update
                if (result != null) {
                    thread {
                        if (db.getDogDao().getAll().isEmpty()) {
                            db.getDogDao().deleteAll() //DB 초기화
                            for (i in result.indices)
                                insertApiDataIntoDB(result[i])
                        } else
                            for (i in result.indices)
                                if (db.getDogDao().checkData2(result[i].id) == 0)
                                    insertApiDataIntoDB(result[i])
                    }
                }
                //로딩 끝내고 main으로 화면 전환
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
            override fun onFailure(call: Call<ArrayList<DogDto>>, t: Throwable) {
                Log.d("TAG", "onFailure: API 호출 실패")
            }
        })
    }

    fun insertApiDataIntoDB(dogDto: DogDto) {
        val dogData = DogData(dogDto.id, dogDto.name, dogDto.bredFor, dogDto.image.url, false)
        db.getDogDao().insert(dogData)
    }
    companion object {
        private const val DELAY_TIME = 3
        private const val DELAY_COUNT = 3
    }
}