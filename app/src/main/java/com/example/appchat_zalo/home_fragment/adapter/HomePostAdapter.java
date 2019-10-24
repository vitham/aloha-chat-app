package com.example.appchat_zalo.home_fragment.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat_zalo.R;
import com.example.appchat_zalo.home_fragment.listner.OnclickHomeFragmentItemListener;
import com.example.appchat_zalo.model.Posts;
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

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.HomePostViewHolder> {

    private List<Posts> mPostList = new ArrayList<>();

    private OnclickHomeFragmentItemListener listener;
    private DatabaseReference mLikeRef, mPostRef;

    private boolean mCheckLike = false;

    public HomePostAdapter(OnclickHomeFragmentItemListener listener) {
        this.listener = listener;
    }

    public void setmPostList(List<Posts> mPostList) {
        this.mPostList = mPostList;
        notifyDataSetChanged();
    }

    public void adddPost(Posts posts) {
        mPostList.add(posts);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public HomePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_item, parent, false);
        return new HomePostViewHolder(view);
    }

    private void initFirebase() {
        mPostRef = FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS);
        mLikeRef = FirebaseDatabase.getInstance().getReference(Constants.TABLE_LIKE);

    }

    @Override
    public void onBindViewHolder(@NonNull HomePostViewHolder holder, int position) {
        holder.bindata(mPostList.get(position));


    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class HomePostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_avatar)
        ImageView mAvatar;

        @BindView(R.id.text_name)
        TextView mName;

        @BindView(R.id.text)
        TextView mTextPost;

        @BindView(R.id.text_time_posts)
        TextView mTimePost;

        @BindView(R.id.text_date_posts)
        TextView mDatePost;

        @BindView(R.id.text_content_post)
        TextView mContentPost;

        @BindView(R.id.image_picture_posts)
        ImageView mPicturePost;

        @BindView(R.id.image_like)
        ImageView mImageLike;

        @BindView(R.id.image_comment)
        ImageView mImageComment;

        @BindView(R.id.text_number_like)
        TextView mTextNumberLike;



        public HomePostViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindata(Posts posts) {
            Glide.with(itemView)
                    .load(posts.getAvatar())
                    .circleCrop()
                    .into(mAvatar);

            mName.setText(posts.getName());
            mContentPost.setText(posts.getContent_posts());
            mTimePost.setText(posts.getTime());
            mDatePost.setText(posts.getDate());
//            mTextNumberLike.setText(posts.getLike());
            Glide.with(itemView)
                    .load(posts.getPicture())
                    .into(mPicturePost);

            final String postKey = posts.getIdPost();
            initFirebase();
            checkLike(postKey);
            numberLike(postKey);
            mImageLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageLike.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference(Constants.TABLE_LIKE).child(postKey).child(Constants.UID).setValue("true");
                    } else {
                        FirebaseDatabase.getInstance().getReference(Constants.TABLE_LIKE).child(postKey).child(Constants.UID).removeValue();

                    }
                }
            });

        }

        private void numberLike(String postKey) {
            mLikeRef.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mTextNumberLike.setText(dataSnapshot.getChildrenCount() + " " + "like");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void checkLike(String postKey) {

            mLikeRef.child(postKey).addValueEventListener(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Constants.UID).exists()) {

                        mImageLike.setImageResource(R.drawable.icliked);
                        mImageLike.setTag("liked");
                        mTextNumberLike.setTextColor(R.color.red);
                    } else {
                        mImageLike.setImageResource(R.drawable.icon_like);
                        mImageLike.setTag("like");
                        mTextNumberLike.setTextColor(R.color.pinkLight);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        @OnClick(R.id.image_comment)
        void onclickComment() {
            listener.onClickHomeFragmentItem(mPostList.get(getAdapterPosition()));
        }

//        @OnClick(R.id.image_like)
//        void onclickLike(){
//            int likes = Integer.parseInt(mPostList.get(getLayoutPosition()).getLike());
//
//            mCheckLike =  true;
//
//            //get id of user click like
//
//            String postIdlike = mPostList.get(getLayoutPosition()).getIdPost();
//            mLikeRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    // dda like,
//                    if(dataSnapshot.child(postIdlike).hasChild(Constants.UID)){
//                        mPostRef.child(postIdlike).child("like").setValue("" + (likes -1) );
//                        mLikeRef.child(postIdlike).child(Constants.UID).removeValue();
//                        mCheckLike  = false;
//                    }
//
//                    else {
//
//                        // chua  like -->  like
//
//                         mPostRef.child(postIdlike).child("like").setValue("" + (likes + 1));
//                         mLikeRef.child(postIdlike).child(Constants.UID).setValue("liked");
//                         mCheckLike = false;
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//        }

    }


}
