package javeriana.edu.co.taller2_localizacinypermisos

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity()
{
    private lateinit var bindingContacts : ActivityContactsBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingContacts = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(bindingContacts.root)

        initializeElements()
    }

    private fun initializeElements()
    {
        bindingContacts.statusContacts.text = "NO STATUS"
        bindingContacts.statusContacts.setTextColor(Color.YELLOW)
    }
}