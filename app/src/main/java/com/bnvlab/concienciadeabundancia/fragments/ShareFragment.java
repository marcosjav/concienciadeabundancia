package com.bnvlab.concienciadeabundancia.fragments;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.auxiliaries.Notify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marcos on 15/04/2017.
 */

public class ShareFragment extends Fragment {
    ListView listView;
    ArrayList<String> list;
    ArrayList<String> listPhone;
    ArrayAdapter<String> adapter;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share,container,false);

        listView = (ListView) view.findViewById(R.id.list_view_contacts_share);
        editText = (EditText) view.findViewById(R.id.edit_text_filter_share);

        list = new ArrayList<>();
        listPhone = new ArrayList<>();

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        getPhone(getContext());
        Collections.sort(list);
        adapter.notifyDataSetChanged();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Notify.shareTo(listPhone.get(position),"Probalo!", getContext());
//                Notify.enviarSMS(listPhone.get(position),"Hola!\nEste es un mensaje automático de prueba.\nMuchas gracias por su paciencia!",getContext());

                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    String contact = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();

                    String[] data = contact.split("\n");
                    final String phone = data[1];
                    final String name = data[0];

                    builder
                            .setMessage("Se va a enviar un sms a:\n" + name + "\n" + phone)
                            .setTitle("Invitar por SMS")
                            .setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Dialog d = (Dialog) dialog;

//                                    SmsManager.getDefault().sendTextMessage(phone,null,"Sent from Android",null,null);
                                    Notify.enviarSMS(phone,"Hola " + name,getContext());

                                    dialog.dismiss();
                                }
                            });

                    Dialog dialog = builder.create();

                    dialog.show();


                }catch (Exception e)
                {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }


    public List<ContactDTO> getPhone(Context context) {
        List<ContactDTO> contactList = new ArrayList<ContactDTO>();
        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[] {
                ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String filter = ""+ ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.CommonDataKinds.Phone.TYPE +"=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        String filter = ""+ ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " IS ? AND "+ ContactsContract.RawContacts.DATA_SET+" IS NULL";
        String order = ContactsContract.Contacts.DISPLAY_NAME + " ASC";// LIMIT " + limit + " offset " + lastId + "";
        String[] selection = new String[]{"com.whatsapp",};

        Cursor phoneCur = cr.query(uri, PROJECTION, filter, selection, order);
        while(phoneCur.moveToNext()) {
            ContactDTO dto = new ContactDTO();
            dto.setName("" + phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            dto.setMobileNo("" + phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            dto.setPhotoUrl("" + phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
            dto.setContactId("" + phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID)));

            contactList.add(dto);
            list.add(dto.getName() + "\n" + dto.getMobileNo());
//            String phoneFormatted = dto.getMobileNo().replace("+","").replace(" ","").replace("-","");
//            if (!phoneFormatted.startsWith("549"))
//                phoneFormatted = "549" + phoneFormatted;
//            listPhone.add(dto.getMobileNo());
        }
        phoneCur.close();

        return contactList;
    }

    class ContactDTO{
        String name, mobileNo, photoUrl, contactId;

        public ContactDTO() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getContactId() {
            return contactId;
        }

        public void setContactId(String contactId) {
            this.contactId = contactId;
        }
    }
}
