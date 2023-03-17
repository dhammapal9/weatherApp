package com.ishu.weatherapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ishu.weatherapp.data.models.GenericResponse
import com.ishu.weatherapp.data.prefstore.AppPreference
import com.ishu.weatherapp.databinding.ActivityMainBinding
import com.ishu.weatherapp.ui.home.adapter.WeatherAdapter
import com.ishu.weatherapp.ui.home.viewmodel.LocationViewModel
import com.ishu.weatherapp.utils.NetworkResult
import com.ishu.weatherapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mypref: AppPreference

    private lateinit var binding: ActivityMainBinding
    var cityName = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationViewModel =
            ViewModelProvider(this)[LocationViewModel::class.java]

        // Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.rcWeather!!.layoutManager = LinearLayoutManager(this)
        binding.rcWeather!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.btnSearch.setOnClickListener {
            if(binding.txtSearch.text.toString() != ""){
                if(Utils.isNetworkAvailable(this)){
                    binding.pbData.visibility = View.VISIBLE
                    binding.llMain.visibility = View.GONE
                    binding.lblNoData.visibility = View.GONE
                    binding.lblcityName.visibility = View.GONE
                    locationViewModel.getWeatherAvailable(binding.txtSearch.text.toString(),"","")
                    cityName=binding.txtSearch.text.toString()
                }else{
                    Toast.makeText(this@MainActivity, "No Internet Access.", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this@MainActivity, "Enter City Name.", Toast.LENGTH_LONG).show()
            }

        }

        obtainLocation()
        checkLocationPermission()
        observeList()
    }

    private fun observeList() {
        locationViewModel.response.observe(this, ::loadWeather)
    }

    private fun loadWeather(networkResult: NetworkResult<GenericResponse>?) {
        networkResult?.let {
            when (it) {
                is NetworkResult.Success -> {
                    binding.pbData.visibility = View.GONE
                    val weatherData = (it.data as GenericResponse)
                    if(weatherData.cod == 200){

                        mypref.setStoredTag(mypref.CITY_SEARCH,cityName)

                        binding.lblcityName.visibility = View.VISIBLE
                        binding.lblNoData.visibility = View.GONE
                        binding.llMain.visibility = View.VISIBLE
                        setData(weatherData)

                        var weatherIcon = weatherData.weather

                        if(weatherIcon.size > 0) {
                            binding.rcWeather!!.adapter = weatherIcon?.let {
                                WeatherAdapter(this, weatherIcon) {

                                }
                            }
                        }else{ }
                        //Toast.makeText(this, weatherData.main?.temp.toString(), Toast.LENGTH_SHORT).show()
                    }else{
                        binding.lblNoData.visibility = View.VISIBLE
                        binding.llMain.visibility = View.GONE
                        //Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show()
                    }
                }
                is NetworkResult.Error ->
                    it.message?.let {

                    binding.pbData.visibility = View.GONE
                        binding.lblNoData.visibility = View.VISIBLE
                        binding.llMain.visibility = View.GONE
                    }
                is NetworkResult.Loading -> {
//                        TODO : Major - Implement Loading scenario once requirement available
                }
                else -> {
                }
            }
        }
    }

    private fun setData(weatherData: GenericResponse) {

        if(Utils.isOkToShow(cityName))
            binding.lblcityName.text = cityName

        if(Utils.isOkToShow(weatherData.coord?.lat.toString()))
            binding.tvLatitude.text = weatherData.coord?.lat.toString()

        if(Utils.isOkToShow(weatherData.coord?.lon.toString()))
            binding.tvLongitude.text = weatherData.coord?.lon.toString()

        if(Utils.isOkToShow(weatherData.base))
            binding.tvBase.text = weatherData.base

        if(Utils.isOkToShow(weatherData.main?.temp.toString()))
            binding.tvTemp.text = weatherData.main?.temp.toString()

        if(Utils.isOkToShow(weatherData.main?.feelsLike.toString()))
            binding.tvFeelslike.text = weatherData.main?.feelsLike.toString()

        if(Utils.isOkToShow(weatherData.main?.tempMin.toString()))
            binding.tvTempMin.text = weatherData.main?.tempMin.toString()

        if(Utils.isOkToShow(weatherData.main?.tempMax.toString()))
            binding.tvTempMax.text = weatherData.main?.tempMax.toString()

        if(Utils.isOkToShow(weatherData.main?.pressure.toString()))
            binding.tvPressure.text = weatherData.main?.pressure.toString()

        if(Utils.isOkToShow(weatherData.main?.humidity.toString()))
            binding.tvHumidity.text = weatherData.main?.humidity.toString()

        if(Utils.isOkToShow(weatherData.wind?.speed.toString()))
            binding.tvSpeed.text = weatherData.wind?.speed.toString()

        if(Utils.isOkToShow(weatherData.wind?.deg.toString()))
            binding.tvDeg.text = weatherData.wind?.deg.toString()

        if(Utils.isOkToShow(weatherData.clouds?.all.toString()))
            binding.tvAll.text = weatherData.clouds?.all.toString()

        if(Utils.isOkToShow(weatherData.sys?.country.toString()))
            binding.tvCountry.text = weatherData.sys?.country.toString()

        if(Utils.isOkToShow(weatherData.sys?.sunrise.toString()))
            binding.tvSunrise.text = weatherData.sys?.sunrise.toString()

        if(Utils.isOkToShow(weatherData.sys?.sunset.toString()))
            binding.tvSunset.text = weatherData.sys?.sunset.toString()

        if(Utils.isOkToShow(weatherData.name))
            binding.tvName.text = weatherData.name.toString()

    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, Please accept to use location.")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun obtainLocation() {
        if(Utils.isOkToShow(mypref.getStoredTag(mypref.CITY_SEARCH))){
            if(Utils.isNetworkAvailable(this)) {
                binding.pbData.visibility = View.VISIBLE
                cityName = mypref.getStoredTag(mypref.CITY_SEARCH)
                locationViewModel.getWeatherAvailable(cityName, "", "")
            }
        }else {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    var cityNameGeo = ""
                    var stateNameGeo = ""
                    var countryNameGeo = ""
                    var countryCode = ""
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address>? =
                            geocoder.getFromLocation(location!!.latitude, location.longitude, 1)
                        cityNameGeo = addresses!![0].locality
                        countryCode = addresses[0].countryCode
                        stateNameGeo = addresses!![0].getAddressLine(1)
                        countryNameGeo = addresses!![0].getAddressLine(2)
                    } catch (e: Exception) {
                    }
                    if(Utils.isOkToShow(cityNameGeo)) {
                        if(Utils.isOkToShow(countryCode)){
                            cityName = "$cityNameGeo,$countryCode"
                        }else{
                            cityName = cityNameGeo
                        }
                        binding.pbData.visibility = View.VISIBLE
                        locationViewModel.getWeatherAvailable(cityName, "", "")
                    }
                    //Toast.makeText(this, countryCode+"="+cityNameGeo, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        obtainLocation()

                    }

                } else {

                    // Check if we are in a state where the user has denied the permission and
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}