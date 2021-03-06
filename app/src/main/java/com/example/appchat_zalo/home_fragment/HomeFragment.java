package com.example.appchat_zalo.home_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat_zalo.R;
import com.example.appchat_zalo.comment.CommentActivity;
import com.example.appchat_zalo.fragment.PostsActivity;
import com.example.appchat_zalo.home_fragment.adapter.HomePostAdapter;
import com.example.appchat_zalo.model.Posts;
import com.example.appchat_zalo.model.Users;
import com.example.appchat_zalo.my_profile.UserRelationshipConfig;
import com.example.appchat_zalo.notification.NotificationActivity;
import com.example.appchat_zalo.search.SearchActivity;
import com.example.appchat_zalo.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {

    private HomePostAdapter mHomePostAdapter = new HomePostAdapter(posts -> {
        Intent intent = new Intent(getContext(), CommentActivity.class);
        intent.putExtra("postId", posts.getIdPost());
        intent.putExtra("userId", posts.getUserId());
        startActivity(intent);

    });

    @BindView(R.id.list_posts_friends)
    RecyclerView mRcvHomePost;

    @BindView(R.id.image_avatar1)
    ImageView mImageAvatar;

    @BindView(R.id.text_posts)
    TextView mTextPost;

    @BindView(R.id.input_search)
    EditText mInputSearch;

    @BindView(R.id.image_notification)
    ImageView mImageNotifications;

    private DatabaseReference mUserRef, mPostRef, mFriendRef, mRef, mLikeRef;
    private String mPostId;

    @OnClick(R.id.image_notification)
    void onclickNotification(){
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        startActivity(intent);
    }
    @OnClick({R.id.input_search})
    void clickSearch() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_posts)
    void onClickPost() {
        Intent intent = new Intent(getContext(), PostsActivity.class);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = this.getArguments();
        if (bundle != null) { //to prevent crash must check against null
            String getData = bundle.getString("postId");
        }

        initFirebase();
        innitRcvPost();
        getListPosts();
//        getMyPost();
        getUser();
        return view;
    }

    private void getMyPost() {
        mPostRef.child(Constants.UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    List<Posts> listMypost = new ArrayList<>();
                    Posts post = data.getValue(Posts.class);
                    Log.d("aa", "onDataChange: list my post ===" + post.toString());
                    listMypost.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser() {
        mUserRef.child(Constants.UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(isAdded()){
                    Users users = dataSnapshot.getValue(Users.class);
                    Glide.with(getContext())
                            .load(users.getAvatar())
                            .circleCrop()
                            .into(mImageAvatar);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initFirebase() {
        mPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_POSTS);
        mUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_USERS);
        mFriendRef = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_FRIEND);
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    private void getListPosts() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Posts> listPost = new ArrayList<>();
                for (DataSnapshot dataMyPost : dataSnapshot.child(Constants.TABLE_POSTS).child(Constants.UID).getChildren()) {
                    Posts post = dataMyPost.getValue(Posts.class);
                    Log.d("aa", "onDataChange: list my post ===" + post.toString());
                    listPost.add(post);
                }

                for (DataSnapshot data : dataSnapshot.child(Constants.TABLE_FRIEND).child(Constants.UID).getChildren()) {
                    Log.d("HomeFragment", "onDataChange: valuee + " + data.getValue());

                    if (data.getValue(String.class).equals(UserRelationshipConfig.FRIEND)) {
                        String idFriend = data.getKey();
                        for (DataSnapshot postData : dataSnapshot.child(Constants.TABLE_POSTS).child(idFriend).getChildren()) {
                            listPost.add(postData.getValue(Posts.class));
                            Log.d("a", "onDataChange: post" + listPost.toString());
                        }
                    }

                }
                mHomePostAdapter.setmPostList(listPost);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void innitRcvPost() {
        mRcvHomePost.setLayoutManager(new LinearLayoutManager(getContext()));
        mRcvHomePost.setHasFixedSize(true);
        mRcvHomePost.setAdapter(mHomePostAdapter);
    }

}
