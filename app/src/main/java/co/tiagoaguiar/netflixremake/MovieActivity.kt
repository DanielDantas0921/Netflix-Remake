package co.tiagoaguiar.netflixremake

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.model.MovieDetail
import co.tiagoaguiar.netflixremake.util.DownloadImageTask
import co.tiagoaguiar.netflixremake.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvCast: TextView
    private lateinit var rv: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var progress: ProgressBar
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

         tvTitle= findViewById(R.id.movie_tv_title)
         tvDesc = findViewById(R.id.movie_tv_desc)
         tvCast = findViewById(R.id.movie_tv_cast)
         rv = findViewById(R.id.movie_rv_similar)
        progress = findViewById(R.id.movie_progress)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não foi encontrado")
        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=82a3faf0-f31b-462d-9e92-a3591c0346d2"

        MovieTask(this,).execute(url)





//        adicionar filmes falsos
//        for(z in 0 until 10){
//            val id = z + 1
//            val coverUrl = "https://cdn.tiagoaguiar.co/images/img/AAAAAdGB4BQI-v-nabbMcT85DHpwWWlYuFyEU4vRrH-duWCtsQlWAtFKtM27TkhD9BHePeZQe2W3BwOCFlOTv0JTSYPJ2F6SZ-QWGA.jpg"
//            movies.add(Movie(id, coverUrl))
//        }


        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this,3)
        rv.adapter = adapter


        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null






    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onResult(movieDetail: MovieDetail) {
        progress.visibility = View.GONE
        Log.i("teste", movieDetail.toString())
        tvTitle.text = movieDetail.movie.title
        tvDesc.text = movieDetail.movie.desc
        tvCast.text = getString(R.string.cast, movieDetail.movie.cast)
        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()
        DownloadImageTask(object: DownloadImageTask.Callback{
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity,R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources,bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable,movieCover)
                val coverImg: ImageView = findViewById(R.id.movie_img)
                coverImg.setImageDrawable(layerDrawable)
            }

        }).execute(movieDetail.movie.coverUrl)


    }

    override fun onFailure(message: String) {
        progress.visibility = View.GONE

        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()

        }

        return super.onOptionsItemSelected(item)
    }


}