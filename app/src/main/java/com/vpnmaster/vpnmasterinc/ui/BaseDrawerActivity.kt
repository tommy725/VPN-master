package com.vpnmaster.vpnmasterinc.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManager
import com.infideap.drawerbehavior.AdvanceDrawerLayout
import com.vpnmaster.vpnmasterinc.Activities.PrivacyPolicy
import com.vpnmaster.vpnmasterinc.R

abstract class BaseDrawerActivity : AppCompatActivity() {
    private lateinit var manager :ReviewManager
    protected var toolbar: Toolbar? = null
        private set

    @get:LayoutRes
    protected abstract val layoutRes: Int

    private var mDrawerLayout: AdvanceDrawerLayout? = null
    private var navigationView: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes) // set content

        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.drawer_navigation_view)

        val category = findViewById<ImageView?>(R.id.category)
        category?.setOnClickListener {
            mDrawerLayout?.openDrawer(GravityCompat.START, true)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupNavView()
    }

    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavView() {
        if (navigationView == null) {
            return
        }

        navigationView?.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            val id = menuItem.itemId
            Handler().postDelayed({ handleDrawerClick(id) }, 300)
            mDrawerLayout?.closeDrawers()
            true
        }

    }

    private fun handleDrawerClick(menuId: Int) {
        when (menuId) {
            R.id.nav_upgrade -> {
            }
            R.id.nav_helpus -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("eylemc@gmail.com")) // Put Your Support Email Here
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
                intent.putExtra(Intent.EXTRA_TEXT, "")
                try {
                    startActivity(Intent.createChooser(intent, "send mail"))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_rate -> {
                rateUs()
            }
            R.id.nav_share -> {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using this Free VPN App, it's provide all servers free https://play.google.com/store/apps/details?id=" + this.packageName)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                }
            }
            R.id.nav_policy -> {
                val intent = Intent(this, PrivacyPolicy::class.java)
                startActivity(intent)
            }
        }

        mDrawerLayout?.closeDrawer(GravityCompat.START)
    }


    private fun rateUs() {
        val uri = Uri.parse("market://details?id=" + this.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.packageName)))
        }
    }

}