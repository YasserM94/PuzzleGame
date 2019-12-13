package com.bau.puzzlegame.ui.leaders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressFlower
import com.bau.puzzlegame.R
import com.bau.puzzlegame.helper.MyProgressDialog
import com.bau.puzzlegame.model.userModel
import com.bau.puzzlegame.ui.leaders.adapter.LeaderAdapter
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class LeadersFragment : Fragment() {

    //region Variables
    private lateinit var root: View
    lateinit var leadersRecyclerView: RecyclerView
    private var progressDialog: ACProgressFlower? = null
    lateinit var manger: LinearLayoutManager
    private var mAdapter: FirebaseRecyclerAdapter<userModel, LeaderAdapter>? = null
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_leaders, container, false)
        initfragment()
        setDataLeader()
        return root
    }

    //set leaders activity
    private fun initfragment() {
        leadersRecyclerView = root.findViewById(R.id.leaders_rv_leaderBoard)
        manger = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, true)
        leadersRecyclerView.layoutManager = manger
        leadersRecyclerView.setHasFixedSize(true)
        progressDialog = MyProgressDialog().getInstanc(this.requireActivity())
        progressDialog!!.show()

    }

    //get leaders data from firebase database realtime
    private fun setDataLeader() {
        val mLeaderReference = FirebaseDatabase.getInstance().getReference("users")
        val query = mLeaderReference.orderByChild("score").limitToLast(12)

        mAdapter = object : FirebaseRecyclerAdapter<userModel, LeaderAdapter>(
            userModel::class.java, R.layout.leaders_card, LeaderAdapter::class.java, query
        ) {
            override fun populateViewHolder(
                viewHolder: LeaderAdapter?,
                model: userModel?,
                position: Int
            ) {

                viewHolder!!.bindLeader(model)
            }

            override fun onChildChanged(
                type: ChangeEventListener.EventType,
                snapshot: DataSnapshot?,
                index: Int,
                oldIndex: Int
            ) {
                super.onChildChanged(type, snapshot, index, oldIndex)
                mAdapter!!.startListening()
                leadersRecyclerView.scrollToPosition(mAdapter!!.itemCount - 1)
                progressDialog!!.dismiss()
            }
        }

        leadersRecyclerView.adapter = mAdapter


    }

}