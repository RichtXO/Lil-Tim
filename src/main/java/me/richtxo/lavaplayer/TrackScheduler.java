package me.richtxo.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private boolean repeat = false;
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    AudioTrack lastTrack;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue (AudioTrack track){
        if (!this.player.startTrack(track, true)){
            this.queue.offer(track);
        }
    }

    public void setRepeating(boolean repeating){
        this.repeat = repeating;
    }

    public boolean isRepeating(){
        return this.repeat;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }


    public void nextTrack(){
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        this.lastTrack = track;
        if (endReason.mayStartNext){
            if (repeat)
                player.startTrack(lastTrack.makeClone(), false);
            else
                nextTrack();
        }
    }
}
