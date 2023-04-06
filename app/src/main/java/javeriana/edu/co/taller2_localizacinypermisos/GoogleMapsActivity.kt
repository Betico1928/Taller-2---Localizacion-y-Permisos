package javeriana.edu.co.taller2_localizacinypermisos

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityGoogleMapsBinding
import java.io.File
import java.lang.Math.toRadians
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*


class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener
{
    // Objeto estatico que pertenece a una clase  y que puede ser accedido desde cualquier parte del programa sin necesidad de crear una instancia de la clase.
    companion object
    {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding: ActivityGoogleMapsBinding

    // Location
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor




    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var lastLatitude = 0.0
        var lastLongitude  = 0.0

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //checkStoragePermission()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult)
            {
                val location = locationResult.lastLocation
                val latLng = location?.let{
                    LatLng(location.latitude, it.longitude)
                }

                googleMap.clear()


                latLng?.let {
                    MarkerOptions().position(it).title("Mi ubicación")
                }?.let { googleMap.addMarker(it)
                    calculateDistance(location, lastLatitude, lastLongitude)
                    lastLatitude = location.latitude
                    lastLongitude = location.longitude }

                // Real time zoom
                /*
                latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                    ?.let { googleMap.moveCamera(it) }
                */
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause()
    {
        super.onPause()
        stopLocationUpdates()
        sensorManager.unregisterListener(this)
    }

    private fun calculateDistance(location: Location, lastLatitude: Double, lastLongitude: Double)
    {
        //Toast.makeText(baseContext, "Latitude: " + location.latitude + "Longuitude: " + location.longitude, Toast.LENGTH_SHORT).show()

        Log.i("Taller 2", "Calculate distance between 2 coordenates")

        val currentLatitude = location.latitude
        val currentLongitude = location.longitude
        val radius = 30.0 // 30 km de distancia
        val earthRadiusKm = 6371

        val dLat = toRadians(currentLatitude - lastLatitude)
        val dLon = toRadians(currentLongitude - lastLongitude)

        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(toRadians(lastLatitude)) * cos(toRadians(currentLatitude))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))


        val distanceBetweenTwoCoordenates = earthRadiusKm * c

        if (distanceBetweenTwoCoordenates <= radius)
        {
            Toast.makeText(baseContext, "ES MENOR JAJAJA", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(baseContext, "ES MAYOR JAJAJA", Toast.LENGTH_SHORT).show()
            //escribirArchivoJson(currentLatitude, currentLongitude)
        }
    }

    private fun escribirArchivoJson(latitud: Double, longitud: Double)
    {
        val archivo = File("ubicaciones.json")
        val fechaHora = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val fechaHoraFormateada = fechaHora.format(formatter)
        val json = "{\"latitud\":$latitud,\"longitud\":$longitud,\"fechaHora\":\"$fechaHoraFormateada\"}"
        if (archivo.exists()) {
            val contenidoActual = archivo.readText()
            archivo.writeText("$contenidoActual,$json")
        } else {
            archivo.writeText("[$json]")
        }
    }

    /*
    private fun checkStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(baseContext, "Permiso aceptado", Toast.LENGTH_SHORT).show()
        }
        else
        {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("This permission is needed because of this and that")
                .setPositiveButton(
                    "ok"
                ) { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                }
                .setNegativeButton(
                    "cancel"
                ) { dialog, which -> dialog.dismiss() }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    /*private fun requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            AlertDialog.Builder(this).setTitle("Permission Needed").setTitle("Se necesita el permiso para escribir en archivo JSON")
                .setPositiveButton("OK", DialogInterface.OnClickListener(DialogInterface.BUTTON_POSITIVE)
                {

                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener()
                {
                    dialogInterface, i ->  
                })

        }
        else
        {
            //ActivityCompat.requestPermissions(this, String())
        }
    }

     */

     */




















    /*
    private fun getCurrentLocation()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
        else
        {
            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            /*
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? -> location?.let {

                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Tu ubicación"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } ?: run {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
            }

             */
        }
    }

     */




















    override fun onMapReady(map: GoogleMap)
    {
        googleMap = map
        setMapStyle(isDarkModeOn())

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // Gestures
            googleMap.uiSettings.isZoomGesturesEnabled = true

            // Zoom controls
            googleMap.uiSettings.isZoomControlsEnabled = true

            // Show current location
            googleMap.isMyLocationEnabled = true


            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->

                if (location != null)
                    {
                        val latLng = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(MarkerOptions().position(latLng).title("Mi ubicación"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }

            startLocationUpdates()
        }
        else
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun startLocationUpdates()
    {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                onMapReady(googleMap)
            }
        }
    }

    // Para dejar de recibir actualizaciones
    private fun stopLocationUpdates()
    {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0]
            setMapStyle(lightValue < 2000f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun isDarkModeOn(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun setMapStyle(isDarkMode: Boolean) {
        val styleRes = if (isDarkMode) R.raw.map_style_dark else R.raw.map_style_light
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, styleRes))
            if (!success) {
                println("Error setting map style.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}