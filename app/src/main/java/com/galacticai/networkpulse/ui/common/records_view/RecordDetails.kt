package com.galacticai.networkpulse.ui.common.records_view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.format
import com.galacticai.networkpulse.common.models.data_value.BitUnit
import com.galacticai.networkpulse.common.models.data_value.BitUnitBase
import com.galacticai.networkpulse.common.models.data_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.data_value.BitValue
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.ui.common.durationSuffixes
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun RecordDetails(
    record: SpeedRecord,
    modifier: Modifier = Modifier,
    headBodySpacing: Dp = 0.dp,
    bodyBG: Color = colorResource(R.color.background),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Header(record)
        if (headBodySpacing > 0.dp)
            Spacer(modifier = Modifier.padding(headBodySpacing))
        Body(record, bodyBG)
    }
}

@Composable
private fun Header(record: SpeedRecord) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val statusLabel: String
        val statusColor: Color
        val statusColorBG: Color
        if (record.isSuccess) {
            statusLabel = stringResource(R.string.successAdjective)
            statusColor = colorResource(R.color.onSuccessContainer)
            statusColorBG = colorResource(R.color.successContainer)
        } else if (record.isTimeout) {
            statusLabel = stringResource(R.string.timeout)
            statusColor = colorResource(R.color.onWarningContainer)
            statusColorBG = colorResource(R.color.warningContainer)
        } else {
            statusLabel = stringResource(R.string.failAdjective)
            statusColor = colorResource(R.color.onErrorContainer)
            statusColorBG = colorResource(R.color.errorContainer)
        }
        Text(
            text = stringResource(R.string.record_details),
            color = statusColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            color = statusColor.copy(.2f),
        )
        Surface(
            modifier = Modifier.padding(2.dp),
            border = BorderStroke(1.dp, statusColor),
            color = statusColorBG,
            shape = CircleShape,
            shadowElevation = 5.dp,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 5.dp)
                    .widthIn(min = 50.dp),
                text = statusLabel,
                color = statusColor,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun Body(record: SpeedRecord, bodyBG: Color) {
    val primaryContainer = colorResource(R.color.primaryContainer)
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, primaryContainer),
        shadowElevation = 5.dp,
        color = bodyBG,
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
        ) {
            @Composable
            fun row(content: @Composable () -> Unit) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) { content() }
            }

            @Composable
            fun stat(
                title: String, value: String,
                color: Color = colorResource(R.color.primary)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                    Divider(
                        modifier = Modifier
                            .widthIn(max = 50.dp)
                            .padding(vertical = 1.dp),
                        color = primaryContainer, thickness = 1.dp
                    )
                    Text(
                        if (value.contains('\n')) value else "$value\n",
                        color = color,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            val durationSuffixes = durationSuffixes(LocalContext.current)
            fun formatDuration(time: Long) = time.milliseconds.format(
                durationSuffixes,
                joint = " ",
                partsCount = 2,
            )
            row {
                val locale = Locale.getDefault()
                val date = SimpleDateFormat("dd/MM/yyyy\nEEEE", locale)
                    .format(record.time)
                val time = SimpleDateFormat("h:mm:ss\na", locale)
                    .format(record.time)
                val timeRelative = formatDuration(System.currentTimeMillis() - record.time) +
                        '\n' + stringResource(R.string.time_ago)

                stat(stringResource(R.string.date), date)
                stat(stringResource(R.string.time), time)
                stat(stringResource(R.string.relative_time), timeRelative)
            }

            @Composable
            fun duration() = stat(
                stringResource(R.string.duration),
                formatDuration(record.runtimeMS.toLong())
            )

            @Composable
            fun speed(v: String) = stat(stringResource(R.string.speed), v)

            @Composable
            fun size(v: String) = stat(stringResource(R.string.size), v)
            row {
                val speedValue: String
                val sizeValue: String
                if (record.isSuccess) {
                    val downSpeed = BitValue(
                        record.down!!,
                        BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
                    ).toNearestUnit()
                    //val up = BitValue(record.up!!, unit).toNearestUnit()
                    val downSpeedString = String.format("%.2f", downSpeed.value)
                    val perSecond = "/${durationSuffixes.seconds}"

                    val downSize = BitValue(record.downSize, downSpeed.unit).toNearestUnit()
                    val downSizeString = String.format("%.2f", downSize.value)

                    speedValue = "$downSpeedString\n${downSpeed.unit}$perSecond"
                    sizeValue = "$downSizeString\n${downSize.unit}"
                } else {
                    val failed = "[ ${stringResource(R.string.failed)} ]"
                    speedValue = failed
                    sizeValue = failed
                }
                duration()
                speed(speedValue)
                size(sizeValue)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RecordDetailsPreview1() {
    RecordDetails(SpeedRecord.Success(System.currentTimeMillis() - 55, 2000, 43.02132f, 210.91234f))
}

@Preview(showBackground = true)
@Composable
private fun RecordDetailsPreview2() {
    RecordDetails(SpeedRecord.Timeout(System.currentTimeMillis() - 55, 2800))
}

@Preview(showBackground = true)
@Composable
private fun RecordDetailsPreview3() {
    RecordDetails(SpeedRecord.Error(System.currentTimeMillis() - 55, 2800))
}
