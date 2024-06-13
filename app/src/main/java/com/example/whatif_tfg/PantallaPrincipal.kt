package com.example.whatif_tfg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class PantallaPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var carta_76ers: CardView
    private lateinit var carta_bucks: CardView
    private lateinit var carta_bulls: CardView
    private lateinit var carta_cavaliers: CardView
    private lateinit var carta_celtics: CardView
    private lateinit var carta_clippers: CardView
    private lateinit var carta_grizzlies: CardView
    private lateinit var carta_hawks: CardView
    private lateinit var carta_heat: CardView
    private lateinit var carta_hornets: CardView
    private lateinit var carta_jazz: CardView
    private lateinit var carta_kings: CardView
    private lateinit var carta_knicks: CardView
    private lateinit var carta_lakers: CardView
    private lateinit var carta_magic: CardView
    private lateinit var carta_mavericks: CardView
    private lateinit var carta_nets: CardView
    private lateinit var carta_nuggets: CardView
    private lateinit var carta_pacers: CardView
    private lateinit var carta_pelicans: CardView
    private lateinit var carta_pistons: CardView
    private lateinit var carta_raptors: CardView
    private lateinit var carta_rockets: CardView
    private lateinit var carta_spurs: CardView
    private lateinit var carta_suns: CardView
    private lateinit var carta_thunder: CardView
    private lateinit var carta_timberwolves: CardView
    private lateinit var carta_trail_blazers: CardView
    private lateinit var carta_warriors: CardView
    private lateinit var carta_wizards: CardView


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var botonPelota: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        botonPelota = findViewById(R.id.imageButton)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        botonPelota.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        carta_76ers = findViewById(R.id.carta_76ers)
        carta_bucks = findViewById(R.id.carta_bucks)
        carta_bulls = findViewById(R.id.carta_bulls)
        carta_cavaliers = findViewById(R.id.carta_cavaliers)
        carta_celtics = findViewById(R.id.carta_celtics)
        carta_clippers = findViewById(R.id.carta_clippers)
        carta_grizzlies = findViewById(R.id.carta_grizzlies)
        carta_hawks = findViewById(R.id.carta_hawks)
        carta_heat = findViewById(R.id.carta_heat)
        carta_hornets = findViewById(R.id.carta_hornets)
        carta_jazz = findViewById(R.id.carta_jazz)
        carta_kings = findViewById(R.id.carta_kings)
        carta_knicks = findViewById(R.id.carta_knicks)
        carta_lakers = findViewById(R.id.carta_lakers)
        carta_magic = findViewById(R.id.carta_magic)
        carta_mavericks = findViewById(R.id.carta_mavericks)
        carta_nets = findViewById(R.id.carta_nets)
        carta_nuggets = findViewById(R.id.carta_nuggets)
        carta_pacers = findViewById(R.id.carta_pacers)
        carta_pelicans = findViewById(R.id.carta_pelicans)
        carta_pistons = findViewById(R.id.carta_pistons)
        carta_raptors = findViewById(R.id.carta_raptors)
        carta_rockets = findViewById(R.id.carta_rockets)
        carta_spurs = findViewById(R.id.carta_spurs)
        carta_suns = findViewById(R.id.carta_suns)
        carta_thunder = findViewById(R.id.carta_thunder)
        carta_timberwolves = findViewById(R.id.carta_timberwolves)
        carta_trail_blazers = findViewById(R.id.carta_trail_blazers)
        carta_warriors = findViewById(R.id.carta_warriors)
        carta_wizards = findViewById(R.id.carta_wizards)


        carta_76ers.setOnClickListener { navigateToJugadoresPantalla("Philadelphia 76ers") }
        carta_bucks.setOnClickListener { navigateToJugadoresPantalla("Milwaukee Bucks") }
        carta_bulls.setOnClickListener { navigateToJugadoresPantalla("Chicago Bulls") }
        carta_cavaliers.setOnClickListener { navigateToJugadoresPantalla("Cleveland Cavaliers") }
        carta_celtics.setOnClickListener { navigateToJugadoresPantalla("Boston Celtics") }
        carta_clippers.setOnClickListener { navigateToJugadoresPantalla("Los Angeles Clippers") }
        carta_grizzlies.setOnClickListener { navigateToJugadoresPantalla("Memphis Grizzlies") }
        carta_hawks.setOnClickListener { navigateToJugadoresPantalla("Atlanta Hawks") }
        carta_heat.setOnClickListener { navigateToJugadoresPantalla("Miami Heat") }
        carta_hornets.setOnClickListener { navigateToJugadoresPantalla("Charlotte Hornets") }
        carta_jazz.setOnClickListener { navigateToJugadoresPantalla("Utah Jazz") }
        carta_kings.setOnClickListener { navigateToJugadoresPantalla("Sacramento Kings") }
        carta_knicks.setOnClickListener { navigateToJugadoresPantalla("New York Knicks") }
        carta_lakers.setOnClickListener { navigateToJugadoresPantalla("Los Angeles Lakers") }
        carta_magic.setOnClickListener { navigateToJugadoresPantalla("Orlando Magic") }
        carta_mavericks.setOnClickListener { navigateToJugadoresPantalla("Dallas Mavericks") }
        carta_nets.setOnClickListener { navigateToJugadoresPantalla("Brooklyn Nets") }
        carta_nuggets.setOnClickListener { navigateToJugadoresPantalla("Denver Nuggets") }
        carta_pacers.setOnClickListener { navigateToJugadoresPantalla("Indiana Pacers") }
        carta_pelicans.setOnClickListener { navigateToJugadoresPantalla("New Orleans Pelicans") }
        carta_pistons.setOnClickListener { navigateToJugadoresPantalla("Detroit Pistons") }
        carta_raptors.setOnClickListener { navigateToJugadoresPantalla("Toronto Raptors") }
        carta_rockets.setOnClickListener { navigateToJugadoresPantalla("Houston Rockets") }
        carta_spurs.setOnClickListener { navigateToJugadoresPantalla("San Antonio Spurs") }
        carta_suns.setOnClickListener { navigateToJugadoresPantalla("Phoenix Suns") }
        carta_thunder.setOnClickListener { navigateToJugadoresPantalla("Oklahoma City Thunder") }
        carta_timberwolves.setOnClickListener { navigateToJugadoresPantalla("Minnesota Timberwolves") }
        carta_trail_blazers.setOnClickListener { navigateToJugadoresPantalla("Portland Trail Blazers") }
        carta_warriors.setOnClickListener { navigateToJugadoresPantalla("Golden State Warriors") }
        carta_wizards.setOnClickListener { navigateToJugadoresPantalla("Washington Wizards") }


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



    private fun navigateToJugadoresPantalla(teamName: String) {
        val equipo = Intent(this, JugadoresPantalla::class.java)
        equipo.putExtra("equipo", teamName)
        startActivity(equipo)
    }
}
