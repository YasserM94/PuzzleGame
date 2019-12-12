package com.bau.puzzlegame.ui.play.levelsFragment.level1


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressFlower
import com.bau.puzzlegame.R
import com.bau.puzzlegame.helper.MyProgressDialog
import com.bau.puzzlegame.helper.changeFragmentInterface
import com.bau.puzzlegame.helper.sharedPreferenses
import com.bau.puzzlegame.helper.userScore
import com.bau.puzzlegame.model.PuzzleModel
import com.bau.puzzlegame.ui.play.levelsFragment.level1.adapterLevel1.adapterLevel1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.puzzle_card.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class Level1Fragment : Fragment() {

    companion object {
        fun newInstance(currentActivity: changeFragmentInterface): Level1Fragment {
            val newinsta = Level1Fragment()
            newinsta.interfaceMain = currentActivity
            return newinsta
        }
    }


    //region Variables
    lateinit var root: View
    lateinit var interfaceMain: changeFragmentInterface
    lateinit var recyclerView: RecyclerView
    lateinit var manager: RecyclerView.LayoutManager
    lateinit var adapter: adapterLevel1
    lateinit var list: ArrayList<PuzzleModel>
    lateinit var listOfPuzzle: Array<PuzzleModel>
    private var mStorageRef: StorageReference? = null
    lateinit var listOfIndexes: ArrayList<Int>
    lateinit var listFiller: ArrayList<Int>
    lateinit var timerTv: TextView
    lateinit var scoreTv: TextView
    lateinit var backLevel: TextView
    lateinit var nextLevel: TextView
    var firstPress = true
    var idForFirstPress = ""
    var idForSecondPress = ""
    var firstPosition: Int = -1
    var secondPosition: Int = -1
    var level_Size: Int = -1
    lateinit var timer: CountDownTimer
    var score: Long = 0
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    var Progressdialog: ACProgressFlower? = null
    lateinit var usernameValue: String
    var oldview: View? = null
    var newview: View? = null
    var clickable: Boolean = true

    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_level1, container, false)
        initFragment()

        getDataForLivel1()
        nextLevel()

        return root
    }


    fun initFragment() {

        setLevelSize(6)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users")
        Progressdialog = MyProgressDialog().getInstanc(this.requireActivity())

        usernameValue = sharedPreferenses(this.context!!).getUsername()!!

        list = ArrayList<PuzzleModel>()
        listOfPuzzle = Array(level_Size) { PuzzleModel() }
        listOfIndexes = ArrayList<Int>()
        listFiller = ArrayList<Int>()
        fillListOfIndexesForLevel1()
        mStorageRef = FirebaseStorage.getInstance().getReference();
        recyclerView = root.findViewById(R.id.level1_recyclerView)
        timerTv = root.findViewById(R.id.level1_timer)
        scoreTv = root.findViewById(R.id.score)
        nextLevel = root.findViewById(R.id.level_tv_next)
        manager = GridLayoutManager(this.requireContext(), 3, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = manager
        onClickListner()
        setupTimer(3)
        userScore = 0
        scoreTv.text = "$userScore"

    }


    fun setLevelSize(size: Int) {
        level_Size = size
    }

    fun onClickListner() {

        recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (clickable) {

                    if (firstPress) {
                        firstPosition = position
                        firstClick(firstPosition, view)
                    } else {
                        clickable = false
                        secondPosition = position
                        secondClick(secondPosition, view)
                    }
                }

            }
        })
    }

    //region Selected Pictures Comparision
    fun firstClick(index: Int, view: View) {
        idForFirstPress = listOfPuzzle[index].id
        firstPress = false
        oldview = view.puzzle_card_image
        showImage((oldview as ImageView?)!!)
    }

    fun secondClick(position: Int, view: View) {
        idForSecondPress = listOfPuzzle[position].id
        if (firstPosition != secondPosition) {
            newview = view.puzzle_card_image
            showImage((newview as ImageView?)!!)
            checkItems((newview as ImageView?)!!)
        } else {
            clickable = true
            adapter.notifyDataSetChanged()
        }
        firstPress = true
    }


    fun checkItems(view: ImageView) {
        if (compareToItem(idForFirstPress, idForSecondPress)) {
            currectChoies()
        } else {
            hidImages(oldview!!, view)
        }
    }

    fun currectChoies() {
        listOfPuzzle[firstPosition].visible = false
        listOfPuzzle[secondPosition].visible = false
        clickable = true
        adapter.notifyDataSetChanged()

        if (checkCompletLevel()) {
            showDialog()
        }
    }

    fun showImage(view: ImageView) {
        view.alpha = 1f
    }


    fun hidImages(view1: View, view2: View) {
        Timer("hidenImages", false).schedule(1000) {
            view1.alpha = 0f
            view2.alpha = 0f
            clickable = true
        }


    }


    fun compareToItem(firstId: String, secondId: String): Boolean {
        if (firstId == secondId)
            score += 60//increase score for each correct comparision
        return firstId == secondId
    }

    //endregion


    fun getDataForLivel1() {

        val storage = FirebaseStorage.getInstance()

        val listRef = storage.reference.child("images/puzzleImages")

        listRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }
                listResult.items.forEachIndexed { index, item ->
                    // All the items under listRef.
                    if (listOfIndexes.contains(index)) {
                        val url = item.path
                        val name = url.substring(url.lastIndexOf("/"))
                        var puzzleItem =
                            PuzzleModel(item.name, name, item.name)
                        list!!.add(puzzleItem)
                    }
                }
                dublicateList()
                adapter = adapterLevel1(listOfPuzzle)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
                var x = it.message
                var z = 0
            }

    }

    //duplicate objects for this level
    fun dublicateList() {
        preparListOfPostitions(level_Size)
        var size = list.size
        var j = 0
        for (i in 0..size - 1) {
            val obj = list[i]
            listOfPuzzle[listFiller[j++]] = PuzzleModel(obj.id, obj.urlImage, obj.name)
            listOfPuzzle[listFiller[j++]] = PuzzleModel(obj.id, obj.urlImage, obj.name)

        }
    }

    //check level completion
    fun checkCompletLevel(): Boolean {
        listOfPuzzle.forEach {
            if (it.visible) {
                return false
            }
        }
        return true
    }


    //choose images positions randomly
    fun fillListOfIndexesForLevel1() {
        while (listOfIndexes.size < level_Size / 2) {
            var index = generateRandomNumber(12)//total of images
            if (!listOfIndexes.contains(index)) {
                listOfIndexes.add(index)
            }
        }
    }

    //check is list of positions full
    fun preparListOfPostitions(size: Int) {
        while (listFiller.size < size) {
            var index = generateRandomNumber(size)
            if (!listFiller.contains(index)) {
                listFiller.add(index)
            }
        }
    }

    fun generateRandomNumber(range: Int): Int {
        return (0..range - 1).random()
    }

    //after level finish
    fun showDialog() {
        score += (timerTv.text.toString().toLong()) * 3
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Level Finished")
        builder.setMessage("Your score is:  $score")

        builder.setPositiveButton("Next") { _, _ ->
            this.getUserScore()
        }
        builder.setNegativeButton("Repeat") { _, _ ->
            this.resetAllFields()
        }
        builder.setCancelable(false)
        builder.show()
        timer.cancel()
    }

    fun nextLevel() {
        nextLevel.setOnClickListener {
            interfaceMain.onChangeRequest(2)
        }
    }

    fun backLevel() {
        nextLevel.setOnClickListener {
            interfaceMain.onChangeRequest(1)
        }
    }

    //transition to next level
    fun goToNextLevel() {
        timer.cancel()
        interfaceMain.onChangeRequest(2)
    }

    //game timer(3 min)
    fun setupTimer(minute: Long) {
        val timeInMillis = minute * 60000
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished / 1000
                timerTv.text = "$time"
            }

            override fun onFinish() {
                showDialogForTimeFinish()
            }
        }
        timer.start()
    }

    fun showDialogForTimeFinish() {
        val builder = AlertDialog.Builder(activity!!.baseContext)
        builder.setTitle("test")
        builder.setMessage("time is over do you want to replay?")

        builder.setNeutralButton(android.R.string.ok) { _, _ ->
            this.resetAllFields()
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            getActivity()!!.finish()
        }
        builder.show()
    }

    //reset to repeat level
    fun resetAllFields() {
        timer.cancel()
        interfaceMain.onChangeRequest(1)
    }

    //store user score in firebase
    fun storeUserScore(newScore: Long) {
        val newUser = myRef.child(usernameValue)
        newUser.child("score").setValue(newScore)
        Progressdialog!!.dismiss()
        this.goToNextLevel()
    }

    //get user score from firebase
    fun getUserScore() {
        Progressdialog!!.show()
        var oldScore: Long
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(usernameValue)) { // run some code
//                    oldScore =
//                        snapshot.child(usernameValue).child("score").value as Long
                    userScore = 0 + score
                    storeUserScore(userScore)
                } else {
                    oldScore = 0
                }
            }
        })
    }


    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view?.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }


}
