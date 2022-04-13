package com.android.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.android.chatapp.R;
import com.android.chatapp.databinding.ActivityMainBinding;

import java.util.prefs.PreferenceChangeEvent;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        preferenceManager= new PreferenceManager(getApplicationContext());

    }
    private void setListeners(){
        binding.imageSignOut.setOnClickListener(e -> signOut());
        binding.fabNewChat.setOnClickListener(e ->
                startActivity(new Intent(getApplicationContext(),UsersActivity.class)));
    }
    //    private void LoadUserDetails(){
//        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
//        byte[] bytes= Base64. decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64. DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        binding.imageProfile.setImageBitmap(bitmap);
//    }
    private void showToast(String mess){
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
    }
    private void signOut() {
//        showToast("Signing out...");
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference =
//                database.collection(Constants. KEY_COLLECTION_USERS).document(
//                        preferenceManager.getString(Constants. KEY_USER_ID)
//                );
//                                       = new HashMap<>(); HashMap<String, Object> updates
//        updates.put(Constants. KEY_FCM_TOKEN, FieldValue.delete(O);
//        documentReference.update(updates)
//                .addOnSuccessListener(unused -> {
//                    preferenceManager.clear();
//                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
//                    finish();
//                })
//                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }
}