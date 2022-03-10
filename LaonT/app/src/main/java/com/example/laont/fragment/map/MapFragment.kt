package com.example.laont.fragment.map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.laont.MainActivity
import com.example.laont.SecretData
import com.example.laont.databinding.FragmentMapBinding
import com.example.laont.dto.Playground
import com.example.laont.fragment.board.PGListActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: MapView
    lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private lateinit var pginfoList: ListView
    private lateinit var list_adapter: PgListAdapter

    var markers = mutableListOf<Pair<Marker, MutableList<Playground>>>()
    var mapReady: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        map = binding.map
        map.onCreate(savedInstanceState)
        map.getMapAsync(this)

        pginfoList = binding.pginfoList

        locationSource = FusedLocationSource(this, MainActivity.PERMISSION_ID)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        map.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        map.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    override fun onMapReady(p0: NaverMap) {
        val parent = activity as MainActivity?
        val lng = parent!!.coords.split(",")[0].toDouble()
        val lat = parent!!.coords.split(",")[1].toDouble()
        naverMap = p0
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lng))
        naverMap.moveCamera(cameraUpdate)
        mapReady = true
        initMarker(parent.PG_list)

        naverMap.setOnMapClickListener { pointF, latLng ->
            pginfoList.visibility = View.GONE
        }

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    fun initMarker(PG_list: List<Playground>) {
        if (mapReady) {
            markers.clear()
            for (playground in PG_list) {
                val marker = Marker()
                marker.position = LatLng(playground.latitude, playground.longitude)
                marker.map = naverMap
                val iter = markers.find { it.first.position == LatLng(playground.latitude, playground.longitude) }
                if (iter == null) {
                    val marker = Marker()
                    marker.position = LatLng(playground.latitude, playground.longitude)
                    marker.map = naverMap
                    marker.captionText = "1"
                    marker.setCaptionAligns(Align.Top)
                    marker.setOnClickListener { o ->
                        popUpPGInfo(markers.find { it.first == o as Marker })
                        true
                    }
                    markers.add(Pair(marker, mutableListOf(playground)))
                } else {
                    iter.second.add(playground)
                    iter.first.captionText = iter.second.size.toString()
                }
            }
        }
    }

    fun popUpPGInfo(marker: Pair<Marker, MutableList<Playground>>?) {
        val cameraUpdate = CameraUpdate.scrollTo(marker!!.first.position)
        naverMap.moveCamera(cameraUpdate)

        pginfoList.visibility = View.VISIBLE
        list_adapter = PgListAdapter(marker.second)
        pginfoList.adapter = list_adapter

        pginfoList.setOnItemClickListener { par, view, position, id ->
            val intent = Intent(binding.root.context, PGListActivity::class.java)
            intent.putExtra("pg_name", marker.second[position].name)
            startActivityForResult(intent, SecretData.RELOAD_PG)
        }
    }
}