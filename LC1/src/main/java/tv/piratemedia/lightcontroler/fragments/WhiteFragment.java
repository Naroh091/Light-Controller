package tv.piratemedia.lightcontroler.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devadvance.circularseekbar.CircularSeekBar;

import tv.piratemedia.lightcontroler.R;
import tv.piratemedia.lightcontroler.controlCommands;
import tv.piratemedia.lightcontroler.MainActivity;
import tv.piratemedia.lightcontroler.helpers.SPHelper;

public class WhiteFragment extends Fragment {

    private boolean recreateView = false;
    private View cacheView = null;
    private boolean disabled = true;
    private int BrightnessCache = 0;
    private int WarmthCache = 0;
    private boolean brightnessTouching = false;
    private boolean warmthTouching = false;
    private controlCommands lightService;
    private int zone;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WhiteFragment newInstance(int sectionNumber, controlCommands service) {
        WhiteFragment fragment = new WhiteFragment();
        Bundle args = new Bundle();
        args.putInt("ZONE", sectionNumber);
        args.putSerializable("CONTROLLER", service);
        fragment.setArguments(args);
        return fragment;
    }

    public WhiteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(!recreateView) {

            // Get from bundle
            lightService = (controlCommands) getArguments().getSerializable("CONTROLLER");
            zone = getArguments().getInt("ZONE");

            final View rootView = inflater.inflate(R.layout.white_control, container, false);

            final CircularSeekBar brightness = (CircularSeekBar) rootView.findViewById(R.id.brightness);
            CircularSeekBar warmth = (CircularSeekBar) rootView.findViewById(R.id.warmth);
            final TextView brightnessvalue = (TextView) rootView.findViewById(R.id.brightnessvalue);
            final TextView warmthvalue = (TextView) rootView.findViewById(R.id.warmthvalue);

            Button on = (Button) rootView.findViewById(R.id.on);
            Button off = (Button) rootView.findViewById(R.id.off);
            Button full = (Button) rootView.findViewById(R.id.full);
            Button night = (Button) rootView.findViewById(R.id.night);

            //Return State

            brightness.setProgress(SPHelper.getBrightness(getActivity(), zone));
            (((MainActivity) getActivity())).setActionbarColor(SPHelper.getColor(getActivity(), zone));


            brightness.setProgress(10);
            brightness.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                    if(brightnessTouching) {
                        if (progress < 11) {
                            brightnessvalue.setText("" + (progress - 10));
                        } else {
                            brightnessvalue.setText("+" + (progress - 10));
                        }
                        if (progress > BrightnessCache) {
                            for (int i = progress; i > BrightnessCache; i--) {
                                lightService.setBrightnessUpOne();
                            }
                        } else if (progress < BrightnessCache) {
                            for (int i = progress; i < BrightnessCache; i++) {
                                lightService.setBrightnessDownOne();
                            }
                        }

                        BrightnessCache = progress;
                    }
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {
                    brightnessTouching = false;
                    brightnessvalue.setAlpha(0.0f);
                    int brightness = seekBar.getProgress() - 10;
                        /*if(brightness != 0) {
                            seekBar.setProgress(8);
                            Controller.setBrightnessJog(zone, brightness);
                        }*/
                    seekBar.setProgress(10);
                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {
                    brightnessvalue.setAlpha(1.0f);
                    BrightnessCache = 11;
                    lightService.lightsOn(zone);
                    brightnessTouching = true;
                }
            });

            warmth.setProgress(10);
            warmth.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                    if(warmthTouching) {
                        progress = 20 - progress;
                        if (progress < 11) {
                            warmthvalue.setText("" + (progress - 10));
                        } else {
                            warmthvalue.setText("+" + (progress - 10));
                        }
                        if (progress > WarmthCache) {
                            for (int i = progress; i > WarmthCache; i--) {
                                lightService.setWarmthUpOne();
                            }
                        } else if (progress < WarmthCache) {
                            for (int i = progress; i < WarmthCache; i++) {
                                lightService.setWarmthDownOne();
                            }
                        }

                        WarmthCache = progress;
                    }
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {
                    warmthTouching = false;
                    warmthvalue.setAlpha(0.0f);
                    int warmth = seekBar.getProgress() - 10;
                        /*if(warmth != 0) {
                            seekBar.setProgress(8);
                            Controller.setWarmthJog(zone, warmth);
                        }*/
                    seekBar.setProgress(10);
                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {
                    warmthvalue.setAlpha(1.0f);
                    WarmthCache = 11;
                    lightService.lightsOn(zone);
                    warmthTouching = true;
                }
            });

            on.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lightService.lightsOn(zone);
                }
            });
            off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lightService.lightsOff(zone);
                }
            });
                /*io.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Controller.lightsOn(zone);
                        } else {
                            Controller.lightsOff(zone);
                        }
                    }
                });*/

            full.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lightService.setToFull(zone);
                        /*ToggleButton io = (ToggleButton) rootView.findViewById(R.id.onoff);
                        io.setChecked(true);*/
                }
            });

            night.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lightService.setToNight(zone);
                        /*ToggleButton io = (ToggleButton) rootView.findViewById(R.id.onoff);
                        io.setChecked(true);*/
                }
            });

            recreateView = true;
            cacheView = rootView;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    disabled = false;
                }
            }, 500);
            return rootView;
        } else {
            disabled = true;
            try {
                ((ViewGroup) cacheView.getParent()).removeView(cacheView);
            } catch(Exception e) {

            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    disabled = false;
                }
            }, 500);
            return cacheView;
        }
    }
}