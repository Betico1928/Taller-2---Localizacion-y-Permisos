package javeriana.edu.co.taller2_localizacinypermisos

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityGoogleMapsBinding
import java.time.LocalDateTime

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    // Para escribir en la ubicacion
    data class Ubicacion(val latitud: Double, val longitud: Double, val fechaHora: LocalDateTime)

    // Binding
    private lateinit var bindingGoogleMaps : ActivityGoogleMapsBinding

    // mMap
    private lateinit var mMap: GoogleMap

    // Location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    companion object
    {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingGoogleMaps = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(bindingGoogleMaps.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap

        // Zoom
        mMap.uiSettings.isZoomGesturesEnabled = true

        // Arrancar mapa
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap()
    {
        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this){ location ->
            if (location != null)
            {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng)
    {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("Ubicacion actual: $currentLatLong")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker): Boolean {return false}

}






































/*

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

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                agregarUbicacion(currentLatitude, currentLongitude)
                Log.i("Taller 2", "Escrito en archivo JSON")
            }
            else
            {
                Log.i("Taller 2", "No se puede escribir en memoria interna")
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

 */