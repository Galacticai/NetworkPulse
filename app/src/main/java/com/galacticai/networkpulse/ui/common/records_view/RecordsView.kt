package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@Composable
fun RecordsView(records: List<SpeedRecord>, graphSize: Int) {
    val ctx = LocalContext.current
    var showSummary by rememberSaveable { mutableStateOf(Setting.Summarize.defaultValue) }
    LaunchedEffect(Unit) { showSummary = Setting.Summarize.get(ctx) }

    Surface(
        color = colorResource(R.color.background),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(targetState = showSummary, label = "RecordsView") {
            Box {
                if (it) RecordsSummary(records = records)
                else RecordsChart(hourRecords = records, graphSize = graphSize)

                TextButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 10.dp),
                    onClick = { showSummary = !showSummary },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.secondaryContainer).copy(.5f),
                        contentColor = colorResource(R.color.secondary)
                    )
                ) {
                    FaIcon(if (it) FaIcons.ListUl else FaIcons.InfoCircle)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        stringResource(if (it) R.string.details else R.string.summary),
                        fontSize = 12.sp
                    )
                }
            }

        }
    }
}