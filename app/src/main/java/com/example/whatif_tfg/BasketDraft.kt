package com.example.whatif_tfg

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

class BasketDraft : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var positionContainers: List<View>

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var botonPelota: ImageView

    // Mapa para mantener un registro de los jugadores asignados
    private val assignedPlayers = mutableMapOf<Int, PlayerAssignment>()

    // Referencias a los nuevos elementos
    private lateinit var teamAvgText: TextView
    private lateinit var teamProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_draft)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        botonPelota = findViewById(R.id.imageButton)

        teamAvgText = findViewById(R.id.team_avg_text)
        teamProgressBar = findViewById(R.id.team_progress_bar)

        positionContainers = listOf(
            findViewById(R.id.position1_container),
            findViewById(R.id.position2_container),
            findViewById(R.id.position3_container),
            findViewById(R.id.position4_container),
            findViewById(R.id.position5_container)
        )

        database = FirebaseDatabase.getInstance("https://tfggrado-de607-default-rtdb.europe-west1.firebasedatabase.app")
        rootRef = database.reference

        positionContainers.forEachIndexed { index, container ->
            container.setOnClickListener {
                showPlayerSelectionDialog(index)
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        botonPelota.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        updateTeamStats()
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
                val intent = Intent(this, TodosLosJugadores::class.java)
                intent.putExtra("equipo", "Draft")
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(navView)
        return true
    }
    private fun showPlayerSelectionDialog(positionIndex: Int) {
        if (assignedPlayers.containsKey(positionIndex)) {
            // Si ya hay un jugador asignado a esta posición, redirige a la actividad del jugador
            val player = assignedPlayers[positionIndex]
            val intent = Intent(this, Jugador::class.java)
            intent.putExtra("jugador", player?.playerName)
            intent.putExtra("equipo", player?.teamName)
            startActivity(intent)
        } else {
            // Mostrar el diálogo de selección de jugador
            val dialogView = LayoutInflater.from(this).inflate(R.layout.ativity_pop_up_elegir_jugador, null)

            val player1Container = dialogView.findViewById<View>(R.id.player1_container)
            val player2Container = dialogView.findViewById<View>(R.id.player2_container)
            val player3Container = dialogView.findViewById<View>(R.id.player3_container)

            getRandomPlayers { players ->
                setPlayerDetails(player1Container, players[0])
                setPlayerDetails(player2Container, players[1])
                setPlayerDetails(player3Container, players[2])

                val dialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create()

                // Aplicar la animación al contenedor del diálogo
                dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

                player1Container.setOnClickListener {
                    updateSelectedPlayer(positionIndex, players[0])
                    dialog.dismiss()
                }
                player2Container.setOnClickListener {
                    updateSelectedPlayer(positionIndex, players[1])
                    dialog.dismiss()
                }
                player3Container.setOnClickListener {
                    updateSelectedPlayer(positionIndex, players[2])
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    private fun getRandomPlayers(callback: (List<Pair<String, String>>) -> Unit) {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val players = mutableListOf<Pair<String, String>>()
                val assignedPlayerNames = assignedPlayers.values.map { it.playerName }
                for (snapshot in dataSnapshot.children) {
                    val playerName = snapshot.child("name").getValue(String::class.java)
                    val teamName = snapshot.child("team").getValue(String::class.java)
                    if (playerName != null && teamName != null && !assignedPlayerNames.contains(playerName)) {
                        players.add(Pair(playerName, teamName))
                    }
                }
                players.shuffle()
                callback(players.take(3))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error
            }
        })
    }

    private fun setPlayerDetails(container: View, player: Pair<String, String>) {
        val playerImage = container.findViewById<ImageView>(R.id.player_image)
        val teamImage = container.findViewById<ImageView>(R.id.team_image) // Añadido
        val playerName = container.findViewById<TextView>(R.id.nombre)
        val rating = container.findViewById<TextView>(R.id.rating)
        val statsGrid = container.findViewById<GridLayout>(R.id.stats_grid)

        playerName.text = player.first
        loadPlayerStats(player.first) { stats, team ->
            statsGrid.findViewById<TextView>(R.id.estadistica1).text = "SPD ${stats.speed}"
            statsGrid.findViewById<TextView>(R.id.estadistica2).text = "BLH ${stats.ballHandling}"
            statsGrid.findViewById<TextView>(R.id.estadistica3).text = "PAS ${stats.passing}"
            statsGrid.findViewById<TextView>(R.id.estadistica4).text = "DEF ${stats.defense}"
            statsGrid.findViewById<TextView>(R.id.estadistica5).text = "SHO ${stats.shooting}"
            statsGrid.findViewById<TextView>(R.id.estadistica6).text = "PHY ${stats.physical}"
            rating.text = stats.rating.toString()
            loadTeamImage(team, teamImage) // Cargar la imagen del equipo
        }
        loadImage(player.first, playerImage)
    }

    private fun loadPlayerStats(playerName: String, callback: (PlayerStats, String) -> Unit) {
        rootRef.orderByChild("name").equalTo(playerName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val speed = snapshot.child("speed").getValue(Int::class.java) ?: 0
                    val ballHandling = snapshot.child("ballHandle").getValue(Int::class.java) ?: 0
                    val passAccuracy = snapshot.child("passAccuracy").getValue(Int::class.java) ?: 0
                    val passIQ = snapshot.child("passIQ").getValue(Int::class.java) ?: 0
                    val passPerception = snapshot.child("passPerception").getValue(Int::class.java) ?: 0
                    val passVision = snapshot.child("passVision").getValue(Int::class.java) ?: 0
                    val freeThrow = snapshot.child("freeThrow").getValue(Int::class.java) ?: 0
                    val layup = snapshot.child("layup").getValue(Int::class.java) ?: 0
                    val midRangeShot = snapshot.child("midRangeShot").getValue(Int::class.java) ?: 0
                    val closeShot = snapshot.child("closeShot").getValue(Int::class.java) ?: 0
                    val shotIQ = snapshot.child("shotIQ").getValue(Int::class.java) ?: 0
                    val threePointShot = snapshot.child("threePointShot").getValue(Int::class.java) ?: 0
                    val defensiveConsistency = snapshot.child("defensiveConsistency").getValue(Int::class.java) ?: 0
                    val strength = snapshot.child("strength").getValue(Int::class.java) ?: 0
                    val stamina = snapshot.child("stamina").getValue(Int::class.java) ?: 0
                    val rating = snapshot.child("overallAttribute").getValue(Int::class.java) ?: 0
                    val team = snapshot.child("team").getValue(String::class.java) ?: ""

                    val passing = (passAccuracy + passIQ + passPerception + passVision) / 4
                    val shooting = (freeThrow + layup + midRangeShot + closeShot + shotIQ + threePointShot) / 6
                    val physical = (strength + stamina) / 2

                    val stats = PlayerStats(speed, ballHandling, passing, defensiveConsistency, shooting, physical, rating)
                    callback(stats, team)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadTeamImage(equipo: String, imageView: ImageView) {
        val equipoParts = equipo.split(" ")
        val lastWord = equipoParts.last().lowercase()

        val teamImageResource = if (lastWord == "76ers") {
            R.drawable.foto_76ers
        } else {
            resources.getIdentifier(lastWord, "drawable", packageName)
        }


        if (teamImageResource != 0) {
            imageView.setImageResource(teamImageResource)
        } else {
            imageView.setImageResource(R.drawable.ic_player_placeholder)
        }
    }

    private data class PlayerStats(
        val speed: Int,
        val ballHandling: Int,
        val passing: Int,
        val defense: Int,
        val shooting: Int,
        val physical: Int,
        val rating: Int
    )

    private data class PlayerAssignment(
        val playerName: String,
        val teamName: String,
        val rating: Int
    )

    private fun loadImage(playerName: String, imageView: ImageView) {
        val parts = playerName.split(" ")

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

            loadImageWithFallback(imageUrls, 0, imageView)

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

            loadImageWithFallback(imageUrls, 0, imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_player_placeholder)
        }

    }

    private fun loadImageWithFallback(urls: List<String>, index: Int, imageView: ImageView) {
        if (index >= urls.size) {
            imageView.setImageResource(R.drawable.ic_player_placeholder)
            return
        }

        Glide.with(this)
            .load(urls[index])
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    imageView.setImageDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    loadImageWithFallback(urls, index + 1, imageView)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun updateSelectedPlayer(positionIndex: Int, player: Pair<String, String>) {
        val selectedContainer = positionContainers[positionIndex]
        setPlayerDetails(selectedContainer, player)
        loadPlayerStats(player.first) { stats, team ->
            assignedPlayers[positionIndex] = PlayerAssignment(player.first, team, stats.rating)
            updateTeamStats()
        }
    }

    private fun calculateQuimica(): Int {
        val teamCount = assignedPlayers.values.groupingBy { it.teamName }.eachCount().values.maxOrNull() ?: 0
        return teamCount * 25
    }

    private fun updateTeamStats() {
        val totalRatings = assignedPlayers.values.sumOf { it.rating }
        val avgRating = if (assignedPlayers.isNotEmpty()) totalRatings / assignedPlayers.size else 0

        teamAvgText.text = "Team Average: $avgRating"

        val quimica = calculateQuimica()
        teamProgressBar.progress = quimica
        val quimicaTextView = findViewById<TextView>(R.id.quimica)
        quimicaTextView.text = "Química: $quimica"

    }
}
