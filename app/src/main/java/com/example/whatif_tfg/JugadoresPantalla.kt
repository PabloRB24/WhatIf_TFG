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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
                recyclerView.adapter = JugadoresAdapter(playerNames)
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



class JugadoresAdapter(private val jugadores: List<String>) : RecyclerView.Adapter<JugadoresAdapter.JugadorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jugador, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        val jugador = jugadores[position]
        holder.bind(jugador)
    }

    override fun getItemCount(): Int = jugadores.size

    class JugadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerName: TextView = itemView.findViewById(R.id.player_name)
        private val playerImage: ImageView = itemView.findViewById(R.id.player_image)
        private val placeholderResId = R.drawable.ic_player_placeholder

        fun bind(jugador: String) {
            playerName.text = jugador

            val parts = jugador.split(" ")
            if (parts.size == 2) {
                val firstName = parts[0]
                val lastName = parts[1]
                val imageUrl1 = "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-547x400.png"
                val imageUrl2 = "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-2K-Rating-550x400.png"

                loadImageWithFallback(imageUrl1, imageUrl2)
            } else if (parts.size >= 3){
                val firstName = parts[0]
                val lastName = parts[1]
                val extra = parts[2]
                val imageUrl1 = "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-547x400.png"
                val imageUrl2 = "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-550x400.png"

                loadImageWithFallback(imageUrl1, imageUrl2)
            }
        }

        private fun loadImageWithFallback(primaryUrl: String, fallbackUrl: String) {
            val requestOptions = RequestOptions()
                .placeholder(placeholderResId)
                .error(placeholderResId)

            Glide.with(itemView.context)
                .load(primaryUrl)
                .apply(requestOptions)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        playerImage.setImageDrawable(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        // Si la primera URL falla, intentar la segunda
                        Glide.with(itemView.context)
                            .load(fallbackUrl)
                            .apply(requestOptions)
                            .into(playerImage)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Método requerido, pero no necesitamos hacer nada aquí
                    }
                })
        }
    }
}