package drawing.training.javi.drawingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class DrawingFragment extends Fragment {

    private DrawingView drawView;

    private int mStrokeSize;

    public DrawingFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);
        Bundle args = getArguments();
        int paint = args.getInt(Constants.ARGS_PAINT, 0xFF660000);
        int sizeWidth = args.getInt(Constants.ARGS_SCREEN_WIDTH,Constants.WIDTH);
        int sizeHeight = args.getInt(Constants.ARGS_SCREEN_HEIGHT, Constants.HEIGHT);
        //int paint = 0xFF660000;

        drawView = (DrawingView)rootView.findViewById(R.id.drawing);
        drawView.setSize(sizeWidth,sizeHeight);
        drawView.setCallback(getActivity());
        drawView.setPaint(paint);

        mStrokeSize = Constants.STROKE_SIZE_MEDIUM;

        final ImageButton mPaintButton = (ImageButton) rootView.findViewById(R.id.btnPaint);
        mPaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setEraseMode(false);
                drawView.setPaintMode(true);
            }
        });

        final ImageButton mHandButton = (ImageButton) rootView.findViewById(R.id.btnHand);
        mHandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintMode(false);
            }
        });

        final ImageButton mEraserButton = (ImageButton) rootView.findViewById(R.id.btnEraser);
        //mEraserButton.setImageDrawable(getResources().getDrawable(R.drawable.icons_status));
        mEraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintMode(true);
                drawView.setEraseMode(true);
            }
        });

        final ImageButton mStrokeButton = (ImageButton) rootView.findViewById(R.id.btnStroke);
        //mEraserButton.setImageDrawable(getResources().getDrawable(R.drawable.icons_status));
        mStrokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStrokeSize) {
                    case Constants.STROKE_SIZE_BIG:
                        mStrokeSize = Constants.STROKE_SIZE_MEDIUM;
                        mStrokeButton.setImageResource(R.mipmap.ic_stroke2);
                        break;
                    case Constants.STROKE_SIZE_MEDIUM:
                        mStrokeSize = Constants.STROKE_SIZE_SMALL;
                        mStrokeButton.setImageResource(R.mipmap.ic_stroke3);
                        break;
                    case Constants.STROKE_SIZE_SMALL:
                        mStrokeSize = Constants.STROKE_SIZE_BIG;
                        mStrokeButton.setImageResource(R.mipmap.ic_stroke1);
                        break;
                    default: break;
                }
                drawView.setStrokeSize(mStrokeSize);
            }
        });

        return rootView;
    }

}