package com.butter.wastesorter.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.butter.wastesorter.R
import com.butter.wastesorter.databinding.ActivityMainBinding
import com.butter.wastesorter.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

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

    val REQUEST_PERMISSION_1: Int = 1
    val REQUEST_PERMISSION_2: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNeedPermssion()

        initData()
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

    override fun onRestart() {
        super.onRestart()

        checkNeedPermssion()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_1) {
            if (permissions[0] == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onRequestPermissionsResult", "PERMISSION_GRANTED")
            } else if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onRequestPermissionsResult", "PERMISSION_GRANTED")
            } else {
                Snackbar.make(
                    binding.frameLayout,
                    getString(R.string.permission_need),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인", View.OnClickListener {
                    val settingIntent: Intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + packageName)
                    )
                    startActivity(settingIntent)
                }).show()
            }
        } else if (requestCode == REQUEST_PERMISSION_2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onRequestPermissionsResult", "PERMISSION_GRANTED")
            } else {
                Snackbar.make(
                    binding.frameLayout,
                    getString(R.string.permission_need),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인", View.OnClickListener {
                    val settingIntent: Intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + packageName)
                    )
                    startActivity(settingIntent)
                }).show()
            }
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

    private fun checkNeedPermssion() {
        val permission: ArrayList<String> = ArrayList()

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.CAMERA)
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permission.isEmpty()) {
            Log.i("checkNeedPermission", "PERMISSION_GRANTED")
        } else {
            ActivityCompat.requestPermissions(
                this,
                permission.toTypedArray(),
                permission.size
            )
        }
    }

    private fun initData() {
        mainViewModel.init(this)
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

        homeFragment.listener = object : HomeFragment.OnFragmentInteraction {
            override fun showInfoFragment() {
                binding.bottomNavBar.selectedItemId = R.id.menu_info
            }
        }
        searchFragment.listener = object : SearchFragment.OnFragmentInteraction {
            override fun showInfoFragment() {
                mainViewModel.imageBitmap.value = null
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