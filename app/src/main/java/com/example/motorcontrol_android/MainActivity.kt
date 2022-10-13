package com.example.motorcontrol_android

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.sagebionetworks.assessmentmodel.Factory
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var rootLayout: LinearLayout by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val product = Factory.create(mapOf("user" to "JetBrains"))
        val tv = TextView(this)
        tv.text = product.toString()
        rootLayout = findViewById(R.id.main_view)
        rootLayout.addView(tv)
    }
}