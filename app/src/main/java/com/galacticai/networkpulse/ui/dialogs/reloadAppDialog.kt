package com.galacticai.networkpulse.ui.dialogs

import android.content.Context
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.restartApp
import com.galacticai.networkpulse.services.PulseService
import com.galacticai.networkpulse.ui.MainActivity
import com.galacticai.networkpulse.ui.PrepareActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun reloadAppDialog(context: Context) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.reload))
        .setMessage(context.getString(R.string.do_you_want_to_reload_the_app_to_apply_the_changes))
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
        .setPositiveButton(android.R.string.yes) { _, _ ->
            PulseService.stop(context)
            restartApp(context as MainActivity, PrepareActivity::class.java)
        }
        .show()
}