package com.baofu.gsydemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baofu.gsy.controller.GsyNormalController;

import java.util.List;

public class SlideVideoAdapter extends RecyclerView.Adapter<SlideVideoAdapter.VideoViewHolder> {

    private List<VideoItem> videoItems;
    private Context context;

    public SlideVideoAdapter(Context context, List<VideoItem> videoItems) {
        this.context = context;
        this.videoItems = videoItems;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bindData(videoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        GsyNormalController player;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            player = itemView.findViewById(R.id.video_player);
        }

        void bindData(VideoItem item) {
            player.setUp(item.getUrl(), true, item.getTitle());

            player.getFullscreenButton().setOnClickListener(v -> {
                player.orientationUtils.resolveByClick();
                player.startWindowFullscreen(context, false, true);
            });
        }
    }
}