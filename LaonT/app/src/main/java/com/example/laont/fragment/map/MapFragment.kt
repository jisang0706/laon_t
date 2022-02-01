package com.example.laont.fragment.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.laont.MainActivity
import com.example.laont.R
import com.example.laont.databinding.FragmentMapBinding
import com.example.laont.dto.Playground
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: MapView

    var markers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        map = binding.map
        map.onCreate(savedInstanceState)
        map.getMapAsync(this)

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
        parent!!.naverMap = p0
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lng))
        parent!!.naverMap.moveCamera(cameraUpdate)
    }

    fun initMarker(PG_list: List<Playground>) {
        val parent = activity as MainActivity?
        markers.clear()
        for (playground in PG_list) {
            val marker = Marker()
            marker.position = LatLng(playground.latitude, playground.longitude)
            marker.map = parent!!.naverMap
            markers.add(marker)
        }
    }
}