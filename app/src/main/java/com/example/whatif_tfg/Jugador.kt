package com.example.whatif_tfg

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

class Jugador : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var playerImage: ImageView
    private lateinit var nombre: TextView
    private lateinit var rating: TextView
    private lateinit var statsGrid: GridLayout
    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var bntAtrasJugadores: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var botonPelota: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jugador)

        playerImage = findViewById(R.id.player_image)
        nombre = findViewById(R.id.nombre)
        rating = findViewById(R.id.rating)
        statsGrid = findViewById(R.id.stats_grid)
        bntAtrasJugadores = findViewById(R.id.bntAtrasJugadores)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        botonPelota = findViewById(R.id.imageButton)

        database = FirebaseDatabase.getInstance("https://tfggrado-de607-default-rtdb.europe-west1.firebasedatabase.app")
        rootRef = database.reference


        val jugador = intent.getStringExtra("jugador")
        val equipo = intent.getStringExtra("equipo")


        if (jugador != null && equipo != null) {
            loadPlayerDetails(jugador)
        }

        bntAtrasJugadores.setOnClickListener {
            finish()
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


    private fun loadPlayerDetails(jugador: String) {
        rootRef.orderByChild("name").equalTo(jugador).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val playerName = snapshot.child("name").getValue(String::class.java)
                    val speed = snapshot.child("speed").getValue(Int::class.java)
                    val ballHandling = snapshot.child("ballHandle").getValue(Int::class.java)
                    val passAccuracy = snapshot.child("passAccuracy").getValue(Int::class.java)
                    val passIQ = snapshot.child("passIQ").getValue(Int::class.java)
                    val passPerception = snapshot.child("passPerception").getValue(Int::class.java)
                    val passVision = snapshot.child("passVision").getValue(Int::class.java)
                    val freeThrow = snapshot.child("freeThrow").getValue(Int::class.java)
                    val layup = snapshot.child("layup").getValue(Int::class.java)
                    val midRangeShot = snapshot.child("midRangeShot").getValue(Int::class.java)
                    val closeShot = snapshot.child("closeShot").getValue(Int::class.java)
                    val shotIQ = snapshot.child("shotIQ").getValue(Int::class.java)
                    val threePointShot = snapshot.child("threePointShot").getValue(Int::class.java)
                    val defensiveConsistency = snapshot.child("defensiveConsistency").getValue(Int::class.java)
                    val strength = snapshot.child("strength").getValue(Int::class.java)
                    val stamina = snapshot.child("stamina").getValue(Int::class.java)
                    val ratingStat = snapshot.child("overallAttribute").getValue(Int::class.java)

                    val passing = (passAccuracy!! + passIQ!! + passPerception!! + passVision!!) / 4
                    val shooting = (freeThrow!! + layup!! + midRangeShot!! + closeShot!! + shotIQ!! + threePointShot!!) / 6
                    val physical = (strength!! + stamina!!)/2

                    statsGrid.findViewById<TextView>(R.id.estadistica1).text = "SPD $speed"
                    statsGrid.findViewById<TextView>(R.id.estadistica2).text = "BLH $ballHandling"
                    statsGrid.findViewById<TextView>(R.id.estadistica3).text = "PAS $passing"
                    statsGrid.findViewById<TextView>(R.id.estadistica4).text = "DEF $defensiveConsistency"
                    statsGrid.findViewById<TextView>(R.id.estadistica5).text = "SHO $shooting"
                    statsGrid.findViewById<TextView>(R.id.estadistica6).text = "PHY $physical"

                    rating.text = ratingStat.toString()
                    nombre.text = playerName


                    val equipo = intent.getStringExtra("equipo") ?: ""
                    val equipoParts = equipo.split(" ")
                    val lastWord = equipoParts.last().lowercase()

                    val teamImageResource = if (lastWord == "76ers") {
                        R.drawable.foto_76ers
                    } else {
                        resources.getIdentifier(lastWord, "drawable", packageName)
                    }

                    if (teamImageResource != 0) {
                        findViewById<ImageView>(R.id.team_image).setImageResource(teamImageResource)
                    } else {
                        findViewById<ImageView>(R.id.team_image).setImageResource(R.drawable.ic_player_placeholder)
                    }



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
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun loadImageWithFallback(urls: List<String>, index: Int = 0) {
        if (index >= urls.size) {
            playerImage.setImageResource(R.drawable.ic_player_placeholder)
            return
        }

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)

        Glide.with(this)
            .load(urls[index])
            .apply(requestOptions)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    playerImage.setImageDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    loadImageWithFallback(urls, index + 1)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}
