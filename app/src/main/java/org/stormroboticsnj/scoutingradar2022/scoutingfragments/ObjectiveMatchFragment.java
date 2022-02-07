package org.stormroboticsnj.scoutingradar2022.scoutingfragments;


import static org.stormroboticsnj.scoutingradar2022.database.DataProcessor.Action;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.stormroboticsnj.scoutingradar2022.R;
import org.stormroboticsnj.scoutingradar2022.database.DataProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObjectiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObjectiveMatchFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {

    private static final String[] BUTTONS = new String[]{
            "Start",
            "Acquire",
            "Upper Hub",
            "Lower Hub",
            "Miss",
            "Start Climb",
            "End Climb",
            "UNDO",
            "SUBMIT"
    };
    private static final boolean HAS_BUTTONS = true;
    private static final String[] BUTTON_ABBREVIATIONS = new String[]{
            "START",
            "AQ",
            "UH",
            "LH",
            "MS",
            "SC",
            "EC",
            "UNDO",
            "SUBMIT"
    };
    private static final String[] SPINNER_NAMES = new String[]{
            "Climb Position"
    };
    private static final String[] SPINNER_ABBREVIATIONS = new String[]{
            "CP"
    };
    private static final boolean HAS_SPINNERS = false;
    private static final String[][] SPINNER_CONTENTS = new String[][]{
            new String[]{"None", "Low", "Mid", "High", "Traversal"}
    };
    private static final String[][] SPINNER_CONTENTS_ABBREVIATIONS = new String[][]{
            new String[]{"0", "L", "M", "H", "T"}
    };
    private static final int BUTTON_MARGIN = 8;
    private final List<Action> mActionList = new ArrayList<>();
    ButtonInfo[] mButtonInfos;
    SpinnerInfo[] mSpinnerInfos;
    private Context mContext;
    // Chronometer
    private Chronometer mChronometer;
    // ConstraintLayout
    private ConstraintLayout mConstraintLayout;
    // Actions List TextView
    private TextView mActionsListView;
    private TextInputLayout mTeamNumTextInput;
    private TextInputLayout mMatchNumTextInput;
    private int mConstraintLayoutId;

    public ObjectiveMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ObjectiveMatchFragment.
     */

    public static ObjectiveMatchFragment newInstance() {

        return new ObjectiveMatchFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mButtonInfos = new ButtonInfo[BUTTONS.length];


    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateUI();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_objective_match, container, false);

        mConstraintLayout = v.findViewById(R.id.layout_obj_main);
        mConstraintLayoutId = mConstraintLayout.getId();


        mChronometer = v.findViewById(R.id.objective_chronometer);
        mActionsListView = v.findViewById(R.id.objective_text_actions);

        if (!HAS_BUTTONS) {
            mChronometer.setVisibility(View.GONE);
            mActionsListView.setVisibility(View.GONE);
        }

        mTeamNumTextInput = v.findViewById(R.id.objective_text_input_team_number);
        mMatchNumTextInput = v.findViewById(R.id.objective_text_input_match_number);

        return v;
    }

    private void updateActionsListView() {
        // Clear the TextView
        mActionsListView.setText(R.string.action_list_start_prefix);
        // Loop through the actions and add them to the TextView
        for (Action action : mActionList) {
            mActionsListView.append(action.getAbbreviation() + " ");
        }

    }

    private void startMatch() {
        // Start the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        for (ButtonInfo buttonInfo : mButtonInfos) {
            buttonInfo.button.setEnabled(true);
        }
        mButtonInfos[0].button.setEnabled(false);
        // Update the TextView
        updateActionsListView();

        // AppDatabase.getInstance(mContext).exe

    }

    private void endMatch() {
        mButtonInfos[mButtonInfos.length - 1].button.setEnabled(false);
        // Stop the chronometer
        mChronometer.stop();

        // Add the Spinner Info
        if (HAS_SPINNERS) {
            for (SpinnerInfo spinnerInfo : mSpinnerInfos) {
                mActionList.add(new Action(spinnerInfo.abbreviation,
                        spinnerInfo.contents_abbreviations
                                [spinnerInfo.spinner.getSelectedItemPosition()]));
            }
        }

        // Update the TextView
        updateActionsListView();

        StringBuilder sb = new StringBuilder();
        for (Action action : mActionList) {
            sb.append(action.getSubAction().equals("N/A") ?
                      String.valueOf(action.getTimeString()) : action.getSubAction())
              .append(" ")
              .append(action.getAbbreviation())
              .append("\n");
        }

        DataProcessor.processObjectiveMatchData(mContext, mActionList,
                Integer.parseInt(Objects.requireNonNull(mTeamNumTextInput.getEditText(),
                        "NO TEAM NUM EDIT TEXT").getText().toString()),
                Integer.parseInt(Objects.requireNonNull(mMatchNumTextInput.getEditText(),
                        "NO TEAM NUM EDIT TEXT").getText().toString()), true);

        // Make pop-up with result
        new AlertDialog.Builder(mContext).setTitle("Result").setMessage(sb.toString()).show();
    }

    private void undoAction() {
        if (mActionList.size() > 1) {
            mActionList.remove(mActionList.size() - 1);
        }
        updateActionsListView();
    }

    public void generateUI() {
        ConstraintSet constraintSet = new ConstraintSet();

        if (HAS_BUTTONS) {

            // Set up the start button
            mButtonInfos[0] = setupNewButton(0, constraintSet, mChronometer.getId());
            // Enable the start button
            mButtonInfos[0].button.setEnabled(true);

            for (int i = 1; i < mButtonInfos.length - 1; i++) {
                // Set up the user-defined buttons
                mButtonInfos[i] =
                        setupNewButton(i, constraintSet, mButtonInfos[i - 1].id);
            }

        }

        if (HAS_SPINNERS) {
            // Spinners
            mSpinnerInfos = new SpinnerInfo[SPINNER_NAMES.length];

            int lastId = HAS_BUTTONS ? mButtonInfos[mButtonInfos.length - 2].id :
                         mChronometer.getId();

            // Set up first spinner
            mSpinnerInfos[0] = setupNewSpinner(0, constraintSet,
                    lastId);
            // Set up the rest of the spinners
            for (int i = 1; i < SPINNER_NAMES.length; i++) {
                mSpinnerInfos[i] =
                        setupNewSpinner(i, constraintSet, mSpinnerInfos[i - 1].id);
            }
        }

        int lastId;
        if (HAS_SPINNERS) {
            lastId = mSpinnerInfos[mSpinnerInfos.length - 1].id;
        } else if (HAS_BUTTONS) {
            lastId = mButtonInfos[mButtonInfos.length - 2].id;
        } else {
            lastId = mChronometer.getId();
        }

        // Set up the submit button
        mButtonInfos[mButtonInfos.length - 1] =
                setupNewButton(mButtonInfos.length - 1, constraintSet, lastId);

        // Submit button is enabled if there is no start button
        mButtonInfos[mButtonInfos.length - 1].button.setEnabled(!HAS_BUTTONS);
    }

    private ButtonInfo setupNewButton(int index, ConstraintSet constraintSet, int previousId) {
        // Create the button
        Button button = new MaterialButton(mContext);
        // Generate a unique id for the button
        int buttonId = View.generateViewId();
        button.setId(buttonId);
        // Button is disabled by default
        button.setEnabled(false);
        // Set the text of the button
        button.setText(BUTTONS[index]);
        // This fragment is the listener for the button
        button.setOnClickListener(this::onButtonClick);
        // Add the button to the layout
        mConstraintLayout.addView(button);

        // Set the constraints for the button
        constraintSet.clone(mConstraintLayout);
        // Connect the button to the previous button
        chainViewsVertically(constraintSet, previousId, buttonId);
        // Center the button horizontally
        centerViewHorizontally(constraintSet, buttonId);
        // Apply the constraints
        constraintSet.applyTo(mConstraintLayout);

        // Return the button info
        return new ButtonInfo(BUTTONS[index], BUTTON_ABBREVIATIONS[index], buttonId, button);
    }

    private SpinnerInfo setupNewSpinner(int index, ConstraintSet constraintSet, int previousId) {
        // Create the spinner
        Spinner spinner = new Spinner(mContext);
        // Generate a unique id for the spinner
        int spinnerId = View.generateViewId();
        spinner.setId(spinnerId);
        // Create the adapter for the spinner
        spinner.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item, SPINNER_CONTENTS[index]));
        // This fragment is the listener for the spinner
        spinner.setOnItemSelectedListener(this);
        // Add the spinner to the layout
        mConstraintLayout.addView(spinner);

        // Set the constraints for the spinner
        constraintSet.clone(mConstraintLayout);
        // Connect the spinner to the previous spinner
        chainViewsVertically(constraintSet, previousId, spinnerId);
        // Center the spinner horizontally
        centerViewHorizontally(constraintSet, spinnerId);
        // Apply the constraints
        constraintSet.applyTo(mConstraintLayout);

        // Return the spinner info
        return new SpinnerInfo(SPINNER_NAMES[index], SPINNER_ABBREVIATIONS[index],
                SPINNER_CONTENTS[index], SPINNER_CONTENTS_ABBREVIATIONS[index], spinnerId, spinner);

    }

    private void centerViewHorizontally(ConstraintSet constraintSet, int viewId) {
        constraintSet.connect(viewId, ConstraintSet.LEFT, mConstraintLayoutId, ConstraintSet.LEFT,
                0);
        constraintSet.connect(viewId, ConstraintSet.RIGHT, mConstraintLayoutId, ConstraintSet.RIGHT,
                0);
    }

    private void chainViewsVertically(ConstraintSet constraintSet, int topId, int bottomId) {
        constraintSet.connect(bottomId, ConstraintSet.TOP, topId, ConstraintSet.BOTTOM,
                BUTTON_MARGIN);
    }

    public void onButtonClick(View view) {

        if (HAS_BUTTONS) {
            if (view.getId() == mButtonInfos[0].id) {
                startMatch();
                return;
            }

            if (view.getId() == mButtonInfos[mButtonInfos.length - 2].id) {
                undoAction();
                return;
            }

            if (view.getId() == mButtonInfos[mButtonInfos.length - 1].id) {
                endMatch();
                return;
            }

            for (ButtonInfo bi : mButtonInfos) {

                if (view.getId() == bi.id) {
                    Action a = new Action(bi.abbreviation,
                            SystemClock.elapsedRealtime() - mChronometer.getBase());
                    mActionList.add(a);
                    updateActionsListView();
                }
            }
        } else {
            endMatch();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private static class ButtonInfo {
        String name;
        String abbreviation;
        int id;
        Button button;

        public ButtonInfo(String name, String abbreviation, int id, Button button) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.id = id;
            this.button = button;
        }


    }

    private static class SpinnerInfo {
        String name;
        String abbreviation;
        String[] contents;
        String[] contents_abbreviations;
        int id;
        Spinner spinner;

        public SpinnerInfo(
                String name, String abbreviation, String[] contents,
                String[] contents_abbreviations, int id, Spinner spinner) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.contents = contents;
            this.contents_abbreviations = contents_abbreviations;
            this.id = id;
            this.spinner = spinner;
        }

    }
}