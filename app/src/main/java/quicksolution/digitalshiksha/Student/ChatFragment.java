package quicksolution.digitalshiksha.Student;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import quicksolution.digitalshiksha.Model.Contacts;
import quicksolution.digitalshiksha.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private View ContactsView;
    private RecyclerView myContactsList;

    private DatabaseReference ContactsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID,saveCurrentTime, saveCurrentDate;
    public static final String DEFAULT="NA";

    private ChatFragment.OnFragmentInteractionListener mListener;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactsView= inflater.inflate(R.layout.fragment_chat, container, false);

        myContactsList = (RecyclerView) ContactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));




        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("loginDetails",MODE_PRIVATE);
        currentUserID=sharedPreferences.getString("Id",DEFAULT);


        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Student");

        return ContactsView;

    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef, Contacts.class)
                        .build();


        final FirebaseRecyclerAdapter<Contacts, ChatFragment.ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ChatFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatFragment.ContactsViewHolder holder, int position, @NonNull Contacts model)
            {
                final String userIDs = getRef(position).getKey();
                final String[] retImage = {"default_image"};

                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.exists())
                        {

                            if (dataSnapshot.hasChild("image"))
                            {
                                retImage[0] = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retImage[0]).into(holder.profileImage);
                            }

                            final String profileName = dataSnapshot.child("name").getValue().toString();
                            String profileStatus = dataSnapshot.child("status").getValue().toString();
                            holder.userName.setText(profileName);
                            if(TextUtils.isEmpty(profileStatus))
                            {
                                holder.userStatus.setText("Hii, I am using Digital Shiksha");

                            }
                            else
                            {
                                holder.userStatus.setText(profileStatus);
                            }



                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", userIDs);
                                    chatIntent.putExtra("visit_user_name", profileName);
                                    chatIntent.putExtra("visit_image", retImage[0]);
                                    startActivity(chatIntent);
                                }
                            });

                            holder.onlineIcon.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent chatIntent = new Intent(getContext(), CallingActivity.class);
                                    chatIntent.putExtra("visit_user_id", userIDs);
                                    chatIntent.putExtra("visit_user_name", profileName);
                                    chatIntent.putExtra("visit_image", retImage[0]);
                                    startActivity(chatIntent);


                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatFragment.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout_for_private_chat, viewGroup, false);
                ChatFragment.ContactsViewHolder viewHolder = new ChatFragment.ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();



    }





    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;


        public ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status);
        }
    }







    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragment.OnFragmentInteractionListener) {
            mListener = (ChatFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
