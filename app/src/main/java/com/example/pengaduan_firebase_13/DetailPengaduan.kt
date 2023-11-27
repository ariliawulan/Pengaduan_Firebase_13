package com.example.pengaduan_firebase_13

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.pengaduan_firebase_13.databinding.ActivityDetailPengaduanBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailPengaduan : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPengaduanBinding
    private lateinit var nameDetail: TextView
    private lateinit var titleDetail: TextView
    private lateinit var contentDetail: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var selectedPengaduan: Pengaduan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPengaduanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inisialisasi komponen interface
        nameDetail = findViewById(R.id.nameDetail)
        titleDetail = findViewById(R.id.titleDetail)
        contentDetail = findViewById(R.id.contentDetail)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)

        // mendapatkan data pengaduan dari intent (list viewnya)
        selectedPengaduan = intent.getSerializableExtra("selectedPengaduan") as Pengaduan

        // mengisi nilai TextView dengan data pengaduan
        nameDetail.text = "Name: ${selectedPengaduan.name}"
        titleDetail.text = "Title: ${selectedPengaduan.title}"
        contentDetail.text = "Content: ${selectedPengaduan.aduan}"

        // atur click listener untuk button edit
        btnEdit.setOnClickListener {
            // intent untuk membuka AddActivity dengan mode edit
            val intent = Intent(this, AddPengaduanActivity::class.java)
            intent.putExtra("selectedPengaduan", selectedPengaduan)
            startActivity(intent)
        }

        // atur click listener untuk button delete
        btnDelete.setOnClickListener {
            if (selectedPengaduan.id.isEmpty()) {
                Log.d("MainActivity", "Error deleting: feedback ID is empty!")
                return@setOnClickListener
            }

            val firestore = FirebaseFirestore.getInstance()
            val pengaduanCollectionRef = firestore.collection("pengaduann")

            pengaduanCollectionRef.document(selectedPengaduan.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("MainActivity", "Successfully Deleting Feedback")
                    // jika aksi hapus berhasil, maka ia akan kembali ke Main Activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // close activity saat ini
                }
                .addOnFailureListener { e ->
                    Log.d("MainActivity", "Error deleting feedback: ", e)
                }
        }
    }
}