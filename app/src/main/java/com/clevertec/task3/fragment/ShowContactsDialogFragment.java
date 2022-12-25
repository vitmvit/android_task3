package com.clevertec.task3.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.clevertec.task3.R;
import com.clevertec.task3.dao.ContactDao;
import com.clevertec.task3.database.AppDatabase;
import com.clevertec.task3.model.Contact;
import com.clevertec.task3.singleton.ConnectionSingleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ShowContactsDialogFragment extends DialogFragment {

    private final AppDatabase appDatabase = ConnectionSingleton.getInstance(null).getAppDatabase();
    private final String NAME_SP = "MySharedPreferences";
    private final String KEY_NAME = "name";
    private final String KEY_PHONE = "phone";
    private final String KEY_EMAIL = "email";
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_show_contacts, null);

        ContactDao contactDao = appDatabase.contactDao();
        List<Contact> contactList = contactDao.findAll();

        ListView listView = view.findViewById(R.id.list_view_dialog);
        ArrayAdapter<Contact> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, contactList);
        listView.setAdapter(adapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.list_view_dialog);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) parent.getAdapter().getItem(position);
                saveData(contact.getName(), contact.getPhone(), contact.getEmail());
                loadData();
                onDestroyView();
            }
        });
    }

    private void saveData(String name, String phone, String email) {
        sharedPreferences = this.getActivity().getSharedPreferences(NAME_SP, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_NAME, name);
        edit.putString(KEY_PHONE, phone);
        edit.putString(KEY_EMAIL, email);
        edit.apply();
    }

    private void loadData() {
        TextView nameContact = this.getActivity().findViewById(R.id.name_contact);
        TextView phoneContact = this.getActivity().findViewById(R.id.phone_contact);
        TextView emailContact = this.getActivity().findViewById(R.id.email_contact);

        sharedPreferences = this.getActivity().getSharedPreferences(NAME_SP, MODE_PRIVATE);
        String savedName = sharedPreferences.getString(KEY_NAME, "");
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        String savedPhone = sharedPreferences.getString(KEY_PHONE, "");
        nameContact.setText(savedName);
        emailContact.setText(savedEmail);
        phoneContact.setText(savedPhone);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}