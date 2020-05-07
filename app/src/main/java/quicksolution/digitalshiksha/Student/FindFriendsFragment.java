package quicksolution.digitalshiksha.Student;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import quicksolution.digitalshiksha.Model.Contacts;
import quicksolution.digitalshiksha.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragment extends Fragment {

    private View FindFriendView;
    private RecyclerView findFriendsList;
    private DatabaseReference UsersRef;
    private EditText searchFriends;


    public FindFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FindFriendView= inflater.inflate(R.layout.fragment_find_friends, container, false);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Student");

        findFriendsList = (RecyclerView) FindFriendView.findViewById(R.id.find_friends_recycler_list);
        findFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        searchFriends=(EditText) FindFriendView.findViewById(R.id.search_friends_fragment);

        searchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {


            }

            @Override
            public void afterTextChanged(Editable s)
            {


            }
        });




        return FindFriendView;

    }



    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsActivity.FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendsActivity.FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsActivity.FindFriendViewHolder holder, final int position, @NonNull Contacts model)
                    {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.profileImage);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }




                    @NonNull
                    @Override
                    public FindFriendsActivity.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindFriendsActivity.FindFriendViewHolder viewHolder = new FindFriendsActivity.FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        findFriendsList.setAdapter(adapter);

        adapter.startListening();
    }



    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;



        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }


}
