package com.id.soulution.fishcatalog.helpers

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.id.soulution.fishcatalog.modules.models.Catalogue
import com.id.soulution.fishcatalog.modules.models.Category

class SeederDummy (var activity: Activity, var auth: FirebaseAuth, var fdb: FirebaseDatabase) {
    private val listItem: MutableList<Catalogue> = arrayListOf()

//    fun seed() {
//        listItem.add(Catalogue(
//            fdb.getReference("catalogue").push().key!!,
//            auth.uid!!,
//            listOf<Category>(
//                Category(0, "Abu - abu"),
//                Category(1, "Chanos chanos"),
//                Category(2, "Chanos"),
//                Category(3, "Chanidae"),
//                Category(4, "Gonorynchiformes"),
//                Category(5, "Actinopterygii"),
//                Category(6, "Chordata")
//            ).toMutableList(),
//            "Ikan Banden",
//            "Ikan muda disebut nener dikumpulkan orang dari sungai-sungai dan dibesarkan di tambak-tambak. Di sana mereka bisa diberi makanan apa saja dan tumbuh dengan cepat. Setelah cukup besar (biasanya sekitar 25–30 cm) bandeng dijual dalam keadaan segar atau sudah dibekukan. Bandeng diolah dengan cara digoreng, dibakar, dikukus, dipindang, atau diasap.",
//        "Ikan Bandeng pertama kali dibudidayakan di daerah Sulawesi Selatan, Makassar,Indonesia",
//            2,
//            "https://firebasestorage.googleapis.com/v0/b/fishcatalog-c7bec.appspot.com/o/fishImages%2FIkan%20Bandeng.jpg?alt=media&token=170c6f9a-3e21-4e54-a828-ac381b796deb"
//        ))
//        listItem.add(Catalogue(
//            fdb.getReference("catalogue").push().key!!,
//            auth.uid!!,
//            listOf<Category>(
//                Category(0, "Abu - abu"),
//                Category(1, "M. seheli"),
//                Category(2, "Moolgarda"),
//                Category(3, "Mugilidae"),
//                Category(4, "Mugiliformes"),
//                Category(5, "Actinopterygii"),
//                Category(6, "Chordata")
//            ).toMutableList(),
//            "Ikan Belanak",
//            "Ikan belanak secara umum bentuknya memanjang agak langsing dan gepeng. Sirip punggung terdiri dari satu jari-jari keras dan delapan jari-jari lemah. Sirip dubur berwarna putih kotor terdiri dari satu jari-jari keras dan sembilan jari-jari lemah. Bibir bagian atas lebih tebal daripada bagian bawahnya ini berguna untuk mencari makan di dasar/organisme yang terbenam dalam lumpur (kriswantoro dan Sunyoto, 1986). Ciri lain dari ikan belanak yaitu mempunyai gigi yang amat kecil, tetapi kadang-kadang pada beberapa spesies tidak ditemukan sama sekali.",
//            "Belanak tersebar di perairan tropis dan subtropis (FAO, 1974 dalam Adrim et al., 1988), juga ditemukan di air payau dan kadang-kadang di air tawar (Iversen, 1976). Di kawasan Pasifik belanak ditemukan di Fiji, Samoa, New Caledonia dan Australia. Sedangkan di Asia, banyak ditemukan di Indonesia, India, Filipina, Malaysia dan Srilangka.",
//            1,
//            "https://firebasestorage.googleapis.com/v0/b/fishcatalog-c7bec.appspot.com/o/fishImages%2FIkan%20Belanak.jpg?alt=media&token=50f8723e-6b5d-4a86-a259-2a4240ed9a5f"
//        ))
//        listItem.add(Catalogue(
//            fdb.getReference("catalogue").push().key!!,
//            auth.uid!!,
//            listOf<Category>(
//                Category(0, "Merah mudah"),
//                Category(1, " L. bitaeniatus"),
//                Category(2, "Lutjanus"),
//                Category(3, "Lutjanidae"),
//                Category(4, "Perciformes"),
//                Category(5, "Actinopterygii"),
//                Category(6, "Chordata")
//            ).toMutableList(),
//            "Ikan Kakap Merah Sejati",
//            "Terdapat dua jenis ikan kakap, kakap merah (Red Snapper) dan kakap putih (White Seabass). Sekalipun sama-sama bernama kakap, kedua ikan ini berasal dari suku ikan yang berbeda Inilah yang disebut kakap merah sejati, karena warna tubuhnya lebih merah daripada jenis lainnya. panjangnya bisa sampai 60 cm. Dibandingkan dengan jenahan, kakap merah (red snapper) memiliki badan lebih lebar dan jarak antar kedua matanya yang menonjol lebih panjang. Sisik-sisiknya lebih kecil dan teratur. Ujung tutup insangnya mencapai bagian bawah pangkal sirip punggung. Sedangkan panjang sirip dada dan perutnya sampai lubang anus.",
//            "Ikan kakakp merah sejati di budidayakan di daerah pula jawa,Indonesia",
//            2,
//            "https://firebasestorage.googleapis.com/v0/b/fishcatalog-c7bec.appspot.com/o/fishImages%2FIkan%20Kakap%20Merah%20Sejati.jpg?alt=media&token=24293acd-c7a2-45bb-a312-ced199837b9c"
//        ))
//
//        for (item: Catalogue in listItem) {
//            this.doSeeder(item)
//        }
//    }

    private fun doSeeder(item: Catalogue) {
        this.fdb.getReference("catalogue").child(item.uid).setValue(item)
    }
}