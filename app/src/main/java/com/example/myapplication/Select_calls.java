package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Select_calls extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 1;
    private LinearLayout contactsContainer;
    private Button btnSelect;
    private ArrayList<String> contactsList = new ArrayList<>();
    public ArrayList<String> selectedContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_calls);

        contactsContainer = findViewById(R.id.contactsContainer);
        btnSelect = findViewById(R.id.btnSelect);

        // Проверка разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            loadContacts();
        }

        // Обработчик кнопки выбора
        btnSelect.setOnClickListener(v -> {
            if (!selectedContacts.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String contact : selectedContacts) {
                    sb.append(contact).append("\n");
                }
                Toast.makeText(this, "Выбраны:\n" + sb.toString(), Toast.LENGTH_LONG).show();
                // Здесь можно добавить логику обработки выбранных контактов
            } else {
                Toast.makeText(this, "Выберите хотя бы один контакт", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContacts() {
        contactsContainer.removeAllViews();
        selectedContacts.clear();

        // Запрос контактов
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contact = name + "\n" + number;
                contactsList.add(contact);

                // Создаем CheckBox для каждого контакта
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(contact);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String contactText = checkBox.getText().toString();
                    if (isChecked) {
                        selectedContacts.add(contactText);
                    } else {
                        selectedContacts.remove(contactText);
                    }
                });
                contactsContainer.addView(checkBox);
            }
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
