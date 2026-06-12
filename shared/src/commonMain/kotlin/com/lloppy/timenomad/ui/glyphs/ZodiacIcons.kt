package com.lloppy.timenomad.ui.glyphs

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.lloppy.timenomad.astro.model.ZodiacSign
import com.lloppy.timenomad.resources.Res
import com.lloppy.timenomad.resources.sign_aquarius
import com.lloppy.timenomad.resources.sign_aries
import com.lloppy.timenomad.resources.sign_cancer
import com.lloppy.timenomad.resources.sign_capricorn
import com.lloppy.timenomad.resources.sign_gemini
import com.lloppy.timenomad.resources.sign_leo
import com.lloppy.timenomad.resources.sign_libra
import com.lloppy.timenomad.resources.sign_pisces
import com.lloppy.timenomad.resources.sign_sagittarius
import com.lloppy.timenomad.resources.sign_scorpio
import com.lloppy.timenomad.resources.sign_taurus
import com.lloppy.timenomad.resources.sign_virgo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

fun ZodiacSign.iconResource(): DrawableResource = when (this) {
    ZodiacSign.ARIES -> Res.drawable.sign_aries
    ZodiacSign.TAURUS -> Res.drawable.sign_taurus
    ZodiacSign.GEMINI -> Res.drawable.sign_gemini
    ZodiacSign.CANCER -> Res.drawable.sign_cancer
    ZodiacSign.LEO -> Res.drawable.sign_leo
    ZodiacSign.VIRGO -> Res.drawable.sign_virgo
    ZodiacSign.LIBRA -> Res.drawable.sign_libra
    ZodiacSign.SCORPIO -> Res.drawable.sign_scorpio
    ZodiacSign.SAGITTARIUS -> Res.drawable.sign_sagittarius
    ZodiacSign.CAPRICORN -> Res.drawable.sign_capricorn
    ZodiacSign.AQUARIUS -> Res.drawable.sign_aquarius
    ZodiacSign.PISCES -> Res.drawable.sign_pisces
}

@Composable
fun ZodiacIcon(sign: ZodiacSign, tint: Color, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(sign.iconResource()),
        contentDescription = sign.displayName,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier,
    )
}
