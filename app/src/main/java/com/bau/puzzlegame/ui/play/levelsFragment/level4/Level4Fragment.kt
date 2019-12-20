package com.bau.puzzlegame.ui.play.levelsFragment.level4


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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


class Level4Fragment : Fragment() {


    companion object {
        fun newInstance(currentActivity: changeFragmentInterface): Level4Fragment {
            val newinsta = Level4Fragment()
            newinsta.interfaceMain = currentActivity
            return newinsta
        }
    }

    //region variables

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
    lateinit var scoreTv: TextView

    //endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_level4, container, false)
        initFragment()

        getDataForLivel1()

        nextLevel()
        backLevel()

        return root
    }

    fun initFragment() {

        setLevelSize(16)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users")
        Progressdialog = MyProgressDialog().getInstanc(this.requireActivity())

        usernameValue = sharedPreferenses(this.context!!).getUsername()!!
        scoreTv = root.findViewById(R.id.score)
        nextLevel = root.findViewById(R.id.level_tv_next)
        backLevel = root.findViewById(R.id.level_tv_back)
        list = ArrayList<PuzzleModel>()
        listOfPuzzle = Array(level_Size) { PuzzleModel() }
        listOfIndexes = ArrayList<Int>()
        listFiller = ArrayList<Int>()
        fillListOfIndexesForLevel1()
        mStorageRef = FirebaseStorage.getInstance().reference
        recyclerView = root.findViewById(R.id.level4_recyclerView)
        timerTv = root.findViewById(R.id.level4_timer)
        manager = GridLayoutManager(this.requireContext(), 4, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = manager
        onClickListner()
        setupTimer(3)
        scoreTv.text = "$userScore"

    }

    fun setLevelSize(size: Int) {
        level_Size = size
    }

    private fun onClickListner() {

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

    //region selected pictures comparision
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

        if (checkCompleteLevel()) {
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
            score += 160//increase score for each correct comparision
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
                        val puzzleItem =
                            PuzzleModel(item.name, name, item.name)
                        list.add(puzzleItem)
                    }
                }
                duplicateList()
                adapter = adapterLevel1(listOfPuzzle)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }

    }

    //Duplicate objects for this level
    private fun duplicateList() {
        prepareListOfPositions(level_Size)
        val size = list.size
        var j = 0
        for (i in 0 until size) {
            val obj = list[i]
            listOfPuzzle[listFiller[j++]] = PuzzleModel(obj.id, obj.urlImage, obj.name)
            listOfPuzzle[listFiller[j++]] = PuzzleModel(obj.id, obj.urlImage, obj.name)

        }
    }

    //check level completion
    private fun checkCompleteLevel(): Boolean {
        listOfPuzzle.forEach {
            if (it.visible) {
                return false
            }
        }
        return true
    }


    //choose images positions randomly
    private fun fillListOfIndexesForLevel1() {
        while (listOfIndexes.size < level_Size / 2) {
            val index = generateRandomNumber(9)//total of images
            if (!listOfIndexes.contains(index)) {
                listOfIndexes.add(index)
            }
        }
    }

    //check is list of postitions full
    private fun prepareListOfPositions(size: Int) {
        while (listFiller.size < size) {
            val index = generateRandomNumber(size)
            if (!listFiller.contains(index)) {
                listFiller.add(index)
            }
        }
    }

    private fun generateRandomNumber(range: Int): Int {
        return (0..range - 1).random()
    }

    //after level finish
    private fun showDialog() {
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

    private fun backLevel() {
        backLevel.setOnClickListener {
            goToNextLevel(3)
        }
    }

    private fun nextLevel() {
        nextLevel.setOnClickListener {
            goToNextLevel(5)
        }
    }

    //transition to next level
    fun goToNextLevel(level: Int) {
        timer.cancel()
        interfaceMain.onChangeRequest(level)
    }

    //game timer(3 min)
    private fun setupTimer(minute: Long) {
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
        /*val builder = AlertDialog.Builder(activity!!.baseContext)
        builder.setTitle("Game Over")
        builder.setMessage("Time is over! Do you want to try again?")

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            this.resetAllFields()
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            activity!!.finish()
        }
        builder.show()*/
        /* val builder = AlertDialog.Builder(activity!!.baseContext)
      builder.setTitle("Game Over")
      builder.setMessage("Time is over! Do you want to try again?")

      builder.setPositiveButton(android.R.string.yes) { _, _ ->
          this.resetAllFields()
      }
      builder.setNegativeButton(android.R.string.no) { _, _ ->
          activity!!.finish()
      }
      builder.show()*/
        Toast.makeText(this.requireContext(),"Time is over!", Toast.LENGTH_LONG).show()
        activity!!.finish()
    }

    //reset to repeat level
    private fun resetAllFields() {
        timer.cancel()
        interfaceMain.onChangeRequest(4)
    }

    //store user score in firebase
    fun storeUserScore(newScore: Long) {
        val newUser = myRef.child(usernameValue)
        newUser.child("score").setValue(newScore)
        Progressdialog!!.dismiss()
        this.goToNextLevel(5)
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
                    oldScore =
                        snapshot.child(usernameValue).child("score").value as Long
                    userScore = oldScore + score
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

    private fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }


}
