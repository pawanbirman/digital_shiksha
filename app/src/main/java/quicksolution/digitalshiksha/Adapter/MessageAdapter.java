package quicksolution.digitalshiksha.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import quicksolution.digitalshiksha.Model.Messages;
import quicksolution.digitalshiksha.R;
import quicksolution.digitalshiksha.Student.ChatActivity;
import quicksolution.digitalshiksha.Student.ChatFragment;
import quicksolution.digitalshiksha.Student.ChatWithSchoolStaffActivity;
import quicksolution.digitalshiksha.Student.ImageViewerActivity;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    BitmapDrawable drawable;
    Bitmap bitmap;


    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;

    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int position)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Student").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);


        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {


                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);


                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage());
            }
            else
            {

                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage());

            }
        }
        else if (fromMessageType.equals("images"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

            }
            else
                {

                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                }
        }

        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx") )
            {
                if(fromUserID.equals(messageSenderId))
                {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load("https://firebasestorage.googleapis.com/v0/b/digitalshiksha-f910b.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=b6c6f98b-3868-47d6-ae16-b59531495a6f")
                            .into(messageViewHolder.messageSenderPicture);

                }
                else
                    {
                        messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                        messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load("https://firebasestorage.googleapis.com/v0/b/digitalshiksha-f910b.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=b6c6f98b-3868-47d6-ae16-b59531495a6f")
                                .into(messageViewHolder.messageReceiverPicture);
                    }
            }

            if(fromUserID.equals(messageSenderId))
            {
                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if(userMessagesList.get(position).getType().equals("pdf") ||userMessagesList.get(position).getType().equals("docx") )
                        {
                            CharSequence options[]=new CharSequence[]
                                    {
                                            "Delete For me",
                                            "Download and View This Document",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };

                            AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete Message ?");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    if(which==0)
                                    {
                                        deleteSentMessage(position,messageViewHolder);
                                    }
                                    else  if(which==1)
                                    {

                                        DownloadManager downloadManager=(DownloadManager)  messageViewHolder.itemView.getContext().getSystemService( messageViewHolder.itemView.getContext().DOWNLOAD_SERVICE);
                                        Uri uri=Uri.parse(userMessagesList.get(position).getMessage());
                                        DownloadManager.Request request=new DownloadManager.Request(uri);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                        request.setDestinationInExternalFilesDir(messageViewHolder.itemView.getContext(),"Downloads","new");
                                        downloadManager.enqueue(request);



                                    }
                                    else if(which==3)
                                    {
                                        deleteMessageForEveryOne(position,messageViewHolder);
                                    }
                                }
                            });
                            builder.show();
                        }

                        else if(userMessagesList.get(position).getType().equals("text"))
                        {
                            CharSequence options[]=new CharSequence[]
                                    {
                                            "Delete For me",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };

                            AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete Message ?");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    if(which==0)
                                    {
                                        deleteSentMessage(position,messageViewHolder);
                                    }
                                    else if(which==2)
                                    {

                                        deleteMessageForEveryOne(position,messageViewHolder);

                                    }
                                }
                            });
                            builder.show();
                        }

                        else if(userMessagesList.get(position).getType().equals("images") )
                        {
                            CharSequence options[]=new CharSequence[]
                                    {
                                            "Delete For me",
                                            "View This Image",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };

                            AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                            builder.setTitle("Delete Message ?");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    if(which==0)
                                    {
                                        deleteSentMessage(position,messageViewHolder);
                                    }
                                    else  if(which==1)
                                    {
                                        Intent intent=new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                        intent.putExtra("url",userMessagesList.get(position).getMessage());
                                        messageViewHolder.itemView.getContext().startActivity(intent);

                                    }
                                    else if(which==3)
                                    {
                                        deleteMessageForEveryOne(position,messageViewHolder);
                                    }

                                }
                            });
                            builder.show();
                        }

                    }
                });
            }


            else
                {

                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if(userMessagesList.get(position).getType().equals("pdf") ||userMessagesList.get(position).getType().equals("docx") )
                            {
                                CharSequence options[]=new CharSequence[]
                                        {
                                                "Delete For me",
                                                "Download and View This Document",
                                                "Cancel"
                                        };

                                AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                                builder.setTitle("Delete Message ?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        if(which==0)
                                        {
                                            deleteReceiveMessage(position,messageViewHolder);
                                        }
                                        else  if(which==1)
                                        {

                                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage().toString()));
                                            messageViewHolder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                            }

                            else if(userMessagesList.get(position).getType().equals("text"))
                            {
                                CharSequence options[]=new CharSequence[]
                                        {
                                                "Delete For me",
                                                "Cancel"
                                        };

                                AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                                builder.setTitle("Delete Message ?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        if(which==0)
                                        {
                                            deleteReceiveMessage(position,messageViewHolder);
                                        }
                                    }
                                });
                                builder.show();
                            }

                            else if(userMessagesList.get(position).getType().equals("images") )
                            {
                                CharSequence options[]=new CharSequence[]
                                        {
                                                "Delete For me",
                                                "View This Image",
                                                "Cancel"
                                        };

                                AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                                builder.setTitle("Delete Message ?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                        if(which==0)
                                        {
                                            deleteReceiveMessage(position,messageViewHolder);

                                        }
                                        else  if(which==1)
                                        {

                                            Intent intent=new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                            intent.putExtra("url",userMessagesList.get(position).getMessage());
                                            messageViewHolder.itemView.getContext().startActivity(intent);


                                        }
                                    }
                                });
                                builder.show();
                            }

                        }
                    });

                }
    }

    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }


    private void deleteSentMessage(final int position,final MessageViewHolder holder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
              if(task.isSuccessful())
              {
                  Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();

              }
              else
                  {
                      Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                  }

            }
        });

    }


    private void deleteReceiveMessage(final int position,final MessageViewHolder holder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void deleteMessageForEveryOne(final int position,final MessageViewHolder holder)
    {
        final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Messages")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(holder.itemView.getContext(), "Deleted ", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}

