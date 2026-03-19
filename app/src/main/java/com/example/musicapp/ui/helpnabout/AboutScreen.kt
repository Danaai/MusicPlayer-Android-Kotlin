package com.example.musicapp.ui.helpnabout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R

@Composable
fun AboutRoute(
    onBackClick: () -> Unit
) {
    AboutScreen(onBackClick = onBackClick)
}

@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        AboutTopBar(onBackClick)

        Spacer(Modifier.height(32.dp))
        AboutHeader()

        Spacer(Modifier.height(32.dp))
        AboutSection(
            titleId = R.string.title_about_1,
            contentInt = R.string.content_about_1
        )

        Spacer(Modifier.height(28.dp))
        AboutSection(
            titleId = R.string.title_about_2,
            contentInt = R.string.content_about_2
        )

        Spacer(Modifier.height(28.dp))
        AboutSection(
            titleId = R.string.title_about_3,
            contentInt = R.string.content_about_3
        )

        Spacer(Modifier.height(32.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )
        AboutFooter(
            onTermsClick = {},
            onPrivacyClick = {},
            onOpenSourceClick = {},
            onWebsiteClick = {},
            onFacebookClick = {},
            onContactClick = {}
        )
    }
}

@Composable
fun AboutTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }

        Text(
            text = "About Us",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun AboutHeader() {
    Column {
        Text(
            text = "MUSIC APP",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "VERSION " + stringResource(R.string.version_app),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4F46E5),
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun AboutSection(
    titleId: Int,
    contentInt: Int
) {
    Column {
        Text(
            text = stringResource(titleId),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4F46E5),
            letterSpacing = 0.8.sp
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(contentInt),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = Color(0xFF1F2937)
        )
    }
}

@Composable
fun AboutFooter(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onOpenSourceClick: () -> Unit,
    onWebsiteClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FooterText(
            text = "Terms of Service",
            subText = "Details regarding rights and terms of use",
            onClick = onTermsClick
        )
        FooterText(
            text = "TPrivacy Policy",
            subText = "Details on how we collect and process data.",
            onClick = onPrivacyClick
        )
        FooterText(
            text = "Open source license",
            subText = "Honoring the contributions from the programming community.",
            onClick = onOpenSourceClick
        )

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FooterLink("WEBSITE", onWebsiteClick)
            FooterLink("FACEBOOK", onFacebookClick)
            FooterLink("CONTACT US", onContactClick)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "© 2023 Music App. All rights reserved.",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun FooterText(
    text: String,
    subText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subText,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun FooterLink(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = Color(0xFF3D5AFE),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun AboutPreview() {
    AboutScreen(
        onBackClick = {}
    )
}