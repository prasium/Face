package karthiknaik.Face2Face.app.activity

import karthiknaik.core.extensions.copyTextToClipboard
import karthiknaik.core.extensions.makeGone
import karthiknaik.core.extensions.makeVisible
import karthiknaik.Face2Face.app.Face2Face
import karthiknaik.Face2Face.app.R
import karthiknaik.Face2Face.app.adapteritem.MeetingHistoryItem
import karthiknaik.Face2Face.app.databinding.ActivityMeetingHistoryBinding
import karthiknaik.Face2Face.app.model.Meeting
import karthiknaik.Face2Face.app.utils.MeetingUtils
import karthiknaik.Face2Face.app.viewmodel.MeetingHistoryViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.IUnityBannerListener
import com.unity3d.services.banners.UnityBanners
import kotlinx.android.synthetic.main.activity_meeting_history.*
import kotlinx.android.synthetic.main.item_meeting_history.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MeetingHistoryActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MeetingHistoryActivity::class.java)
            context.startActivity(intent)
        }
    }

    //Unity Banner Ad
    private val unityGameID = "3736875"
    private val testMode = true // set testMode to false for real ads!
    private val banner_id = "banner"


    private lateinit var binding: ActivityMeetingHistoryBinding
    private val viewModel by viewModel<MeetingHistoryViewModel>() // Lazy inject ViewModel

    private lateinit var meetingHistoryAdapter: FastItemAdapter<MeetingHistoryItem>
    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView(savedInstanceState)
        setupObservables()

        if (Face2Face.isAdEnabled)
        {
            //Unity Ads for Banner Listener
        UnityAds.initialize(this,unityGameID,testMode)

        val unityBannerListener = object : IUnityBannerListener {
            override fun onUnityBannerLoaded(p0: String?, p1: View?) {
                //Attach layout to show banner ad
                Log.d("avex","did")
                findViewById<ViewGroup>(R.id.bannerAd).addView(p1)
            }

            override fun onUnityBannerShow(p0: String?) {
                Log.d("sh","owing")
            }

            override fun onUnityBannerClick(p0: String?) {
            Log.d("bla","lalala")
            }

            override fun onUnityBannerHide(p0: String?) {

            }

            override fun onUnityBannerError(p0: String?) {

            }

            override fun onUnityBannerUnloaded(p0: String?) {
                Log.d("avx","didnt")
            }
        }
            UnityBanners.setBannerListener(unityBannerListener)
            UnityBanners.loadBanner(this,banner_id)
            //Loads up ad in the banner
    }
        else
        {
            bannerAd.makeGone()
        }
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        outState = meetingHistoryAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

//    override fun onPause() {
//        if (Face2Face.isAdEnabled) adView.pause()
//        super.onPause()
//    }
//
//    override fun onResume() {
//        if (Face2Face.isAdEnabled) adView.resume()
//        super.onResume()
//    }
//
//    override fun onDestroy() {
//        if (Face2Face.isAdEnabled) adView.destroy()
//        super.onDestroy()
//    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        meetingHistoryAdapter = FastItemAdapter()
        meetingHistoryAdapter.setHasStableIds(true)
        meetingHistoryAdapter.withSavedInstanceState(savedInstanceState)

       binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = meetingHistoryAdapter

        onMeetingCodeClick()
        onRejoinClick()
    }

    private fun setupObservables() {
        viewModel.meetingHistoryLiveData.observe(this, Observer { meetingHistoryList ->
            val meetingHistoryItems = ArrayList<MeetingHistoryItem>()

            for (meeting in meetingHistoryList) {
                meetingHistoryItems.add(MeetingHistoryItem(meeting))
            }

            FastAdapterDiffUtil[meetingHistoryAdapter.itemAdapter] = meetingHistoryItems
            showEmptyState(meetingHistoryAdapter.itemCount)
        })
    }

    private fun showEmptyState(itemCount: Int) {
        if (itemCount > 0) binding.groupEmpty.makeGone() else binding.groupEmpty.makeVisible()
    }

//    private fun setupBannerAd() {
//        adView = AdView(this)
//        binding.adViewContainer.addView(adView)
//        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
//            if (!initialLayoutComplete) {
//                initialLayoutComplete = true
//
//                adView.adUnitId = getString(R.string.banner_ad_id_meeting_history)
//                adView.adSize = getAdaptiveBannerAdSize(binding.adViewContainer)
//                adView.loadAd(AdRequest.Builder().build())
//            }
//        }
//    }

    /**
     * Returns the size of the Adaptive Banner Ad based on the screen width
     */
    private fun getAdaptiveBannerAdSize(adViewContainer: FrameLayout): AdSize {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adViewContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    /**
     * Called when the meeting code is clicked of a RecyclerView Item
     */
    private fun onMeetingCodeClick() {
        meetingHistoryAdapter.addEventHook(object : ClickEventHook<MeetingHistoryItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.itemView.tvMeetingCode
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<MeetingHistoryItem>,
                item: MeetingHistoryItem
            ) {
                copyTextToClipboard(
                    item.meeting.code,
                    getString(R.string.meeting_history_meeting_code_copied)
                )
            }
        })
    }

    /**
     * Called when the Rejoin button is clicked of a RecyclerView Item
     */
    private fun onRejoinClick() {
        meetingHistoryAdapter.addEventHook(object : ClickEventHook<MeetingHistoryItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.itemView.btnRejoinMeeting
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<MeetingHistoryItem>,
                item: MeetingHistoryItem
            ) {
                MeetingUtils.startMeeting(
                    this@MeetingHistoryActivity,
                    item.meeting.code,
                    R.string.all_rejoining_meeting
                ) // Start Meeting

                viewModel.addMeetingToDb(
                    Meeting(
                        item.meeting.code,
                        System.currentTimeMillis()
                    )
                ) // Add meeting to db
            }
        })
    }

}
