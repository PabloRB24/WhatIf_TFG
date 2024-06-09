package com.example.whatif_tfg

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

class TodosLosJugadores : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var atras: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var botonPelota: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todos_los_jugadores)

        recyclerView = findViewById(R.id.recyclerViewTodosLosJugadores)
        atras = findViewById(R.id.bntAtrasEquipos)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        botonPelota = findViewById(R.id.imageButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance("https://tfggrado-de607-default-rtdb.europe-west1.firebasedatabase.app")
        rootRef = database.reference


        getAllPlayers { players ->
            recyclerView.adapter = JugadoresAdapterTodos(players)
        }

        atras.setOnClickListener {
            finish() // Cierra la actividad actual y vuelve a la anterior
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        botonPelota.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.todos_equipos -> {
                val intent = Intent(this, PantallaPrincipal::class.java)
                intent.putExtra("equipo", "Todos los equipos")
                startActivity(intent)
            }
            R.id.todos_jugadores -> {
                val intent = Intent(this, TodosLosJugadores::class.java)
                intent.putExtra("equipo", "Todos los jugadores")
                startActivity(intent)
            }
            R.id.draft -> {
                val intent = Intent(this, BasketDraft::class.java)
                intent.putExtra("equipo", "Draft")
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(navView)
        return true
    }


    private fun getAllPlayers(callback: (List<Pair<String, String>>) -> Unit) {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val players = mutableListOf<Pair<String, String>>()
                for (snapshot in dataSnapshot.children) {
                    val playerName = snapshot.child("name").getValue(String::class.java)
                    val teamName = snapshot.child("team").getValue(String::class.java)
                    if (playerName != null && teamName != null) {
                        players.add(Pair(playerName, teamName))
                    }
                }
                callback(players)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error al obtener datos", databaseError.toException())
                callback(emptyList())
            }
        })
    }
}

class JugadoresAdapterTodos(private val jugadores: List<Pair<String, String>>) : RecyclerView.Adapter<JugadoresAdapterTodos.JugadorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jugador, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        val (jugador, equipo) = jugadores[position]
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

            } else if (parts.size >= 3) {
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
                intent.putExtra("jugador", jugador)
                intent.putExtra("equipo", equipo)
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
