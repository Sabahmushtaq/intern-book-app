package com.example.mybooks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorlayout: CoordinatorLayout
    lateinit var toolbar : Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationbar: NavigationView
    var previousMenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawer)
        coordinatorlayout=findViewById(R.id.coordinator)
        toolbar =findViewById(R.id.toolbar)
        frameLayout =findViewById(R.id.frame)
        navigationbar=findViewById(R.id.navigationview)
        setUpToolBar()
        openDashBoard()

        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationbar.setNavigationItemSelectedListener {
            if(previousMenuItem!= null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it
            when(it.itemId){
                R.id.dashboard ->{
                   openDashBoard()
                    drawerLayout.closeDrawers()}
                R.id.favourite ->{supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,FavouriteFragment())
                   // .addToBackStack("Favourite")
                    .commit()
                        supportActionBar?.title="favourite"
                    drawerLayout.closeDrawers()
                    }
                R.id.people ->{supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,PeopleFragment())
                   // .addToBackStack("People")
                    .commit()
                    supportActionBar?.title="people"
                    drawerLayout.closeDrawers()}
                R.id.aboutus ->{supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,AboutusFragment())
                   // .addToBackStack("Aboutus")
                    .commit()
                    supportActionBar?.title="AboutUs"
                    drawerLayout.closeDrawers()}
            }
            return@setNavigationItemSelectedListener true

        }

    }



    fun setUpToolBar(){
       setSupportActionBar(toolbar)
        supportActionBar?.title="toolbar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
        fun openDashBoard(){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame,Dashboardfragment())
                //  .addToBackStack("Dashboard")
                .commit()
            supportActionBar?.title="Dashboard"

        }
    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is Dashboardfragment -> openDashBoard()
        else -> super.onBackPressed()}
    }

}
