package com.example.blooddonationapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonationapplication.DataAccess.ApplyAdapter
import com.example.blooddonationapplication.data.BloodDonationApplyment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class ApplymentsActivity : AppCompatActivity()  {
    private val db=FirebaseFirestore.getInstance()
    private val collectionRef=db.collection("apply")
    private var adapter:ApplyAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applyment)
        setRecyclerView()
    }
    private fun setRecyclerView() {
        val bloodGroup=getSharedPreferences("sharedPrefs", MODE_PRIVATE).getString("bloodGroup","")
        val query=collectionRef.whereEqualTo("complete",false).whereEqualTo("bloodGroup",bloodGroup)
        val options:FirestoreRecyclerOptions<BloodDonationApplyment> = FirestoreRecyclerOptions.Builder<BloodDonationApplyment>()
            .setQuery(query,BloodDonationApplyment::class.java).build()
        adapter= bloodGroup?.let { ApplyAdapter(options, it) }
        val recyclerView:RecyclerView=findViewById(R.id.rv_test)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.adapter=adapter
        adapter!!.setOnItemClickListener(object : ApplyAdapter.OnItemClickListener {
            override fun onItemClick(documentSnapshot: DocumentSnapshot?, position: Int) {
                val note: BloodDonationApplyment? = documentSnapshot?.toObject(BloodDonationApplyment::class.java)
                val id = documentSnapshot?.id
                val path = documentSnapshot?.reference?.path
                if (id != null) {
                    apply(id)
                }
                Toast.makeText(this@ApplymentsActivity,"Position $position ID: $id",Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun apply(id:String){
        val intent = Intent(this,ApplyActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }
    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }


}