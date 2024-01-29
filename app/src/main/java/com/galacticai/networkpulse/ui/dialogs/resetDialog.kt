package com.galacticai.networkpulse.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import com.galacticai.networkpulse.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun resetDialog(context: Context, name: String? = null, onYes: DialogInterface.OnClickListener) {
    val d = MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.reset))
        .setPositiveButton(android.R.string.yes, onYes)
        .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
    if (name != null) d.setMessage(name)
    d.show()
}