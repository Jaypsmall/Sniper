package com.example.sniper

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

enum class SniperState {
    OFF, WINDOWED, FULLSCREEN
}

object ColorManager {
    fun colorToHex(color: Color): String {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        return String.format("#%02X%02X%02X", r, g, b)
    }

    fun isDark(color: Color): Boolean {
        val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
        return luminance < 0.5
    }
}

@Composable
fun Modifier.goldButtonStyle() = this.then(
    Modifier
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF8C6221),
                    Color(0xFFFFF3A8),
                    Color(0xFFC29B47)
                )
            ),
            shape = CircleShape
        )
        .border(1.5.dp, Color(0xFFF3E5AB), CircleShape)
)

@Composable
fun SniperMainScreen(
    currentColor: Color,
    isDarkMode: Boolean,
    isGoldMode: Boolean,
    uiAccentColor: Color,
    onColorCaptured: (Color) -> Unit,
    onColorConfirmed: (Color) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = Color(0xFF000000), // DarkPalette.Background
                drawerShape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF000000))
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(48.dp))
                        
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sniper_icono_nuevootro),
                                contentDescription = "Logo",
                                modifier = Modifier.size(100.dp)
                            )
                            
                            Text(
                                text = "SNIPER ENGINE",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFFFFF) // DarkPalette.TextPrimary
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF333333) // DarkPalette.Divider
                        )

                        val itemModifier = Modifier.height(52.dp).padding(horizontal = 12.dp)
                        val labelStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        NavigationDrawerItem(
                            label = { Text("Scanner", style = labelStyle) },
                            selected = true,
                            onClick = { scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent,
                                selectedContainerColor = (if (isGoldMode) Color(0xFFC29B47) else uiAccentColor).copy(alpha = 0.15f),
                                selectedTextColor = if (isGoldMode) Color(0xFFFFF3A8) else uiAccentColor,
                                selectedIconColor = if (isGoldMode) Color(0xFFC29B47) else uiAccentColor,
                                unselectedTextColor = Color(0xFF808080), // DarkPalette.TextSecondary
                                unselectedIconColor = Color(0xFF808080)
                            ),
                            modifier = itemModifier.then(
                                if (!isGoldMode) Modifier.border(0.5.dp, uiAccentColor.copy(alpha = 0.5f), CircleShape)
                                else Modifier
                            )
                        )

                        NavigationDrawerItem(
                            label = { Text("Favoritos", style = labelStyle) },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent, 
                                unselectedTextColor = Color(0xFF808080), 
                                unselectedIconColor = Color(0xFF808080)
                            ),
                            modifier = itemModifier
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF333333)
                        )

                        NavigationDrawerItem(
                            label = { Text("Ajustes", style = labelStyle) },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent, 
                                unselectedTextColor = Color(0xFF808080), 
                                unselectedIconColor = Color(0xFF808080)
                            ),
                            modifier = itemModifier
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Versión 1.0.1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF808080)
                        )
                        Text(
                            text = "Created by JAYLIZ with ❤️",
                            fontSize = 11.sp,
                            color = Color(0xFF808080).copy(0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SniperGodOverlay(
                isDarkMode = isDarkMode,
                isGoldMode = isGoldMode,
                currentColor = currentColor,
                uiAccentColor = uiAccentColor,
                onMenuClick = { scope.launch { drawerState.open() } },
                onColorCaptured = onColorCaptured,
                onColorConfirmed = onColorConfirmed
            )
        }
    }
}

@Composable
fun SniperGodOverlay(
    isDarkMode: Boolean,
    isGoldMode: Boolean,
    currentColor: Color,
    uiAccentColor: Color,
    onMenuClick: () -> Unit,
    onColorCaptured: (Color) -> Unit,
    onColorConfirmed: (Color) -> Unit
) {
    val fineBorder = BorderStroke(
        1.5.dp,
        if (isGoldMode) Color(0xFFC29B47) else Color.White.copy(0.3f)
    )
    val flashAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        CameraSniper(
            onColorCaptured = onColorCaptured,
            onColorConfirmed = {
                scope.launch {
                    flashAnim.snapTo(1f)
                    flashAnim.animateTo(0f, animationSpec = tween(300))
                }
                onColorConfirmed(it)
            }
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = flashAnim.value))
        )
        
        // BANNER SUPERIOR MODO DARK CON DETALLES DORADOS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.9f))
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color(0xFFC29B47),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(Modifier.width(8.dp))
                
                Image(
                    painter = painterResource(id = R.drawable.sniper_icono_nuevootro),
                    contentDescription = "Logo",
                    modifier = Modifier.size(42.dp)
                )
                
                Spacer(Modifier.width(10.dp))
                
                Text(
                    text = "SNIPER ENGINE",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFC29B47)
                    )
                )
            }

            // --- SEPARADOR ---
            Box(
                modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFC29B47).copy(alpha = 0.3f))
            )
        }

        // Tarjeta de Color encuadrada en la parte baja inicialmente
        var cardOffset by remember { mutableStateOf(Offset(0f, 250f)) } 

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(cardOffset.x.toInt(), cardOffset.y.toInt()) }
                .navigationBarsPadding()
                .width(200.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        cardOffset += dragAmount
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = fineBorder
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(currentColor)
                        .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(6.dp))
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ColorManager.colorToHex(currentColor).uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "RGB: ${(currentColor.red * 255).toInt()},${(currentColor.green * 255).toInt()},${(currentColor.blue * 255).toInt()}",
                        color = Color.LightGray,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = {
                        scope.launch {
                            flashAnim.snapTo(1f)
                            flashAnim.animateTo(0f, animationSpec = tween(300))
                        }
                        onColorConfirmed(currentColor)
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .then(if (isGoldMode) Modifier.goldButtonStyle() else Modifier.background(uiAccentColor, CircleShape))
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.Default.Check,
                        "Capture",
                        modifier = Modifier.size(14.dp),
                        tint = if (isGoldMode) Color(0xFF543B14) else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CameraSniper(onColorCaptured: (Color) -> Unit, onColorConfirmed: (Color) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var lastColor by remember { mutableStateOf(Color.White) }
    var crosshairOffset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    val nZ = (zoomRatio * zoom).coerceIn(1f, 10f)
                    zoomRatio = nZ
                    cameraControl?.setZoomRatio(nZ)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    crosshairOffset = Offset(
                        (crosshairOffset.x + dragAmount.x).coerceIn(
                            -containerSize.width / 2f + 50f,
                            containerSize.width / 2f - 50f
                        ),
                        (crosshairOffset.y + dragAmount.y).coerceIn(
                            -containerSize.height / 2f + 50f,
                            containerSize.height / 2f - 50f
                        )
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { onColorConfirmed(lastColor) }
            }
    ) {
        AndroidView(
            factory = { ctx ->
                val pV = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cP = cameraProviderFuture.get()
                    val p = Preview.Builder().build().also { it.surfaceProvider = pV.surfaceProvider }
                    val iA = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    iA.setAnalyzer(executor) { iP ->
                        val yB = iP.planes[0].buffer
                        val uB = iP.planes[1].buffer
                        val vB = iP.planes[2].buffer
                        val w = iP.width
                        val h = iP.height
                        
                        val containerRatio = containerSize.width.toFloat() / containerSize.height.toFloat()
                        val bufferRatio = h.toFloat() / w.toFloat()
                        
                        var scale: Float
                        var offsetX = 0f
                        var offsetY = 0f
                        if (containerRatio > bufferRatio) {
                            scale = containerSize.width.toFloat() / h.toFloat()
                            offsetY = (scale * w - containerSize.height) / 2f
                        } else {
                            scale = containerSize.height.toFloat() / w.toFloat()
                            offsetX = (scale * h - containerSize.width) / 2f
                        }
                        
                        val screenX = containerSize.width / 2f + crosshairOffset.x
                        val screenY = containerSize.height / 2f + crosshairOffset.y
                        val stretchedX = screenX + offsetX
                        val stretchedY = screenY + offsetY
                        val normX = stretchedX / (h * scale)
                        val normY = stretchedY / (w * scale)
                        
                        val centerX = (normY * w).toInt().coerceIn(0, w - 1)
                        val centerY = ((1f - normX) * h).toInt().coerceIn(0, h - 1)
                        
                        var sY = 0L
                        var sU = 0L
                        var sV = 0L
                        val s = 8
                        val startX = (centerX - s / 2).coerceIn(0, w - s)
                        val startY = (centerY - s / 2).coerceIn(0, h - s)
                        
                        for (x in 0 until s) {
                            for (y in 0 until s) {
                                val px = startX + x
                                val py = startY + y
                                sY += yB.get(py * w + px).toInt() and 0xFF
                                val uvI = (py / 2) * (iP.planes[1].rowStride) + (px / 2) * (iP.planes[1].pixelStride)
                                if (uvI < uB.remaining()) sU += uB.get(uvI).toInt() and 0xFF
                                if (uvI < vB.remaining()) sV += vB.get(uvI).toInt() and 0xFF
                            }
                        }
                        
                        val aY = (sY / (s * s)).toFloat()
                        val aU = (sU / (s * s)).toFloat() - 128f
                        val aV = (sV / (s * s)).toFloat() - 128f
                        
                        val r = (aY + 1.402f * aV).coerceIn(0f, 255f)
                        val g = (aY - 0.344136f * aU - 0.714136f * aV).coerceIn(0f, 255f)
                        val b = (aY + 1.772f * aU).coerceIn(0f, 255f)
                        
                        val nC = Color(r / 255f, g / 255f, b / 255f)
                        val lC = Color(
                            lastColor.red * 0.95f + nC.red * 0.05f,
                            lastColor.green * 0.95f + nC.green * 0.05f,
                            lastColor.blue * 0.95f + nC.blue * 0.05f
                        )
                        lastColor = lC
                        onColorCaptured(lC)
                        iP.close()
                    }
                    try {
                        cP.unbindAll()
                        val c = cP.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, p, iA)
                        cameraControl = c.cameraControl
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, executor)
                pV
            },
            modifier = Modifier.fillMaxSize()
        )
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f + crosshairOffset.x, size.height / 2f + crosshairOffset.y)
            drawCircle(Color.White, radius = 14.dp.toPx(), center = center, style = Stroke(2.5.dp.toPx()))
            drawLine(
                Color.White,
                Offset(center.x - 28.dp.toPx(), center.y),
                Offset(center.x + 28.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                Color.White,
                Offset(center.x, center.y - 28.dp.toPx()),
                Offset(center.x, center.y + 28.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(lastColor, radius = 4.dp.toPx(), center = center)
        }
    }
}
