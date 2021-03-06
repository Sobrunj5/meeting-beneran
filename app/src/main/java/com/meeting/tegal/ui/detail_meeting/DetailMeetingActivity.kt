package com.meeting.tegal.ui.detail_meeting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.api.load
import com.example.meeting.models.MeetingRoom
import com.example.meeting.utilities.Constants
import com.meeting.tegal.Partner
import com.meeting.tegal.R
import com.meeting.tegal.ui.order.OrderActivity
import kotlinx.android.synthetic.main.activity_detail_meeting.*

class DetailMeetingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_meeting)
        setUpDetailRoom()
    }

    private fun setUpDetailRoom() {
        tv_nama_ruangan.text = getPassedMeeting()?.nama_tempat
        getPassedMeeting()?.promo?.let {
            tv_harga1.text = Constants.setToIDR(it.promo_price!!)
        } ?:kotlin.run {
            tv_harga1.text = Constants.setToIDR(getPassedMeeting()?.harga_sewa!!)
        }
        tv_keterangan.text = getPassedMeeting()?.keterangan
        //tv_fasilitas1.text = getPassedMeeting()?.mitra!!.makanans.joinToString { makanan -> makanan.nama!!  }
        iv_ruangan.load(getPassedMeeting()?.foto)
        btn_pesan_sekarang.setOnClickListener {
            startActivity(Intent(this@DetailMeetingActivity, OrderActivity::class.java).apply {
                putExtra("ROOM", getPassedMeeting())
                putExtra("COMPANY", getPassedCompany())
                if (getPassedIsSearch()){
                    putExtra("DATE", getPassedDate())
                    putExtra("START_TIME", getPassedStartTime())
                    putExtra("END_TIME", getPassedEndTime())
                    putExtra("IS_SEARCH", true)
                }
        }) }
    }

    private fun getPassedMeeting() = intent.getParcelableExtra<MeetingRoom>("ROOM")
    private fun getPassedCompany() = intent.getParcelableExtra<Partner>("COMPANY")
    private fun getPassedDate() = intent.getStringExtra("DATE")
    private fun getPassedStartTime() = intent.getStringExtra("START_TIME")
    private fun getPassedEndTime() = intent.getStringExtra("END_TIME")
    private fun getPassedIsSearch() = intent.getBooleanExtra("IS_SEARCH", false)
}
