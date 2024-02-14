package com.galacticai.networkpulse.ui.common

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.theme.GalacticTheme

@Composable
fun AppTitle(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    //iconSize: Dp = 32.dp
) {
    val isArabic = (LocalContext.current as AppCompatActivity)
        .resources.configuration
        .locales[0].isO3Language == "ara"

    @Composable
    fun network() = Text(
        text = stringResource(R.string.title_network),
        color = colorResource(R.color.primary),
        fontSize = fontSize,
    )

    @Composable
    fun spacer() = Spacer(modifier = Modifier.width(1.dp))

    @Composable
    fun pulse() = Text(
        text = stringResource(R.string.title_pulse),
        fontSize = fontSize,
        color = colorResource(R.color.secondary)
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        //        Image(
        //            painter = painterResource(R.drawable.round_monitor_heart_24),
        //            contentDescription = stringResource(R.string.app_name),
        //            colorFilter = ColorFilter.tint(colorResource(R.color.secondary)),
        //            modifier = Modifier.size(iconSize, iconSize)
        //        )
        //        Spacer(modifier = Modifier.width(10.dp))

        if (isArabic) {
            pulse()
            spacer()
            network()
        } else {
            network()
            spacer()
            pulse()
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CustomLinearLayoutPreview() {
    GalacticTheme {
        AppTitle()
    }
}
