package com.example.pengaduan_firebase_13

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pengaduan_firebase_13.databinding.ActivityAddPengaduanBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddPengaduanActivity : AppCompatActivity() {
    // inisiasi
    private val firestore = FirebaseFirestore.getInstance()
    private val pengaduanCollectionRef = firestore.collection("pengaduann")
    private lateinit var binding: ActivityAddPengaduanBinding
    private var updateId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPengaduanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ambil data dari intent
        val selectedPengaduan = intent.getSerializableExtra("selectedPengaduan") as? Pengaduan

        with(binding) {
            if (selectedPengaduan != null) {
                // jika ada list pengaduan, set nilai awal untuk tambah pengaduan
                nameEditText.setText(selectedPengaduan.name)
                titleEditText.setText(selectedPengaduan.title)
                pengaduanEditText.setText(selectedPengaduan.aduan)

                // jika aduan telah diisi maka simpan id untuk update
                updateId = selectedPengaduan.id
            }

            saveButton.setOnClickListener {
                // ambil data dari form add pengaduan
                val name_pengaduan = nameEditText.text.toString()
                val judul_pengaduan = titleEditText.text.toString()
                val detail_pengaduan = pengaduanEditText.text.toString()

                // create objek cpengaduan
                val pengaduan = Pengaduan(name = name_pengaduan, title = judul_pengaduan, aduan = detail_pengaduan)

                if (updateId.isNotEmpty()) {
                    // jika ada updateId, berarti mode editing on
                    pengaduan.id = updateId
                    updatePengaduan(pengaduan)
                }
                else {
                    // jika tidak ada updateId, berarti mode add pengaduan baru
                    addPengaduan(pengaduan)
                }

                // kirim data ke halaman sebelumnya
                val intent = Intent(this@AddPengaduanActivity, MainActivity::class.java)
                intent.putExtra("pengaduanData", pengaduan)
                startActivity(intent)
            }
        }
    }

    private fun addPengaduan(pengaduan: Pengaduan) {
        // menambah pengaduan ke firestore
        pengaduanCollectionRef.add(pengaduan)
            .addOnSuccessListener { documentReference ->
                val createdPengaduanId = documentReference.id
                pengaduan.id = createdPengaduanId
                documentReference.set(pengaduan)
                    .addOnFailureListener {
                        Log.d("AddPengaduanActivity", "Error updating pengaduan ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("AddPengaduanActivity", "Error adding pengaaduan: ", it)
            }
    }

    private fun updatePengaduan(pengaduan: Pengaduan) {
        // update pengaduan di firestore berdasarkan ID
        pengaduanCollectionRef.document(pengaduan.id).set(pengaduan)
            .addOnFailureListener {
                Log.d("AddPengaduanActivity", "Error updating pengaduan ", it)
            }
    }
}