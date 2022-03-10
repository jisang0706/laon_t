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
import com.example.laont.dto.*
import com.example.laont.fragment.viewpager.PagerAdapter
import com.example.laont.retrofit.NaverRetrofitService
import com.example.laont.retrofit.OpenAPIRetrofitService
import com.example.laont.retrofit.RetrofitCreator
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.min
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    companion object {
        var PERMISSION_ID = 1000
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var coords: String
    lateinit var address: String
    lateinit var town: String
    var PG_list = mutableListOf<Playground>()

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
                    val location: Location? = task.result
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
            val lastLocation: Location = p0.lastLocation
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
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        coords = lng.toString()+","+lat.toString()
        val retrofit = RetrofitCreator.defaultRetrofit(SecretData.NAVER_API_URI)
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
                    town = reverseGeocoding.results[0].region.area3.name
                    address = reverseGeocoding.results[0].region.area2.name +
                            " " + reverseGeocoding.results[0].region.area3.name
                    toolbarText.setText(town)
                    getPlayGround(address)
                    bottom_adapter.boardFragment.initAreaBoard()
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
                        bottom_adapter.mapFragment.initMarker(PG_list)
                        view_pager.currentItem = 1
                    }
                }
                true
            }
        }
    }

    fun getPlayGround(areaNm: String) {
        val retrofit = RetrofitCreator.xmlRetrofit(SecretData.OPEN_API_URL)
        val service = retrofit.create(OpenAPIRetrofitService::class.java)
        val call : Call<PGResponseDto> = service.getPlayGround(
            SecretData.OPEN_API_serviceKey,
            areaNm,
            50
        )

        call.enqueue(object : Callback<PGResponseDto> {
            override fun onResponse(call: Call<PGResponseDto>, response: Response<PGResponseDto>) {
                if (response.isSuccessful) {
                    processPlayground(response.body()?.body!!.items.itemlist)
                }
            }

            override fun onFailure(call: Call<PGResponseDto>, t: Throwable) {
                Toast.makeText(binding.root.context, "놀이시설 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun processPlayground(items: List<PGItemDto>) {
        val retrofit = RetrofitCreator.defaultRetrofit(SecretData.NAVER_API_URI)
        val service = retrofit.create(NaverRetrofitService::class.java)
        for (i in 0 until items.size) {
            val pgAddress: String
            if (items[i].roadAddress != null) {
                pgAddress = items[i].roadAddress!!
            } else {
                pgAddress = items[i].groundAddress1!! + " " + items[i].groundAddress2!!
            }
            val call: Call<GeoCodingDto> = service.geocoding(
                pgAddress,
                SecretData.NAVER_CLIENT_ID,
                SecretData.NAVER_CLIENT_SECRET
            )

            call.enqueue(object: Callback<GeoCodingDto> {
                override fun onResponse(
                    call: Call<GeoCodingDto>,
                    response: Response<GeoCodingDto>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.addresses!!.size > 0) {
                            PG_list.add(
                                Playground(
                                    items[i].id.toString(),
                                    pgAddress,
                                    items[i].name.toString(),
                                    response.body()?.addresses!![0].y,
                                    response.body()?.addresses!![0].x
                                )
                            )
                            if (i >= items.size-1) {
                                PG_list.sortBy {
                                    ((coords.split(",")[0].toDouble() - it.longitude).pow(
                                        2
                                    ) + (coords.split(",")[1].toDouble() - it.latitude).pow(2))
                                }
                                Log.e("WOW", "LISTSIZE" + PG_list.size)
                                PG_list = PG_list.subList(0, min(10, PG_list.size))
                                bottom_adapter.mapFragment.initMarker(PG_list)
                                bottom_adapter.boardFragment.initPgBoard()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GeoCodingDto>, t: Throwable) { }

            })
        }
    }
}