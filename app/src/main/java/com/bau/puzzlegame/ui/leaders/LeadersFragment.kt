package com.bau.puzzlegame.ui.leaders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bau.puzzlegame.R
import com.bau.puzzlegame.model.userModel
import com.bau.puzzlegame.ui.leaders.adapter.LeaderAdapter
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class LeadersFragment : Fragment() {

    //region Variables
    lateinit var root: View
    lateinit var leadarRecyclerView: RecyclerView
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
    fun initfragment() {

        leadarRecyclerView = root.findViewById(R.id.leaders_rv_leaderBoard)
        manger = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, true)
        leadarRecyclerView.layoutManager = manger
        leadarRecyclerView.setHasFixedSize(true)


    }

    //get leaders data from firebase database realtime
    fun setDataLeader() {

        var mLeaderReference = FirebaseDatabase.getInstance().getReference("users")
        val query = mLeaderReference!!.orderByChild("score").limitToLast(10)

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
                leadarRecyclerView.scrollToPosition(mAdapter!!.itemCount - 1)
            }
        }

        leadarRecyclerView.adapter = mAdapter


    }

}