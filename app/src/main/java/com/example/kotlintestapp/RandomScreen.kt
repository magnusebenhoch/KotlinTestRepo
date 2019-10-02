package com.example.kotlintestapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager




class RandomScreen : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private var dogList: MutableList<Bitmap> = mutableListOf()
    private lateinit var dogAdapter: RecyclerAdapter

    @SuppressLint("CLickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_screen)
        button = findViewById(R.id.button)
        recyclerView = findViewById(R.id.recyclerView)

        dogAdapter = RecyclerAdapter(dogList)
        recyclerView.adapter = dogAdapter

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        button.setOnClickListener{
            for (i in 0..10) {
                createDog()
            }
            Observable.fromIterable(0..10)
        }
    }

    fun createDog() {
        getDogURL()
            .flatMap ({
            dogURL -> getDogImage(dogURL.message!!)
        }, { dogURL, dogBitmap ->
            dogList.add(dogBitmap)
            Log.i("Log", dogList.toString())
            dogAdapter.notifyItemInserted(dogList.size-1)
        })
            .subscribe()
    }


    fun getDogURL() : Observable<DogURL> {
        return Observable.create { subscriber ->
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://dog.ceo")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(DogAPI::class.java)
            val call = service.getData()

            call.enqueue(object : Callback<DogURL> {
                override fun onFailure(call: Call<DogURL>, t: Throwable) {
                    Log.i(t.toString(), "API Error")
                    subscriber.onError(t)
                }

                override fun onResponse(call: Call<DogURL>, response: Response<DogURL>) {
                    Log.i("Log", response.code().toString())
                    if (response.code() == 200) {
                        if (response.body() != null){
                            subscriber.onNext(response.body()!!)
                            subscriber.onComplete()
                        }
                    }
                    else {
                        subscriber.onError(error("code 200 response == null"))
                    }
                }
            })
        }
    }

    fun getDogImage(url: String) : Observable<Bitmap> {
        Log.i("test", "Second request")
        return Observable.create() {
            subscriber ->
            Log.i("Log", "before second request $url")
            Picasso.get().load(url).into(object : Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Log.i("Log", "getDogImage failed")
                    subscriber.onError(e!!.fillInStackTrace())
                }
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    Log.i("Log", "getDogImage complete")
                    subscriber.onNext(bitmap!!)
                }
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            })
        }
    }
}

class DogURL {
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: String? = null
}

interface DogAPI{
  @GET("api/breeds/image/random")
  fun getData() : Call<DogURL>
}

class RecyclerAdapter(private val dogList: MutableList <Bitmap>) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerAdapter.MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dog_cell, parent, false) as LinearLayout
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(linearLayout)
    }
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.layout.findViewById<ImageView>(R.id.imageView).setImageBitmap(dogList[position])
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dogList.size
}
