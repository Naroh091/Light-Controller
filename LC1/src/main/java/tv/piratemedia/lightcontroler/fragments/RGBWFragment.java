package tv.piratemedia.lightcontroler.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.larswerkman.holocolorpicker.ColorPicker;

import tv.piratemedia.lightcontroler.MainActivity;
import tv.piratemedia.lightcontroler.R;
import tv.piratemedia.lightcontroler.controlCommands;
import tv.piratemedia.lightcontroler.helpers.SPHelper;

import static tv.piratemedia.lightcontroler.R.id.dplus;

public class RGBWFragment extends Fragment {

    private boolean recreateView = false;
    private View cacheView = null;
    private boolean disabled = true;
    private controlCommands lightService;

    private int zone;

    // Views
    private Switch generalOnOff;
    private ToggleButton toggleWhite;
    private ColorPicker color;
    private SeekBar brightness;
    private Switch candleMode;

    // Modes
    private boolean micStarted = false;
//    private boolean candleMode = false;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RGBWFragment newInstance(int sectionNumber, controlCommands service) {
        RGBWFragment fragment = new RGBWFragment();
        Bundle args = new Bundle();
        args.putInt("ZONE", sectionNumber);
        args.putSerializable("CONTROLLER", service);
        fragment.setArguments(args);
        return fragment;
    }

    public RGBWFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!recreateView) {

            // Get from bundle
            lightService = (controlCommands) getArguments().getSerializable("CONTROLLER");
            zone = getArguments().getInt("ZONE");

            final View rootView = inflater.inflate(R.layout.new_rgbw_control, container, false);

            // Link views

            generalOnOff = (Switch) rootView.findViewById(R.id.general_switch);
            toggleWhite = (ToggleButton) rootView.findViewById(R.id.white_toggle);
            color = (ColorPicker) rootView.findViewById(R.id.color);
            brightness = (SeekBar) rootView.findViewById(R.id.brightness);
            candleMode = (Switch) rootView.findViewById(R.id.candle_switch);

            generalOnOff.setChecked(SPHelper.getOnState(getActivity(), zone));
            brightness.setProgress(SPHelper.getBrightness(getActivity(), zone));

            setColors();

            Button disco = (Button) rootView.findViewById(dplus);
//            Button dplus = (Button) rootView.findViewById(dplus);
            Button dminus = (Button) rootView.findViewById(R.id.dminus);
            Button white = (Button) rootView.findViewById(R.id.white);
            Button night = (Button) rootView.findViewById(R.id.night);
            SeekBar tolerance = (SeekBar) rootView.findViewById(R.id.mictolerance);
            final Button toggleMic = (Button) rootView.findViewById(R.id.mic);
            Spinner modeSpinner = (Spinner) rootView.findViewById(R.id.movement_modes);
            final LinearLayout modeContainer = (LinearLayout) rootView.findViewById(R.id.modes_container);

            generalOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        turnOnLights(zone);
                    } else {
                        turnOffLights(zone);
                    }
                }
            });

            toggleWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        turnOnLights(zone);
                        lightService.turnOnWhite(zone);
                        ((MainActivity) getActivity()).setActionbarColor(Color.parseColor("#ffffff"));
                    } else {
                        lightService.setColor(zone, SPHelper.getColor(getActivity(), zone));
                        ((MainActivity) getActivity()).setActionbarColor(SPHelper.getColor(getActivity(), zone));
                    }
                }
            });

            color.setShowOldCenterColor(false);

            color.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                @Override
                public void onColorChanged(int i) {
                    if (!disabled) {
                        turnOnLights(zone);
                        System.out.println();
                        lightService.setColor(zone, i);
                        ((MainActivity) getActivity()).setActionbarColor(i);
                        SPHelper.putColor(getActivity(), zone, i);
                    }
                }
            });

            brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    turnOnLights(zone);
                    lightService.setBrightness(zone, progress);
                    SPHelper.putBrightness(getActivity(), zone, progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            candleMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        turnOnLights(zone);
                        lightService.startCandleMode(zone);
                    }else{
                        lightService.stopCandleMode();
                    }
                }
            });

//
//            if (micStarted) {
//                toggleMic.setText(R.string.mic_stop_listening);
//            } else {
//                toggleMic.setText(R.string.mic_start_listening);
//            }
//
//            if (candleMode) {
//                toggleCandle.setText(R.string.candle_mode_stop);
//            } else {
//                toggleCandle.setText(R.string.candle_mode_start);
//            }
//
//            modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    try {
//                        for (int i = 0; i < modeContainer.getChildCount(); i++) {
//                            modeContainer.getChildAt(i).setVisibility(View.GONE);
//                        }
//                        modeContainer.getChildAt(position).setVisibility(View.VISIBLE);
//                    } catch(NullPointerException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    lightService.setBrightness(getArguments().getInt(ARG_SECTION_NUMBER), progress);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                    lightService.touching = true;
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    lightService.touching = false;
//                }
//            });
//


//
//
//
//            disco.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    lightService.toggleDiscoMode(getArguments().getInt(ARG_SECTION_NUMBER));
//                        /*ToggleButton io = (ToggleButton) rootView.findViewById(R.id.generalOnOff);
//                        io.setChecked(true);*/
//                }
//            });
//
//            dplus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    lightService.discoModeFaster();
//                        /*ToggleButton io = (ToggleButton) rootView.findViewById(R.id.generalOnOff);
//                        io.setChecked(true);*/
//                }
//            });
//
//            dminus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    lightService.discoModeSlower();
//                        /*ToggleButton io = (ToggleButton) rootView.findViewById(R.id.generalOnOff);
//                        io.setChecked(true);*/
//                }
//            });
//
//
//
//            night.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    lightService.setColorToNight(getArguments().getInt(ARG_SECTION_NUMBER));
//                }
//            });
//
//            tolerance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    lightService.tolerance[0] = progress;
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//
//            toggleMic.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!micStarted) {
//                        toggleMic.setText("Stop Listening");
//                        lightService.startMeasuringVol(getArguments().getInt(ARG_SECTION_NUMBER));
//                        micStarted = true;
//                    } else {
//                        toggleMic.setText("Start Listening");
//                        lightService.stopMeasuringVol();
//                        micStarted = false;
//                    }
//                }
//            });
//


            recreateView = true;
            cacheView = rootView;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disabled = false;
                }
            }, 500);
            return rootView;
        } else {
            disabled = true;
            try {
                ((ViewGroup) cacheView.getParent()).removeView(cacheView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disabled = false;
                }
            }, 500);
            return cacheView;
        }
    }

    private void turnOnLights(int zone){
        lightService.lightsOn(zone);
        SPHelper.putOnState(getActivity(), zone, true);
        generalOnOff.setChecked(true);
        SPHelper.putOnState(getActivity(), zone, true);
        setColors();
    }

    private void turnOffLights(int zone){
        lightService.lightsOff(zone);
        SPHelper.putOnState(getActivity(), zone, false);
        ((MainActivity) getActivity()).setActionbarColor(Color.DKGRAY);

        // Turn off modes
        if(candleMode.isChecked()) {
            lightService.stopCandleMode();
            candleMode.setChecked(false);
        }
    }

    private void setColors(){
        int oldColor = SPHelper.getColor(getActivity(), zone);
        if(oldColor == 0) {
            oldColor = Color.DKGRAY;
            SPHelper.putColor(getActivity(), zone, Color.DKGRAY);
        }
        color.setOldCenterColor(oldColor);
        ((MainActivity) getActivity()).setActionbarColor(oldColor);
    };

}