package com.example.locationrealtimefirebase

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.AbstractThreadedSyncAdapter
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseUser
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locationrealtimefirebase.Adapter.UserAdapter
import com.example.locationrealtimefirebase.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap


class MainActivity : AppCompatActivity() {

    lateinit var mFusedLocationCliente : FusedLocationProviderClient
    lateinit var mDatabase : DatabaseReference
    lateinit var mDatabaseUsers : DatabaseReference
    lateinit var firebaseUser: FirebaseUser

    private val ACCESS_FINE_LOCATION_CODE = 101
    private val ACCESS_COARSE_LOCATION_CODE = 102

    lateinit var userAdapter: UserAdapter
    lateinit var mUsers : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        initializeRecycleview()

        mUsers = ArrayList()

        mFusedLocationCliente = LocationServices.getFusedLocationProviderClient(this);
        inicializarVarFirebase()
        initializeSearchUsers()

        setupPermissions()
        activelocalizacion()

    }

    private fun initializeSearchUsers() {

        search_users.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchUsers(charSequence.toString().toLowerCase())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

    }

    private fun searchUsers(s: String) {

        val fuser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
            .startAt(s)
            .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)!!

                    assert(fuser != null)
                    if (!user.id.equals(fuser!!.uid)) {
                        mUsers.add(user)
                    }
                }

                userAdapter = UserAdapter(baseContext, mUsers, false)
                recycler_view.setAdapter(userAdapter)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })

    }

    private fun inicializarVarFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("Users")
        mDatabase = mDatabaseUsers.child(firebaseUser.getUid())

        initializeUsers()

    }

    private fun initializeUsers() {

        mDatabaseUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (search_users.text.toString() == "") {
                    mUsers.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)

                        if (!user!!.id.equals(firebaseUser.uid)) {
                            mUsers.add(user)
                        }

                    }

                    userAdapter = UserAdapter(baseContext, mUsers, false)
                    recycler_view.setAdapter(userAdapter)
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })
    }

    private fun initializeRecycleview() {
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(baseContext)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, StartActivity::class.java))
            return true
        }
        return false
    }

    private fun activelocalizacion() {

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

    fun status(status : String) {
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        mDatabase.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

}
