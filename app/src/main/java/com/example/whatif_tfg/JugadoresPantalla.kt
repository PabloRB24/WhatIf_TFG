package com.example.whatif_tfg

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

class JugadoresPantalla : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var atras: ImageView
    private lateinit var mapa: LinearLayout

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var botonPelota: ImageView

    val teamCoordinates = mapOf(
        "Atlanta Hawks" to Pair(33.7573, -84.3963),
        "Boston Celtics" to Pair(42.3662, -71.0621),
        "Brooklyn Nets" to Pair(40.6827, -73.9757),
        "Charlotte Hornets" to Pair(35.2251, -80.8392),
        "Chicago Bulls" to Pair(41.8807, -87.6742),
        "Cleveland Cavaliers" to Pair(41.4965, -81.6882),
        "Dallas Mavericks" to Pair(32.7905, -96.8103),
        "Denver Nuggets" to Pair(39.7487, -105.0077),
        "Detroit Pistons" to Pair(42.3410, -83.0551),
        "Golden State Warriors" to Pair(37.7679, -122.3873),
        "Houston Rockets" to Pair(29.7508, -95.3621),
        "Indiana Pacers" to Pair(39.7640, -86.1555),
        "Los Angeles Clippers" to Pair(34.0430, -118.2673),
        "Los Angeles Lakers" to Pair(34.0430, -118.2673),
        "Memphis Grizzlies" to Pair(35.1382, -90.0506),
        "Miami Heat" to Pair(25.7814, -80.1870),
        "Milwaukee Bucks" to Pair(43.0451, -87.9172),
        "Minnesota Timberwolves" to Pair(44.9795, -93.2760),
        "New Orleans Pelicans" to Pair(29.9490, -90.0815),
        "New York Knicks" to Pair(40.7505, -73.9934),
        "Oklahoma City Thunder" to Pair(35.4634, -97.5151),
        "Orlando Magic" to Pair(28.5392, -81.3839),
        "Philadelphia 76ers" to Pair(39.9012, -75.1720),
        "Phoenix Suns" to Pair(33.4458, -112.0712),
        "Portland Trail Blazers" to Pair(45.5316, -122.6668),
        "Sacramento Kings" to Pair(38.5803, -121.4991),
        "San Antonio Spurs" to Pair(29.4270, -98.4375),
        "Toronto Raptors" to Pair(43.6435, -79.3791),
        "Utah Jazz" to Pair(40.7683, -111.9011),
        "Washington Wizards" to Pair(38.8981, -77.0209)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jugadores_pantalla)

        recyclerView = findViewById(R.id.recyclerViewJugadores)
        atras = findViewById(R.id.bntAtrasEquipos)
        mapa = findViewById(R.id.mapsClic)
        recyclerView.layoutManager = LinearLayoutManager(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        botonPelota = findViewById(R.id.imageButton)

        database = FirebaseDatabase.getInstance("https://tfggrado-de607-default-rtdb.europe-west1.firebasedatabase.app")
        rootRef = database.reference

        atras.setOnClickListener {
            val intent = Intent(this, PantallaPrincipal::class.java)
            startActivity(intent)
        }

        val equipo = intent.getStringExtra("equipo")
        if (equipo != null) {
            getPlayersFromTeam(equipo) { playerNames ->
                recyclerView.adapter = JugadoresAdapter(playerNames,equipo)
            }
        } else {
            Log.e("JugadoresPantalla", "No se pasó el nombre del equipo en el Intent")
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        botonPelota.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }



        mapa.setOnClickListener {
            if (equipo != null) {
                startMapsActivityWithTeamCoordinates(equipo)
            }
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

    fun startMapsActivityWithTeamCoordinates(teamName: String) {
        val coordinates = teamCoordinates[teamName]
        if (coordinates != null) {
            val intent = Intent(this, MapsActivity::class.java).apply {
                putExtra("latitude", coordinates.first)
                putExtra("longitude", coordinates.second)
            }
            startActivity(intent)
        } else {
            // Manejar el caso en el que el nombre del equipo no esté en el mapa
            Toast.makeText(this, "Equipo no encontrado", Toast.LENGTH_SHORT).show()
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
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-1-600x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-1-547x400.png",
                    "https://www.2kratings.com/wp-content/uploads/$firstName-$lastName-$extra-2K-Rating-1-550x400.png"
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