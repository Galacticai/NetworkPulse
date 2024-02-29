package com.galacticai.networkpulse.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

@Preview(showBackground = true)
@Composable
fun NoRecordsMessage() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FaIcon(
            FaIcons.Ghost,
            size = 64.dp,
            tint = colorResource(R.color.primaryContainer)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.no_records),
            fontSize = 18.sp,
            color = colorResource(R.color.primary)
        )
    }
}