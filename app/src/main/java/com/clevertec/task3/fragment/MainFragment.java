package com.clevertec.task3.fragment;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.clevertec.task3.MainActivity;
import com.clevertec.task3.R;
import com.clevertec.task3.dao.ContactDao;
import com.clevertec.task3.database.AppDatabase;
import com.clevertec.task3.model.Contact;
import com.clevertec.task3.singleton.ConnectionSingleton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {
    private static final int NOTIFICATION_ID = 234;
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final String NAME_SP = "MySharedPreferences";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String CHANNEL_DESCRIPTION = "This is my channel";
    private static String chanelId = "channel_default";
    private final AppDatabase appDatabase = ConnectionSingleton.getInstance(null).getAppDatabase();
    private SharedPreferences sharedPreferences;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonSelectContact = view.findViewById(R.id.select_contact);
        Button buttonShowContacts = view.findViewById(R.id.show_contacts);
        Button buttonShowFromSP = view.findViewById(R.id.show_from_SP);
        Button buttonExit = view.findViewById(R.id.button_exit);
        Button buttonShowInNotification = view.findViewById(R.id.show_in_notification);

        buttonSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSelectContactFragment(view);
            }
        });

        buttonShowContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShowContactsFragment();
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        buttonShowInNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNotification(view);
            }
        });

        buttonShowFromSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = MainFragment.this.getActivity().getSharedPreferences(NAME_SP, MODE_PRIVATE);
                String savedName = sharedPreferences.getString(KEY_NAME, "");
                String savedEmail = sharedPreferences.getString(KEY_EMAIL, "-");
                String savedPhone = sharedPreferences.getString(KEY_PHONE, "-");

                if (!Objects.equals(savedName, "")) {
                    Snackbar.make(view, savedName + " " + savedEmail + " " + savedPhone + " ", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, R.string.empty_snackbar, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void toSelectContactFragment(View view) {
        int hasReadContactPermission = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_CONTACTS);
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            getParentFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .remove(this)
                    .replace(R.id.container, SelectContactFragment.newInstance())
                    .commit();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
    }

    public void toShowContactsFragment() {
        ContactDao contactDao = appDatabase.contactDao();
        List<Contact> contacts = contactDao.findAll();

        if (contacts.size() != 0) {
            ShowContactsDialogFragment dialog = new ShowContactsDialogFragment();
            dialog.show(getChildFragmentManager(), "custom");
        } else {
            Toast.makeText(getActivity(), R.string.empty_db, Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotification(View view) {
        sharedPreferences = MainFragment.this.getActivity().getSharedPreferences(NAME_SP, MODE_PRIVATE);
        String savedName = sharedPreferences.getString(KEY_NAME, "");
        ContactDao contactDao = appDatabase.contactDao();

        if (contactDao.findByName(savedName) != null) {

            NotificationManager notificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                chanelId = getString(R.string.chanel_id);
                CharSequence name = getString(R.string.chanel_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(chanelId, name, importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                notificationManager.createNotificationChannel(mChannel);
            }

            String phoneName = sharedPreferences.getString(KEY_PHONE, "");
            Contact contact = contactDao.findByPhone(phoneName);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(view.getContext(), chanelId)
                    .setSmallIcon(R.drawable.ic_notifications_active_white_24dp)
                    .setContentTitle(contact.getName())
                    .setContentText(contact.getEmail());

            Intent resultIntent = new Intent(view.getContext(), MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(view.getContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Toast.makeText(getActivity(), R.string.empty_sr, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
