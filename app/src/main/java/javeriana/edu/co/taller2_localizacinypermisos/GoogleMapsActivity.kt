package javeriana.edu.co.taller2_localizacinypermisos

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityGoogleMapsBinding
import java.io.File
import java.lang.Math.toRadians
import java.util.Date
import kotlin.math.*

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SensorEventListener
{
    // Sensors
    var sensorManager : SensorManager? = null
    var sensor : Sensor? = null

    // Para escribir en la ubicacion
    data class Ubicacion(val latitud: Double, val longitud: Double, val fechaHora: String)

    // Binding
    private lateinit var bindingGoogleMaps : ActivityGoogleMapsBinding

    // mMap
    private lateinit var mMap: GoogleMap

    // Location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var lastLatitude = 0.0
    var lastLongitude  = 0.0
    private lateinit var geocoder: Geocoder

    // Permission Launcher
    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isLocationPermissionGranted = false

    companion object
    {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingGoogleMaps = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(bindingGoogleMaps.root)

        verifySensor()

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->

            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
            isLocationPermissionGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted

            Log.i("Taller 2", "Read initial permission: $isReadPermissionGranted")
            Log.i("Taller 2", "Write initial permission: $isWritePermissionGranted")
            Log.i("Taller 2", "Location initial permission: $isLocationPermissionGranted")
        }

        requestPermissions()

        Log.i("Taller 2", "Read permission: $isReadPermissionGranted")
        Log.i("Taller 2", "Write permission: $isWritePermissionGranted")
        Log.i("Taller 2", "Location permission: $isLocationPermissionGranted")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bindingGoogleMaps.buscarDireccion.setOnClickListener {
            val direccionABuscar = bindingGoogleMaps.mapText.text


            if (direccionABuscar.isNotEmpty())
            {
                try {
                    val direccionesGeocoder = Geocoder(this).getFromLocationName(direccionABuscar.toString(), 5)

                    if (direccionesGeocoder!!.size > 0)
                    {
                        val address = direccionesGeocoder[0]
                        val nombreAddress = address.getAddressLine(0)
                        val markerLatitude = address.latitude
                        val markerLongitude = address.longitude

                        val puntoAColocar = LatLng(markerLatitude, markerLongitude)
                        mMap.addMarker(MarkerOptions().position(puntoAColocar).title(direccionABuscar.toString()).snippet(nombreAddress))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntoAColocar, 14f))
                    }
                    else
                    {
                        Toast.makeText(baseContext, "Direccion no encontrada...", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: Exception)
                {
                    Toast.makeText(applicationContext, "Error: " + e.message, Toast.LENGTH_LONG).show()
                }
            }
        }


        locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult)
            {
                val location = locationResult.lastLocation
                val latLng = location?.let{
                    LatLng(location.latitude, it.longitude)
                }

                //mMap.clear()

                latLng?.let {
                    MarkerOptions().position(it).title("Mi ubicación")
                }?.let{
                    mMap.addMarker(it)
                    calculateDistance(location, lastLatitude, lastLongitude) }

                // Real time zoom
                /*
                latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                    ?.let { googleMap.moveCamera(it) }
                */
            }
        }
    }


    private fun requestPermissions()
    {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWritePermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()


        if (!isReadPermissionGranted)
        {
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!isWritePermissionGranted)
        {
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!isLocationPermissionGranted)
        {
            permissionRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }


        if (permissionRequest.isNotEmpty())
        {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap

        // Zoom
        mMap.uiSettings.isZoomGesturesEnabled = true

        // Controles de zoom
        mMap.uiSettings.isZoomControlsEnabled = true

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

        // Mostrar el puntico azul de localizacion actual
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this){ location ->
            if (location != null)
            {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                //Coloca un marcador en la ubicacion actual.
                placeMarkerOnMap(currentLatLong)

                // Ajusta la camara a la ubicacion actual
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }


            mMap.setOnMapLongClickListener {
                mMap.addMarker(MarkerOptions().position(it).title("Marcador LongClick"))
            }

            startLocationUpdates()
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


    // Pone un marcador en la ubicacion actual
    private fun placeMarkerOnMap(currentLatLong: LatLng)
    {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("Ubicacion actual")
        Log.i("Taller 2", "Marker añadido")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false


    // Para dejar de recibir actualizaciones
    private fun stopLocationUpdates()
    {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume()
    {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause()
    {
        super.onPause()
        stopLocationUpdates()
        sensorManager?.unregisterListener(this)
    }


    private fun calculateDistance(location: Location, lastLatitude: Double, lastLongitude: Double)
    {
        //Toast.makeText(baseContext, "Latitude: " + location.latitude + "Longuitude: " + location.longitude, Toast.LENGTH_SHORT).show()

        Log.i("Taller 2", "Calculate distance between 2 coordenates")

        var currentLatitude = location.latitude
        var currentLongitude = location.longitude
        val radius = 0.03 // 30 metros de distancia
        val earthRadiusKm = 6371

        val dLat = toRadians(currentLatitude - lastLatitude)
        val dLon = toRadians(currentLongitude - lastLongitude)

        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(toRadians(lastLatitude)) * cos(toRadians(currentLatitude))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))


        val distanceBetweenTwoCoordenates = earthRadiusKm * c

        if (distanceBetweenTwoCoordenates <= radius)
        {
            Log.i("Taller 2", "Distancia menor")
        }
        else
        {
            Log.i("Taller 2", "Distancia mayor")
            agregarUbicacion(currentLatitude, currentLongitude)
        }

        this@GoogleMapsActivity.lastLatitude = currentLatitude
        this@GoogleMapsActivity.lastLongitude = currentLongitude
    }

    fun agregarUbicacion(latitud: Double, longitud: Double)
    {
        // Crear objeto Gson para serializar/deserializar objetos JSON
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        // Crear archivo si no existe
        val nombreArchivo = "locations.json"
        val archivo = File(getExternalFilesDir(null), nombreArchivo)
        if (!archivo.exists())
        {
            archivo.createNewFile()
        }

        // Leer contenido del archivo y convertir a lista de Ubicacion (si el archivo no está vacío)
        val ubicacionesAnteriores = if (archivo.length() > 0)
        {
            gson.fromJson(archivo.readText(), Array<Ubicacion>::class.java).toList()
        }
        else {
            emptyList()
        }

        // Crear nueva instancia de Ubicacion y agregar a la lista anterior
        val nuevaUbicacion = Ubicacion(latitud, longitud, Date(System.currentTimeMillis()).toString())
        val ubicacionesNuevas = ubicacionesAnteriores + nuevaUbicacion

        // Serializar lista de ubicaciones y escribir en el archivo
        val json = gson.toJson(ubicacionesNuevas)
        archivo.writeText(json)
        Log.i("Taller 2", "Escrito en archivo JSON")
    }

    private fun verifySensor()
    {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        Log.i("Taller 2", sensor.toString())
        Log.i("Taller 2", sensorManager.toString())
    }

    override fun onSensorChanged(event: SensorEvent?)
    {
        var googleMap = mMap

        if (event != null)
        {
            if (event.values[0] < 10000)
            {
                try
                {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))

                    if (!success)
                    {
                        Log.e("Taller 2", "Style parsing failed.")
                    }
                }
                catch (e: Resources.NotFoundException)
                {
                    Log.e("Taller 2", "Can't find style. Error: ", e)
                }
            }
            else
            {
                try
                {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_retro))

                    if (!success)
                    {
                        Log.e("Taller 2", "Style parsing failed.")
                    }
                }
                catch (e: Resources.NotFoundException)
                {
                    Log.e("Taller 2", "Can't find style. Error: ", e)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
    {
        return
    }
}