package com.galacticai.networkpulse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import com.galacticai.networkpulse.ui.prepare.PrepareItemOld

class PrepareActivityOld : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare)

        init()
    }

    fun init() {
        // check, add item to array
        // check second, add item to array
        // ...
        // call 1 method to add all accumulated items to the layout


        // placeholders
        val list = findViewById<LinearLayout>(R.id.prepareItems)
        for (i in 0..10) {
            val item =
                PrepareItemOld(
                    this,
                    "Required item $i",
                    "$i ${getString(R.string.lorem)}",
                    R.drawable.ic_launcher_foreground
                )
            item.setOnClickListener {
                item.isChecked = true
            }
            list.addView(item)
            if (i < 10) {
                list.addView(
                    Space(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            8
                        )
                    }
                )
            }
        }
    }

    fun openMainActivity() {
        val intent = Intent(this, MainActivityOld::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}