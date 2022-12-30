package com.clevertec.task3.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.clevertec.task3.R;
import com.clevertec.task3.dao.ContactDao;
import com.clevertec.task3.database.AppDatabase;
import com.clevertec.task3.interfaces.OnBackPressedListener;
import com.clevertec.task3.model.Contact;
import com.clevertec.task3.singleton.ConnectionSingleton;

import java.util.ArrayList;

public class SelectContactFragment extends Fragment implements OnBackPressedListener {

    private final AppDatabase appDatabase = ConnectionSingleton.getInstance(null).getAppDatabase();

    public static SelectContactFragment newInstance() {
        return new SelectContactFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_contact, container, false);

        ContentResolver contentResolver = this.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        ArrayList<String> contacts = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                contacts.add(name);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, contacts);
        ListView contactList = view.findViewById(R.id.list_view);
        contactList.setAdapter(adapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonPrevious = view.findViewById(R.id.button_previous);
        ListView listView = view.findViewById(R.id.list_view);

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMainFragment();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getAdapter().getItem(position);
                String phone = getInfo(TypeInfo.PHONE, name, view.getContext());
                String email = getInfo(TypeInfo.EMAIL, name, view.getContext());

                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhone(phone);
                contact.setEmail(email);

                ContactDao contactDao = appDatabase.contactDao();
                if (contactDao.findByName(name) == null) {
                    contactDao.save(contact);
                    Contact saved = contactDao.findByName(name);
                    Toast.makeText(getActivity(), (saved == null ? R.string.error_contact : R.string.save_contact), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.copy_contact, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void toMainFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(this)
                .replace(R.id.container, MainFragment.newInstance())
                .commit();
    }

    private String getInfo(TypeInfo type, String name, Context context) {
        switch (type) {
            case PHONE:
                return getInfo(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        name, context
                );
            case EMAIL:
                return getInfo(
                        ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        name, context
                );
            default:
                return null;
        }
    }

    private String getInfo(String displayName, String arrayElement, Uri uri, String name, Context context) {
        String selection = displayName + " like'%" + name + "%'";
        String[] array = new String[]{arrayElement};
        Cursor cursor = context.getContentResolver().query(uri, array, selection, null, null);
        String result = "Не указан";
        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }

    private enum TypeInfo {
        PHONE,
        EMAIL
    }

    @Override
    public void onBackPressed() {
        toMainFragment();
    }
}