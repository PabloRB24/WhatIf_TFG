package com.example.whatif_tfg

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.database.*

class JugadoresPantalla : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var atras: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jugadores_pantalla)

        recyclerView = findViewById(R.id.recyclerViewJugadores)
        atras = findViewById(R.id.bntAtrasEquipos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance("https://tfggrado-de607-default-rtdb.europe-west1.firebasedatabase.app")
        rootRef = database.reference

        atras.setOnClickListener {
            val intent = Intent(this, PantallaPrincipal::class.java)
            startActivity(intent)
        }

        val teamName = intent.getStringExtra("equipo")
        if (teamName != null) {
            getPlayersFromTeam(teamName) { playerNames ->
                recyclerView.adapter = JugadoresAdapter(playerNames,teamName)
            }
        } else {
            Log.e("JugadoresPantalla", "No se pasó el nombre del equipo en el Intent")
        }
    }

    private fun getPlayersFromTeam(teamName: String, callback: (List<String>) -> Unit) {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val playerNames = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val team = snapshot.child("team").getValue(String::class.java)
                    if (team == teamName) {
                        val playerName = snapshot.child("name").getValue(String::class.java)
                        playerName?.let { playerNames.add(it) }
                    }
                }
                callback(playerNames)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error al obtener datos", databaseError.toException())
                callback(emptyList())
            }
        })
    }
}



class JugadoresAdapter(private val jugadores: List<String>, private val equipo: String) : RecyclerView.Adapter<JugadoresAdapter.JugadorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jugador, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        val jugador = jugadores[position]
        holder.bind(jugador, equipo)
    }

    override fun getItemCount(): Int = jugadores.size

    class JugadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerName: TextView = itemView.findViewById(R.id.player_name)
        private val playerImage: ImageView = itemView.findViewById(R.id.player_image)
        private val placeholderResId = R.drawable.ic_player_placeholder
        private val playerContainer: LinearLayout = itemView.findViewById(R.id.player_container)

        fun bind(jugador: String, equipo: String) {
            playerName.text = jugador
            val parts = jugador.split(" ")
            if (parts.size == 2) {
                var firstName = parts[0]
                var lastName = parts[1]
                if (firstName.contains("’")) {
                    firstName = firstName.replace("’", "")
                }
                if (lastName.contains("’")) {
                    lastName = lastName.replace("’", "")
                }
                if (firstName.contains(".")) {
                    firstName = firstName.replace(".", "")
                }
                if (lastName.contains(".")) {
                    lastName = lastName.replace(".", "")
                }

                val imageUrls = listOf(
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-547x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-550x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-600x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-1-600x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-1-547x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-1-550x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-III-2K-Rating-547x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-II-2K-Rating-547x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-III-2K-Rating-550x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-II-2K-Rating-550x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-III-2K-Rating-600x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-II-2K-Rating-600x400.png"
                )

                loadImageWithFallback(imageUrls)

            } else if (parts.size >= 3){
                var firstName = parts[0]
                var lastName = parts[1]
                var extra = parts[2]
                if (firstName.contains("’")) {
                    firstName = firstName.replace("’", "")
                }
                if (lastName.contains("’")) {
                    lastName = lastName.replace("’", "")
                }
                if (firstName.contains(".")) {
                    firstName = firstName.replace(".", "")
                }
                if (lastName.contains(".")) {
                    lastName = lastName.replace(".", "")
                }
                if (extra.contains(".")) {
                    extra = extra.replace(".", "")
                }

                val imageUrls = listOf(
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-547x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-550x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-600x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-1-600x400.png"
                )

                loadImageWithFallback(imageUrls)
            } else {
                playerImage.setImageResource(placeholderResId)
            }

            // Aplicar el fondo correspondiente al equipo
            val equipoParts = equipo.split(" ")
            val equipoNombre = equipoParts.last().lowercase()
            val backgroundResId = itemView.context.resources.getIdentifier("fade_$equipoNombre", "drawable", itemView.context.packageName)

            if (backgroundResId != 0) {
                playerContainer.background = ContextCompat.getDrawable(itemView.context, backgroundResId)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, Jugador::class.java)
                intent.putExtra("equipo", equipo)
                intent.putExtra("jugador", jugador)
                itemView.context.startActivity(intent)
            }
        }

        private fun loadImageWithFallback(urls: List<String>, index: Int = 0) {
            if (index >= urls.size) {
                // Si todas las URLs fallan, establecer la imagen de error
                playerImage.setImageResource(placeholderResId)
                return
            }

            val requestOptions = RequestOptions()
                .placeholder(placeholderResId)
                .error(placeholderResId)

            Glide.with(itemView.context)
                .load(urls[index])
                .apply(requestOptions)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        playerImage.setImageDrawable(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        // Si la carga falla, intentar con la siguiente URL
                        loadImageWithFallback(urls, index + 1)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Método requerido, pero no necesitamos hacer nada aquí
                    }
                })
        }
    }
}