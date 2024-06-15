package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView

import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar
        supportActionBar?.hide()

        fetchWeatherData("Jaipur")
        
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "e3a37fb53b7e468b8b2b973a3ba7b443", "metric")
        response.enqueue(object : Callback<weatherapp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp :$maxTemp °C"
                    binding.minTemp.text = "Min Temp :$minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hpa"
                    binding.condition.text = condition
                    binding.day.text =dayName(System.currentTimeMillis())
                    binding.date.text =date()
                    binding.cityName.text = "$cityName"

                    changeImagesAccordingtoWeatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


        }


//    @SuppressLint("ResourceType")
//    private fun changeImagesAccordingtoWeatherCondition(condiotions: String) {
//        when(condiotions){
//
//            "Haze" ->{
//                binding.root.setBackgroundResource(R.drawable.colud_background)
//                binding.root.setBackgroundResource(R.raw.cloud)
//
//            }
//        }
//        binding.lottieAnimationView.playAnimation()
//    }


@SuppressLint("ResourceType")
private fun changeImagesAccordingtoWeatherCondition(conditions: String) {
    val backgroundLayout = findViewById<ConstraintLayout>(R.id.backgroundLayout)
    val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)


    when (conditions) {
        "Haze", "Partly Clouds","OverCase","Mist","Foggy","Clouds" -> {
            backgroundLayout.setBackgroundResource(R.drawable.colud_background)
            lottieAnimationView.setAnimation(R.raw.cloud)
        }
        "Light Snow", "Moderate Snow","heavy Snow","Blizzard" -> {
            backgroundLayout.setBackgroundResource(R.drawable.snow_background)
            lottieAnimationView.setAnimation(R.raw.snow)
        }
        "Rain","Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
            backgroundLayout.setBackgroundResource(R.drawable.rain_background)
            lottieAnimationView.setAnimation(R.raw.rain)
        }
        // Add more cases for other weather conditions if needed
        else -> {
            // Default to a sunny background
            backgroundLayout.setBackgroundResource(R.drawable.sunny_background)
            lottieAnimationView.setAnimation(R.raw.sun)
        }
    }
//    binding.lottieAnimationView.playAnimation()
    lottieAnimationView.playAnimation()
}

//    @SuppressLint("ResourceType")
//    private fun changeImagesAccordingtoWeatherCondition(conditions: String) {
//        // Use the binding object directly to access views
//        when (conditions) {
//            "haze", "cloud" -> {
////                binding.root.setBackgroundResource(R.drawable.colud_background)
////                binding.lottieAnimationView.setAnimation(R.raw.cloud)
//                Log.d("WeatherDebug", "Matching haze/cloud condition")
//            }
//            "rain" -> {
//                binding.root.setBackgroundResource(R.drawable.rain_background)
//                binding.lottieAnimationView.setAnimation(R.raw.rain)
//            }
//            // Add more cases for other weather conditions if needed
//
//            else -> {
//                // Default to a generic background and animation
//                binding.root.setBackgroundResource(R.drawable.sunny_background)
//                binding.lottieAnimationView.setAnimation(R.raw.sun)
//            }
//        }
//        // Play the Lottie animation
//        binding.lottieAnimationView.playAnimation()
//    }



    private fun date(): String {

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
