package javeriana.edu.co.taller2_localizacinypermisos

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityGoogleMapsBinding
import java.io.File
import java.lang.Math.toRadians
import java.time.LocalDateTime
import kotlin.math.*

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback
{
    // Para escribir en la ubicacion
    data class Ubicacion(val latitud: Double, val longitud: Double, val fechaHora: LocalDateTime)

    // Binding
    private lateinit var binding: ActivityGoogleMapsBinding

    // Location
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Objeto estatico que pertenece a una clase  y que puede ser accedido desde cualquier parte del programa sin necesidad de crear una instancia de la clase.
    companion object
    {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var lastLatitude = 0.0
        var lastLongitude  = 0.0

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    private fun calculateDistance(location: Location, lastLatitude: Double, lastLongitude: Double)
    {
        //Toast.makeText(baseContext, "Latitude: " + location.latitude + "Longuitude: " + location.longitude, Toast.LENGTH_SHORT).show()

        Log.i("Taller 2", "Calculate distance between 2 coordenates")

        val currentLatitude = location.latitude
        val currentLongitude = location.longitude
        val radius = 0.03 // 30 metros de distancia
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

            var writeStatus = permisoEscritura()
            Log.i("Taller 2", "Write status: $writeStatus")

            if (writeStatus)
            {
                agregarUbicacion(currentLatitude, currentLongitude)
            }
        }
    }


    fun agregarUbicacion(latitud: Double, longitud: Double)
    {
        // Crear objeto Gson para serializar/deserializar objetos JSON
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        // Crear archivo si no existe
        val archivo = File("ubicaciones.json")
        if (!archivo.exists()) {
            archivo.createNewFile()
        }

        // Leer contenido del archivo y convertir a lista de Ubicacion (si el archivo no está vacío)
        val ubicacionesAnteriores = if (archivo.length() > 0) {
            gson.fromJson(archivo.readText(), Array<Ubicacion>::class.java).toList()
        } else {
            emptyList()
        }

        // Crear nueva instancia de Ubicacion y agregar a la lista anterior
        val nuevaUbicacion = Ubicacion(latitud, longitud, LocalDateTime.now())
        val ubicacionesNuevas = ubicacionesAnteriores + nuevaUbicacion

        // Serializar lista de ubicaciones y escribir en el archivo
        val json = gson.toJson(ubicacionesNuevas)
        archivo.writeText(json)
    }


















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


    private fun permisoEscritura() : Boolean
    {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause()
    {
        super.onPause()
        stopLocationUpdates()
    }

    // Para dejar de recibir actualizaciones
    private fun stopLocationUpdates()
    {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}