package com.example.pengaduan_firebase_13

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.pengaduan_firebase_13.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // inisiasi
    private lateinit var binding: ActivityMainBinding
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val pengaduanList: ArrayList<Pengaduan> = ArrayList()
    private val pengaduanListLiveData: MutableLiveData<List<Pengaduan>> by lazy {
        MutableLiveData<List<Pengaduan>>()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // atur click listener untuk add button untuk mengarahkan ke AddPengaduanActivity
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddPengaduanActivity::class.java)
            startActivity(intent)
        }

        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        listView.adapter = adapter

        // menerima data dari intent
        val pengaduanData = intent.getSerializableExtra("pengaduanData") as? Pengaduan

        // menampilkan data di halaman lain dan tambahkan ke list
        pengaduanData?.let {
            // menambahkan pengaduan baru ke dalam list
            pengaduanList.add(it)
            // update adapter dengan list pengaduan baru
            updateAdapter()
            // clear intent data untuk menghindari duplikat data
            intent.removeExtra("pengaduanData")
        }

        // memperbarui list
        observePengaduan()

        // atur click listener untuk listView
        with(binding) {
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedPengaduan = pengaduanList[position]

                // Intent untuk membuka DetailPengaduan dengan data pengaduan yang dipilih
                val intent = Intent(this@MainActivity, DetailPengaduan::class.java)
                intent.putExtra("selectedPengaduan", selectedPengaduan)
                startActivity(intent)
            }
        }
    }

    private fun updateAdapter() {
        // Clear the adapter and add all items from the list
        adapter.clear()
        for (pengaduan in pengaduanList) {
            val displayText =
                "Name    :    ${pengaduan.name}\nTitle       :   ${pengaduan.title}\nContent:   ${pengaduan.aduan}"
            adapter.add(displayText)
        }
    }

    private fun observePengaduan() {
        val firestore = FirebaseFirestore.getInstance()
        val pengaduanCollectionRef = firestore.collection("pengaduann")

        pengaduanCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for pengaduan changes: ", error)
                return@addSnapshotListener
            }

            // Clear the existing list
            pengaduanList.clear()

            snapshots?.forEach { documentSnapshot ->
                val pengaduan = documentSnapshot.toObject(Pengaduan::class.java)
                if (pengaduan != null) {
//                    pengaduan.id = documentSnapshot.id
                    pengaduanList.add(pengaduan)
                }
            }

            // Notify LiveData observer
            pengaduanListLiveData.postValue(pengaduanList)

            // Update the adapter with the new list
            updateAdapter()
        }
    }
}
