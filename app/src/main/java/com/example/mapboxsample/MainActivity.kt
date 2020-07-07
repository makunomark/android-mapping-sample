package com.example.mapboxsample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class MainActivity : AppCompatActivity(), DirectionResponseListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Mapbox.getInstance(
            this,
            BuildConfig.MAPBOX_DOWNLOADS_TOKEN
        )
    }

    fun openNavigationUiSdk(view: View) {
        openNavigationUi()
    }

    private fun openNavigationUi() {
        val origin = Point.fromLngLat(36.86151, -1.22841)
        val destination = Point.fromLngLat(36.785, -1.302)

        NavigationRoute.builder(this)
            .accessToken(BuildConfig.MAPBOX_DOWNLOADS_TOKEN)
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    response.body()?.let { this@MainActivity.onDirectionResponse(it) }
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Timber.d(t.localizedMessage)
                }
            })
    }

    override fun onDirectionResponse(directionsResponse: DirectionsResponse) {
        val options = NavigationLauncherOptions.builder()
            .directionsRoute(directionsResponse.routes()[0])
            .shouldSimulateRoute(true)
            .build()
        NavigationLauncher.startNavigation(this, options)
    }

    fun openEmbededNavigation(view: View) {
        startActivity(Intent(this, EmbeddedNavigationActivity::class.java))
    }

    fun openWaypointNavigation(view: View) {
        startActivity(Intent(this, WaypointNavigationActivity::class.java))
    }
}

interface DirectionResponseListener {
    fun onDirectionResponse(directionsResponse: DirectionsResponse)
}