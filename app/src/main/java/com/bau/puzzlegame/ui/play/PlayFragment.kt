package com.bau.puzzlegame.ui.play

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bau.puzzlegame.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.squareup.picasso.Picasso


class PlayFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var playBtn: Button

    lateinit var imageBanner: ImageView
    lateinit var root: View


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_play, container, false)
        mAuth = FirebaseAuth.getInstance()

        initFragment()
        setOnClickListener()

        return root
    }


    fun initFragment() {
        playBtn = root.findViewById(R.id.play_start_btn)

        imageBanner = root.findViewById(R.id.play_img_banner)

        getImageBanner()
    }

    //get play activity image banner from firebase remote config
    fun getImageBanner() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // every hour
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetch()
            .addOnCompleteListener(this.requireActivity(),
                OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {

                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        remoteConfig.activateFetched()

                    } else {
                        Toast.makeText(
                            this.requireContext(), "Fetch Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val imgUrl = remoteConfig.getString("image_banner")
                    setImageBanner(imgUrl)
                })
    }

    fun setImageBanner(url: String) {
        Picasso.get().load(url).into(imageBanner)
    }

    fun setOnClickListener() {
        playBtn.setOnClickListener {
            startLevel(1)
        }
    }


    //transition to the levels activity
    fun startLevel(id: Int) {
        val intent = Intent(this.requireContext(), CurrentLevelActivity::class.java)
        intent.putExtra("level", id)
        startActivity(intent)
    }
}