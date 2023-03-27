package javeriana.edu.co.taller2_localizacinypermisos

import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity()
{
    private lateinit var bindingContacts : ActivityContactsBinding

    // Solicitud de permiso
    private var contactsPermission = registerForActivityResult(

        // Request permission
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback{ contactPermissionStatus ->

            Log.i("Taller 2", "Contacts Permission: $contactPermissionStatus")

            initializeElements(contactPermissionStatus)
        }
    )


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingContacts = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(bindingContacts.root)

        getContactsPermission()
    }

    private fun getContactsPermission()
    {
        // Si el permiso NO ha sido aceptado:
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS))
            {
                // Mostrar el porque se necesita el permiso.
                Toast.makeText(this, "The app needs the contacts in order to show them", Toast.LENGTH_LONG).show()
            }
            // Perdir el permiso (variable de registerForActivityResult)
            contactsPermission.launch(android.Manifest.permission.READ_CONTACTS)
        }

        // Si el permiso ya ha sido aceptado.
        else
        {
            initializeElements(true)
        }

    }



    private fun initializeElements(contactPermissionStatus: Boolean)
    {
        if (contactPermissionStatus)
        {
            bindingContacts.statusContacts.text = "Permission Granted"
            bindingContacts.statusContacts.setTextColor(Color.GREEN)
        }
        else
        {
            bindingContacts.statusContacts.text = "Permission Denied"
            bindingContacts.statusContacts.setTextColor(Color.RED)
        }
    }
}