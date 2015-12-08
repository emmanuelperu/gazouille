package fr.manumehdi.gazouille.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.model.Gazouillis;
import fr.manumehdi.gazouille.utils.StringsUtils;
import fr.manumehdi.gazouille.views.span.HashTagSpan;

/**
 * Created by mehdi on 14/11/2015.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.RecyclerAdapterHolder> {

    private List<Gazouillis> gazouillis;

    public TweetAdapter(List<Gazouillis> gazouillis) {
        super();
        this.gazouillis = gazouillis;
    }

    private HashTagSpan.OnHashTagClickListener hashTagClickListener;

    public void setHashTagClickListener(HashTagSpan.OnHashTagClickListener hashTagClickListener) {
        this.hashTagClickListener = hashTagClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterHolder holder, int position) {
        // FIXMe vérifier la nullité du premier element
        if (this.gazouillis != null && position < this.gazouillis.size() && this.gazouillis.get(position) != null)
            holder.reloadData(this.gazouillis.get(position));
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        // FIXMe vérifier la nullité du premier element
        holder.reloadData(this.gazouillis.get(0));
    }

    @Override
    public int getItemCount() {
        return this.gazouillis != null ? this.gazouillis.size() : 0;
    }

    @Override
    public RecyclerAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return
                new RecyclerAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false));
    }

    public class RecyclerAdapterHolder extends RecyclerView.ViewHolder {

        private ImageView userImageView;
        private TextView userNameTextView;
        private TextView tweetMessageTextView;


        public RecyclerAdapterHolder(View itemView) {
            super(itemView);
            userImageView = (ImageView) itemView.findViewById(R.id.tweet_user_img);
            userNameTextView = (TextView) itemView.findViewById(R.id.tweet_user_name);
            tweetMessageTextView = (TextView) itemView.findViewById(R.id.tweet_message);
        }


        public void reloadData(Gazouillis gazouillis) {
            this.userNameTextView.setText(gazouillis.getMessage().getUser().getName());

            final String tweetMessage = gazouillis.getMessage().getContent();
            final SpannableString tweetMessageSpannable = new SpannableString(tweetMessage);

            final List<Pair<Integer, Integer>> hashTagsForSpanPosition = StringsUtils.getHashTagsForSpanPosition(tweetMessage);

            if (hashTagsForSpanPosition != null && hashTagsForSpanPosition.size() > 0) {
                while (hashTagsForSpanPosition.iterator().hasNext()) {
                    final Pair<Integer, Integer> it = hashTagsForSpanPosition.iterator().next();

                    HashTagSpan hashTagSpan = new HashTagSpan(itemView.getContext());
                    if (hashTagClickListener != null) {
                        hashTagSpan.setOnHashTagClickListener(hashTagClickListener);
                    } else {
                        hashTagSpan.setOnHashTagClickListener(new HashTagSpan.OnHashTagClickListener() {
                            @Override
                            public void onClick(String hashTag) {
                                Toast.makeText(itemView.getContext(), "click on gazouilli", Toast.LENGTH_LONG);
                            }
                        });
                    }
                    tweetMessageSpannable.setSpan(hashTagSpan, it.first, it.second, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }

            tweetMessageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            tweetMessageTextView.setText(tweetMessageSpannable);
        }

    }

}