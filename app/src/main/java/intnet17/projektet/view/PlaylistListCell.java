package intnet17.projektet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import intnet17.projektet.R;


/**
 * Created by Emelie on 2017-02-24.
 */

public class PlaylistListCell extends RelativeLayout{

    private LayoutInflater inflater;

    public PlaylistListCell(Context context){
        super(context);
        init(context);
    }
    public PlaylistListCell(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init(context);
    }
    public PlaylistListCell(Context context, AttributeSet attributeSet, int defStyleAttribute){
        super(context, attributeSet, defStyleAttribute);
        init(context);
    }

    public void init(Context context){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.playlist_cell, null);
    }


}
