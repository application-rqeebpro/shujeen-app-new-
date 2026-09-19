package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Visual brand logo component representing the Shajeen Travel & Tourism Agency
 * based on the official corporate logo with soaring airplane, dynamic ribbons,
 * and bold Arabic typography.
 */
@Composable
fun ShajeenBrandLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 80.dp,
    showText: Boolean = true,
    titleSize: Int = 20,
    subtitleSize: Int = 13
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.size(iconSize + 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_shajeen_logo),
                    contentDescription = "شعار وكالة شجين للسفريات والسياحة",
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "وكالة شجين",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF102E4C),
                    fontSize = titleSize.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "للسفريات والسياحة",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2EA6D6),
                    fontSize = subtitleSize.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
