package com.example.coffeeapp.screens

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapScreen() {
    val context = LocalContext.current

    //Создаем и настраиваем карту один раз
    val mapView = remember {
        MapView(context).apply {
            map.move(
                CameraPosition(
                    Point(55.751244, 37.618423), // Центр Москвы
                    14.0f,
                    0.0f,
                    0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
            // Ставим метку
            map.mapObjects.addPlacemark(Point(55.751244, 37.618423))
        }
    }

    //Управляем стартами и остановками движка Яндекса синхронно с экраном
    DisposableEffect(key1 = mapView) {
        mapView.onStart()
        onDispose {
            mapView.onStop()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // 3. Передаем в AndroidView без запрещенных проверок
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { _ ->
                // Если карту переключали, у нее мог остаться старый стандартный ViewGroup-родитель.
                // Безопасно удаляем ее из него стандартными средствами Android SDK:
                (mapView.parent as? ViewGroup)?.removeView(mapView)

                mapView // Возвращаем саму карту
            }
        )
    }
}