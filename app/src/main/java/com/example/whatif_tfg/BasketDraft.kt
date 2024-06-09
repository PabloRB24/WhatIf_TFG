package com.example.whatif_tfg

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import kotlin.random.Random

class BasketDraft : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var positionImages: List<ImageView>
    private lateinit var database: FirebaseDatabase
    private lateinit var rootRef: DatabaseReference
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var botonPelota: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_draft)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        botonPelota = findViewById(R.id.imageButton)

        positionImages = listOf(
            findViewById(R.id.position1_image),
            findViewById(R.id.position2_image),
            findViewById(R.id.position3_image),
            findViewById(R.id.position4_image),
            findViewById(R.id.position5_image)
        )

        database = FirebaseDatabase.getInstance("https://tfggrado-de607-default-rtdb.europe-west1.firebasedatabase.app")
        rootRef = database.reference

        positionImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
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


    private fun showPlayerSelectionDialog(positionIndex: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.ativity_pop_up_elegir_jugador, null)

        val player1Image = dialogView.findViewById<ImageView>(R.id.player1_image)
        val player1Name = dialogView.findViewById<TextView>(R.id.player1_name)
        val player2Image = dialogView.findViewById<ImageView>(R.id.player2_image)
        val player2Name = dialogView.findViewById<TextView>(R.id.player2_name)
        val player3Image = dialogView.findViewById<ImageView>(R.id.player3_image)
        val player3Name = dialogView.findViewById<TextView>(R.id.player3_name)

        getRandomPlayers { players ->
            player1Name.text = players[0].first
            loadImage(players[0].first, players[0].second, player1Image)

            player2Name.text = players[1].first
            loadImage(players[1].first, players[1].second, player2Image)

            player3Name.text = players[2].first
            loadImage(players[2].first, players[2].second, player3Image)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            player1Image.setOnClickListener {
                updateSelectedPlayer(positionIndex, players[0])
                dialog.dismiss()
            }
            player2Image.setOnClickListener {
                updateSelectedPlayer(positionIndex, players[1])
                dialog.dismiss()
            }
            player3Image.setOnClickListener {
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

    private fun loadImage(firstName: String, lastName: String, imageView: ImageView) {
        val parts = firstName.split(" ")
        val first = parts[0].replace("’", "").replace(".", "")
        val last = parts[1].replace("’", "").replace(".", "")

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
        val selectedImageView = positionImages[positionIndex]
        loadImage(player.first, player.second, selectedImageView)
        val playerNameTextView = selectedImageView.rootView.findViewById<TextView>(selectedImageView.id + 1)
        playerNameTextView.text = player.first
    }
}
