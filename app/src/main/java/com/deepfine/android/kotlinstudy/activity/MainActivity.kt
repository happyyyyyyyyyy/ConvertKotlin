package com.deepfine.android.kotlinstudy.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.deepfine.android.kotlinstudy.R
import com.deepfine.android.kotlinstudy.Utility
import com.deepfine.android.kotlinstudy.databinding.ActivityMainBinding
import com.deepfine.android.kotlinstudy.fragment.BookmarkFragment
import com.deepfine.android.kotlinstudy.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
  private val binding: ActivityMainBinding by lazy {
    DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    changeScreen()
  }

  private fun changeScreen() {
    binding.bottomNav.setOnItemSelectedListener {
      when (it.itemId) {
        //홈으로 화면 전환
        R.id.home -> supportFragmentManager.beginTransaction()
          .replace(R.id.mainFrame, HomeFragment()).commit()
        //북마크로 화면 전환
        R.id.bookmark -> {
          //네트워크 비연결 시 경고창 출력
          if (Utility.isNetworkConnected(this@MainActivity))
            showNetworkWarningDialog()
          supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, BookmarkFragment()).commit()
        }
      }
      true
    }
    binding.bottomNav.selectedItemId = R.id.home // 첫 화면 홈 화면으로
  }

  private fun showNetworkWarningDialog() {
    AlertDialog.Builder(this@MainActivity)
      .setMessage(R.string.main_dialog_text)
      .setPositiveButton("OK", null)
      .show()
  }
}