// FirestoreManager.kt 파일

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()

    fun saveSpokenText(text: String) {
        val data = hashMapOf(
            "text" to text,
            "timestamp" to FieldValue.serverTimestamp() // 현재 시각 저장
        )

        db.collection("spokenTexts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                // 저장 성공 시 처리
                println("데이터 저장 성공: Document ID - ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                // 저장 실패 시 처리
                println("데이터 저장 실패: ${e.message}")
            }
    }

    // 데이터베이스에서 데이터를 읽어와서 목록으로 반환
    suspend fun getSavedTextListFromDatabase(): List<String> {
        val textList = mutableListOf<String>()
        try {
            val snapshot = db.collection("spokenTexts").get().await()
            for (dataSnapshot in snapshot.documents) {
                val text = dataSnapshot.getString("text")
                text?.let {
                    textList.add(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return textList
    }

    // 텍스트 저장 날짜 가져오기 함수
    suspend fun getSpokenTextDate(text: String): String? {
        try {
            val snapshot = db.collection("spokenTexts")
                .whereEqualTo("text", text)
                .get()
                .await()

            for (document in snapshot.documents) {
                val timestamp = document.getTimestamp("timestamp")
                return timestamp?.toDate()?.toString() ?: "Unknown Date"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // 데이터 삭제 함수
    suspend fun deleteSpokenText(text: String) {
        try {
            val snapshot = db.collection("spokenTexts")
                .whereEqualTo("text", text)
                .get()
                .await()

            for (document in snapshot.documents) {
                db.collection("spokenTexts").document(document.id).delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


