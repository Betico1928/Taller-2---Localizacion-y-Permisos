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
        ActivityResultCallback{contactPermissionStatus ->

            Log.i("Taller 2", "Contacts Permission: $contactPermissionStatus")

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
        contactsPermission.launch(android.Manifest.permission.READ_CONTACTS)
    }
}