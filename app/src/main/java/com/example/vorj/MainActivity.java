package com.example.vorj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vorj.Adapters.AllUsersAdapter;
import com.example.vorj.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientBuilder;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    SinchClient sinchClient;
    Call call;
    ArrayList<User> userArrayList;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userArrayList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();

        SinchClientBuilder sinchClientBuilder = Sinch.getSinchClientBuilder();
        sinchClientBuilder.context(this);
        sinchClientBuilder.userId(firebaseUser.getUid());
        sinchClientBuilder.applicationKey("c76d479e-219a-4a9b-8b34-87739505cb05");
        sinchClientBuilder.applicationSecret("ymyEQxrcW0+VzBb/b9q4OA==");
        sinchClientBuilder.environmentHost("clientapi.sinch.com");
        sinchClient= sinchClientBuilder.build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        sinchClient.start();
        fetchAllUsers();

    }

    private void fetchAllUsers()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot dss:dataSnapshot.getChildren())
                {
                    User user=dss.getValue(User.class);
                    userArrayList.add(user);
                }

                AllUsersAdapter adapter=new AllUsersAdapter(MainActivity.this,userArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"error"+databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_logout)
        {
            if(firebaseUser!=null)
            {
                auth.signOut();
                finish();
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private class SinchCallClientListener implements CallListener, CallClientListener {

        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(getApplicationContext(),"Ringing....",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(getApplicationContext(),"Call Established",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedCall) {
            Toast.makeText(getApplicationContext(),"Call Ended",Toast.LENGTH_LONG).show();
            call = null;
            endedCall.hangup();

        }

        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> list) {

        }

        @Override
        public void onIncomingCall(CallClient callClient, final com.sinch.android.rtc.calling.Call incomingcall)
        {
            final AlertDialog alertDialog= new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Calling");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                    call.hangup();

                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Pick",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog,int i)
                {
                    call=incomingcall;
                    call.answer();
                    call.addCallListener(new SinchCallClientListener());
                    Toast.makeText(getApplicationContext(),"Call Is Started",Toast.LENGTH_LONG).show();

                }
            });

            alertDialog.show();
        }





    }


    public void callUser(User user)
    {
        if(call==null)
        {
            call=sinchClient.getCallClient().callUser(user.getUserid());
            call.addCallListener(new SinchCallClientListener());

            openCallerDialog(call);
        }

    }

    private void openCallerDialog(final Call call)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("ALERT");
        alertDialog.setMessage("CALLING");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hang Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                call.hangup();

            }
        });
        alertDialog.show();
    }
    /*
    private class SinchCallClientListener implements CallClientListener
    {
        @Override
        public void onIncomingCall(CallClient callClient, final Call incomingcall)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("CALLING");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Reject",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog,int which){
                    dialog.dismiss();
                    call.hangup();
                }

            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Pick",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog,int which)
                {
                    call=incomingcall;
                    call.answer();
                    call.addCallListener(new SinchCallClientListener());
                    Toast.makeText(getApplicationContext(),"Call Is Started",Toast.LENGTH_LONG).show();

                }
            });

            alertDialog.show();
        }

    }
*/
}
