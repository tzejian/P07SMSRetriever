package com.example.a127107.p07smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {
    Button btnAddText,btnSend;
    TextView tvFrag2;
    EditText etNum2;
    Intent i = new Intent();


    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second,container,false);

        tvFrag2 = (TextView)view.findViewById(R.id.tvFrag2) ;
        btnAddText = (Button)view.findViewById(R.id.btnAddTextFrag2);
        etNum2 = (EditText)view.findViewById(R.id.etWord);
        btnSend = (Button)view.findViewById(R.id.btnSend);

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();
                // Fetch SMS Message from Built-in Content

                //filter string
                String filter="body LIKE ? AND body LIKE ?";
                // The matches for the ?
                String[] filterArgs = {"%"+etNum2 +"%"};

                // Fetch SMS Message from Built-in Content Provider

                Cursor cursor = cr.query(uri, reqCols,filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }

                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                        i.putExtra("sms",smsBody);
                    } while (cursor.moveToNext());
                }
                tvFrag2.setText(smsBody);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent email = new Intent(Intent.ACTION_SEND);

                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"jason_lim@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "SMS");
                email.putExtra(Intent.EXTRA_TEXT,
                        tvFrag2.getText());

                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));
            }
        });






        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnAddText.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}

