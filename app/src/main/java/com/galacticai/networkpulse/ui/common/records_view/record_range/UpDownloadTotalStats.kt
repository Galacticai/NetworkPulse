package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import com.galacticai.networkpulse.models.records_summary.RecordsSummary

@Composable
fun UpDownloadTotalStats(summary: RecordsSummary) {
    StatContainer(
        stringResource(R.string.total),
        listOf {
            //            val up = BitValue(
            //                summary.upTotalSize.toFloat(),
            //                BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
            //            ).toNearestUnit()
            val down = BitValue(
                //TODO: BitValue.value better be Double, since Float could overflow if user sets a large download size
                summary.downTotalSize.toFloat(),
                BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
            ).toNearestUnit()
            //            Stat(stringResource(R.string.upload_noun), up.toString())
            Stat(stringResource(R.string.download_noun), down.toString())
        }
    )
}
