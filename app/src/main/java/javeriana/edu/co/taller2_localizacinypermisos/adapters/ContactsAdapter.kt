package javeriana.edu.co.taller2_localizacinypermisos.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import javeriana.edu.co.taller2_localizacinypermisos.R

class ContactsAdapter(context: Context?, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags)
{

    // Conectarse con cada linea o renglon.
    override fun newView(contextNewView: Context?, cursorNewView: Cursor?, viewGroupNewView: ViewGroup?): View
    {
        return LayoutInflater.from(contextNewView).inflate(R.layout.contact_line, viewGroupNewView, false)
    }


    // Se ponen los datos en cada lista
    override fun bindView(viewBindView: View?, contextBindView: Context?, cursorBindView: Cursor?)
    {
        // El "!!" es cuando se garantiza que no va a saltar el NullSafety y se garantiza que no sera nulo.

        val contactId = viewBindView!!.findViewById<TextView>(R.id.contactIdText)
        val contactName = viewBindView!!.findViewById<TextView>(R.id.contactNameText)
        //val contactImage = viewBindView!!.findViewById<ImageView>(R.id.contactsImageView)

        val cursorId = cursorBindView!!.getInt(0)
        val cursorName = cursorBindView!!.getString(1)
        //val cursorImage = cursorBindView!!.getString(2)


        contactId.text = cursorId.toString()
        contactName.text = cursorName
        //contactImage.draw(cursorImage)
    }
}