package com.example.eventconnect.ui.home

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OpenStreetMapView(
    context: Context,
    latitude: Double,
    longitude: Double
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                controller.setZoom(16.5)
                controller.setCenter(GeoPoint(latitude, longitude))

                // Clean UI
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false

                overlays.clear()

                val marker = Marker(this).apply {
                    position = GeoPoint(latitude, longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Pickup Location"
                }

                overlays.add(marker)

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}
