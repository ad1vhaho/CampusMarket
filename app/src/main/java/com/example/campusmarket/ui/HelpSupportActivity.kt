package com.example.campusmarket.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class HelpSupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(getColor(com.example.campusmarket.R.color.campus_background))
        }
        root.addView(MaterialToolbar(this).apply {
            title = "Help & Support"
            setNavigationIcon(com.example.campusmarket.R.drawable.ic_arrow_back)
            setNavigationOnClickListener { finish() }
        })
        val helpText = TextView(this).apply {
            text = "Frequently asked questions\n\nHow do I publish?\nOpen Sell, complete the required fields and tap Publish Listing.\n\nWhere are drafts?\nOpen Profile, then My Listings and select Drafts.\n\nHow do messages work?\nOpen a student-created listing and tap Message Seller.\n\nHow do I reset my password?\nUse Forgot Password on the login screen."
            textSize = 16f
            setTextColor(getColor(com.example.campusmarket.R.color.campus_text_black))
            setPadding(40, 40, 40, 24)
        }
        root.addView(helpText, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        root.addView(MaterialButton(this).apply {
            text = "Email Support"
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:?subject=CampusMarket%20Support")))
            }
        }, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        setContentView(root)
    }
}
