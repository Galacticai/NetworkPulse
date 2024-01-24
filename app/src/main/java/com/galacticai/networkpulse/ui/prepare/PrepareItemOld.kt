package com.galacticai.networkpulse.ui.prepare

import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.galacticai.networkpulse.R


class PrepareItemOld(
    context: Context,
    title: String,
    description: String,
    imageID: Int,
    isChecked: Boolean = false
) : LinearLayout(context) {
    private val imageView: ImageView
    private val titleTextView: TextView
    private val descriptionTextView: TextView
    private val checkBox: CheckBox

    var isChecked: Boolean
        get() = checkBox.isChecked
        set(value) {
            checkBox.isChecked = value
            isClickable = !value
            background = if (value) null else getDrawable(context, R.drawable.shape_rounded_inkwell)
        }

    var title: CharSequence
        get() = titleTextView.text
        set(value) {
            titleTextView.text = value
        }

    var description: CharSequence
        get() = descriptionTextView.text
        set(value) {
            descriptionTextView.text = value
        }

    private var _imageID: Int = 0
    var imageID: Int
        get() = _imageID
        set(value) {
            _imageID = value
            imageView.setImageResource(value)
        }


    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_prepare, this)
        imageView = findViewById(R.id.itemIcon)
        titleTextView = findViewById(R.id.itemTitle)
        descriptionTextView = findViewById(R.id.itemDescription)
        checkBox = findViewById(R.id.itemCheckbox)

        this.title = title
        this.description = description
        this.imageID = imageID
        this.isChecked = isChecked
    }


}
