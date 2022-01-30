package com.example.laont

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.laont.databinding.ActivityMainBinding
import com.example.laont.dto.ReverseGeocodingDto
import com.example.laont.dto.UserInfoDto
import com.example.laont.fragment.viewpager.PagerAdapter
import com.example.laont.retrofit.NaverRetrofitService
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var PERMISSION_ID = 1000

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    private lateinit var mBinding: ActivityMainBinding
    private val binding get() = mBinding!!
    private lateinit var toolbarText: TextView
    private lateinit var view_pager: ViewPager2
    private lateinit var bottom_adapter: PagerAdapter
    private lateinit var bottom_navigation: BottomNavigationView

    private lateinit var reverseGeocoding: ReverseGeocodingDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarText = binding.toolbarText

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        viewPagerInit()
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if(isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        coordsToAddress(location.longitude, location.latitude)
                    }
                }
            } else {
                Toast.makeText(this, "GPS를 켜주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()!!
        )
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location = p0.lastLocation
            coordsToAddress(lastLocation.longitude, lastLocation.latitude)
        }
    }

    // Location permission check
    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // get user permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_ID
        )
    }

    // Check location service of the device is enabled
    private fun isLocationEnabled(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    private fun coordsToAddress(lng: Double, lat: Double) {
        val retrofit = RetrofitCreator.defaultRetrofit(SecretData.NAVER_REVERSE_GEOCODING_URI)
        val service = retrofit.create(NaverRetrofitService::class.java)
        val call : Call<ReverseGeocodingDto> = service.reverseGeocoding(
            lng.toString()+","+lat.toString(),
            "json",
            "addr",
            SecretData.NAVER_CLIENT_ID,
            SecretData.NAVER_CLIENT_SECRET
        )

        call.enqueue(object: Callback<ReverseGeocodingDto> {
            override fun onResponse(
                call: Call<ReverseGeocodingDto>,
                response: Response<ReverseGeocodingDto>
            ) {
                if (response.isSuccessful) {
                    reverseGeocoding = ReverseGeocodingDto(
                        response.body()?.status!!,
                        response.body()?.results!!
                    )
                    toolbarText.setText(reverseGeocoding.results[0].region.area3.name)
                }
            }

            override fun onFailure(call: Call<ReverseGeocodingDto>, t: Throwable) { }

        })
    }

    fun viewPagerInit() {
        view_pager = binding.viewpager
        bottom_adapter = PagerAdapter(supportFragmentManager, lifecycle)
        view_pager.adapter = bottom_adapter
        view_pager.isUserInputEnabled = false

        bottom_navigation = binding.bottomNevigation
        bottom_navigation.run {
            setOnNavigationItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.item_board -> {
                        view_pager.currentItem = 0
                    }
                    R.id.item_map -> {
                        view_pager.currentItem = 1
                    }
                }
                true
            }
        }
    }
}