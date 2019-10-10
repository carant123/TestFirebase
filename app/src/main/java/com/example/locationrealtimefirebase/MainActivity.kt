package com.example.locationrealtimefirebase

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mFusedLocationCliente : FusedLocationProviderClient

    private val ACCESS_FINE_LOCATION_CODE = 101
    private val ACCESS_COARSE_LOCATION_CODE = 102

    lateinit var mDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        setupPermissions()
        activelocalizacion()
    }

    private fun activelocalizacion() {
        mFusedLocationCliente = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationCliente.lastLocation.addOnSuccessListener { location: Location? ->
            ltlongi.text = "" + location?.latitude + " " + location?.longitude
            val anotherMap = mutableMapOf<String, Double>()
            anotherMap.put("latitud", location!!.latitude)
            anotherMap.put("longitude", location!!.longitude)
            mDatabase.child("usuarios").push().setValue(anotherMap)

        }
    }

    private fun setupPermissions() {
        val permissionFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        val permissionCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED && permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_CODE)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                ACCESS_COARSE_LOCATION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                             permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_FINE_LOCATION_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("TAG1", "Permission has been denied by user")
                } else {
                    Log.i("TAG2", "Permission has been granted by user")
                    activelocalizacion()
                }
            }
            ACCESS_COARSE_LOCATION_CODE -> {

            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Log.i("TAG1_2", "Permission has been denied by user")
            } else {
                Log.i("TAG2_2", "Permission has been granted by user")
                activelocalizacion()
            }
        }
        }
    }
}
