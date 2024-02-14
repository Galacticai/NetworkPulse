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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.graphing.bar_chart.BarData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@Composable
fun RecordsView(
    modifier: Modifier = Modifier,
    records: List<SpeedRecord>,
    parser: ((SpeedRecord) -> BarData)? = null,
    startWithSummary: Boolean = Setting.Summarize.defaultValue
) {
    val ctx = LocalContext.current
    var showSummary by rememberSaveable { mutableStateOf(startWithSummary) }
    LaunchedEffect(Unit) { showSummary = Setting.Summarize.get(ctx) }

    Surface(
        color = colorResource(R.color.background),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        AnimatedContent(targetState = showSummary, label = "RecordsView") {
            Box {
                if (it) RecordsSummary(records)
                else RecordsChart(records = records, parser = parser)
                SwitcherButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    isSummary = it,
                ) { showSummary = !showSummary }
            }
        }
    }
}

@Composable
private fun SwitcherButton(modifier: Modifier, isSummary: Boolean, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 3.dp)
            .shadow(
                10.dp,
                shape = RoundedCornerShape(50),
                ambientColor = colorResource(R.color.background),
                spotColor = colorResource(R.color.background),
            )
            .then(modifier),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.secondaryContainer).copy(.6f),
            contentColor = colorResource(R.color.secondary)
        ),
    ) {
        FaIcon(
            if (isSummary) FaIcons.ChartBar else FaIcons.InfoCircle,
            size = 18.dp,
            tint = colorResource(R.color.secondary)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            stringResource(if (isSummary) R.string.details else R.string.summary),
            fontSize = 12.sp
        )
    }
}