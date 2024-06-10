package com.example.whatif_tfg

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.database.*

class BasketDraft : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var positionContainers: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_draft)

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
    }

    private fun showPlayerSelectionDialog(positionIndex: Int) {
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

    private fun getRandomPlayers(callback: (List<Pair<String, String>>) -> Unit) {
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
        loadImage(player.first, player.second, playerImage)
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

        // Obtener el identificador de la imagen del equipo desde el drawable
        val resourceId = resources.getIdentifier(lastWord, "drawable", packageName)

        if (resourceId != 0) {
            imageView.setImageResource(resourceId)
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


    private fun loadImage(playerName: String, teamName: String, imageView: ImageView) {
        val parts = playerName.split(" ")
        val first = parts[0].replace("’", "").replace(".", "")
        val last = if (parts.size > 1) parts[1].replace("’", "").replace(".", "") else ""

        val imageUrls = listOf(
            "https://www.2kratings.com/wp-content/uploads/$first-$last-2K-Rating-547x400.png",
            "https://www.2kratings.com/wp-content/uploads/$first-$last-2K-Rating-550x400.png",
            "https://www.2kratings.com/wp-content/uploads/$first-$last-2K-Rating-600x400.png"
        )

        loadImageWithFallback(imageUrls, 0, imageView)
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
    }
}
