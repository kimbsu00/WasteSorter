package com.butter.wastesorter.view

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.butter.wastesorter.R
import com.butter.wastesorter.databinding.ActivityMainBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    private val homeFragment: HomeFragment = HomeFragment()
    private val searchFragment: SearchFragment = SearchFragment()
    private val infoFragment: InfoFragment = InfoFragment()
    private val recordFragment: RecordFragment = RecordFragment()

    // 뒤로가기 두번 누르면 앱 종료 관련 변수 시작
    val FINISH_INTERVAL_TIME: Long = 2000
    var backPressedTime: Long = 0
    // 뒤로가기 두번 누르면 앱 종료 관련 변수 끝

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onBackPressed() {
        val tempTime: Long = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (0 <= intervalTime && intervalTime <= FINISH_INTERVAL_TIME) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            val msg: String = "뒤로 가기를 한 번 더 누르면 종료됩니다."
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFullScreen() {
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)

            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun init() {
        replaceFragment(homeFragment)
        binding.apply {
            bottomNavBar.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_home -> {
                        replaceFragment(homeFragment)
                        true
                    }
                    R.id.menu_search -> {
                        replaceFragment(searchFragment)
                        true
                    }
                    R.id.menu_info -> {
                        replaceFragment(infoFragment)
                        true
                    }
                    R.id.menu_record -> {
                        replaceFragment(recordFragment)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }

        searchFragment.listener = object : SearchFragment.OnFragmentInteraction {
            override fun showInfoFragment() {
                binding.bottomNavBar.selectedItemId = R.id.menu_info
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}