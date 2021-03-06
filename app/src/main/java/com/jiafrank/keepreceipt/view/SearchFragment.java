package com.jiafrank.keepreceipt.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Category;
import com.jiafrank.keepreceipt.service.TextFormatService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import io.realm.Realm;

import static com.jiafrank.keepreceipt.Constants.MULTIPLE_CATEGORY_SELECTION_ALLOWED_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.PICK_CATEGORY;
import static com.jiafrank.keepreceipt.Constants.SEARCH_CATEGORIES_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_DATE_FORMAT;
import static com.jiafrank.keepreceipt.Constants.SEARCH_KEYWORDS_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MAX_DATE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MAX_PRICE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MIN_DATE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MIN_PRICE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SELECTED_CATEGORY_INTENT_NAME;

public class SearchFragment extends Fragment {

    // UI references
    private View rootView;
    private Activity parentActivity;
    private ActionBar actionBar;
    private TextInputEditText keywordsInput;
    private TextInputEditText categoriesInput;
    private TextInputEditText maxPriceInput;
    private TextInputLayout maxPriceInputLayout;
    private TextInputEditText minPriceInput;
    private TextInputLayout minPriceInputLayout;
    private TextInputEditText maxDateInput;
    private TextInputLayout maxDateInputLayout;
    private TextInputEditText minDateInput;
    private TextInputLayout minDateInputLayout;
    private ImageButton clearMaxDateInputButton;
    private ImageButton clearMinDateInputButton;
    private Button searchButton;

    // State Variables
    private Calendar nowCalendar = Calendar.getInstance(Locale.getDefault());
    private Calendar maxStatedCalendar;
    private Calendar minStatedCalendar;
    private ArrayList<String> statedCategoryStrings;
    private String statedKeywords;
    private Double statedMaxPrice;
    private Double statedMinPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        parentActivity = getActivity();

        // Initialize views
        actionBar = ((HomeActivity) parentActivity).getSupportActionBar();
        keywordsInput = rootView.findViewById(R.id.searchKeywordsInput);
        categoriesInput = rootView.findViewById(R.id.searchCategoryInput);
        maxPriceInput = rootView.findViewById(R.id.searchMaxPriceInput);
        maxPriceInputLayout = rootView.findViewById(R.id.searchMaxPriceInputLayout);
        minPriceInput = rootView.findViewById(R.id.searchMinPriceInput);
        minPriceInputLayout = rootView.findViewById(R.id.searchMinPriceInputLayout);
        maxDateInput = rootView.findViewById(R.id.searchMaxDateInput);
        maxDateInputLayout = rootView.findViewById(R.id.searchMaxDateInputLayout);
        minDateInput = rootView.findViewById(R.id.searchMinDateInput);
        minDateInputLayout = rootView.findViewById(R.id.searchMinDateInputLayout);
        clearMaxDateInputButton = rootView.findViewById(R.id.maxDateClearButton);
        clearMinDateInputButton = rootView.findViewById(R.id.minDateClearButton);
        searchButton = rootView.findViewById(R.id.searchButton);

        setUpUI();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setUpUI() {

        // Set new title
        actionBar.setTitle(getString(R.string.search_title));

        /**
         * Set Up Date Inputs
         */
        // First hide the clear date buttons
        showOrHideClearDateButtons();
        // MAX DATE
        // Set clear button listener
        clearMaxDateInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxStatedCalendar = null;
                maxDateInput.setText(null);
                showOrHideClearDateButtons();
            }
        });
        // Set listener for setting date
        final DatePickerDialog.OnDateSetListener onMaxDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (maxStatedCalendar == null) {
                    maxStatedCalendar = Calendar.getInstance(Locale.getDefault());
                }
                maxStatedCalendar.set(year, month, dayOfMonth);
                maxDateInput.setText(TextFormatService.getFormattedStringFromDate(maxStatedCalendar.getTime(), true));
                showOrHideClearDateButtons();
            }
        };
        // Initialize touch listener on the date input
        maxDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarToUse = maxStatedCalendar == null ? nowCalendar : maxStatedCalendar;
                new DatePickerDialog(parentActivity, onMaxDateSetListener,
                        calendarToUse.get(Calendar.YEAR), calendarToUse.get(Calendar.MONTH), calendarToUse.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // MIN DATE
        // Set clear button listener
        clearMinDateInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minStatedCalendar = null;
                minDateInput.setText(null);
                showOrHideClearDateButtons();
            }
        });
        // Set listener for setting date
        final DatePickerDialog.OnDateSetListener onMinDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (minStatedCalendar == null) {
                    minStatedCalendar = Calendar.getInstance(Locale.getDefault());
                }
                minStatedCalendar.set(year, month, dayOfMonth);
                minDateInput.setText(TextFormatService.getFormattedStringFromDate(minStatedCalendar.getTime(), true));
                showOrHideClearDateButtons();
            }
        };
        // Initialize touch listener on the date input
        minDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarToUse = minStatedCalendar == null ? nowCalendar : minStatedCalendar;
                new DatePickerDialog(parentActivity, onMinDateSetListener,
                        calendarToUse.get(Calendar.YEAR), calendarToUse.get(Calendar.MONTH), calendarToUse.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        /**
         * Set Up Category input
         */
        categoriesInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity to pick category
                Intent intent = new Intent(parentActivity, PickCategoryActivity.class);
                // We allow selection of multiple categories
                intent.putExtra(MULTIPLE_CATEGORY_SELECTION_ALLOWED_INTENT_NAME, true);
                intent.putStringArrayListExtra(SELECTED_CATEGORY_INTENT_NAME, null == statedCategoryStrings ? new ArrayList<String>() : new ArrayList<>(statedCategoryStrings));
                startActivityForResult(intent, PICK_CATEGORY);
            }
        });
        // By default, all categories are chosen
        categoriesInput.setText(getString(R.string.search_categories_any_placeholder));

        /**
         * Finally, set up the listener for search
         */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                getInputs();
                // Validate, then construct query string
                if (validateInputsOrShowError()) {
                    // Pass all params to another activity
                    Intent goToResultsActivity = new Intent(parentActivity, SearchResultsActivity.class);
                    goToResultsActivity.putExtra(SEARCH_KEYWORDS_INTENT_NAME, statedKeywords);
                    goToResultsActivity.putStringArrayListExtra(SEARCH_CATEGORIES_INTENT_NAME, statedCategoryStrings);
                    goToResultsActivity.putExtra(SEARCH_MAX_PRICE_INTENT_NAME, statedMaxPrice == null ? null : String.valueOf(statedMaxPrice));
                    goToResultsActivity.putExtra(SEARCH_MIN_PRICE_INTENT_NAME, statedMinPrice == null ? null : String.valueOf(statedMinPrice));
                    goToResultsActivity.putExtra(SEARCH_MAX_DATE_INTENT_NAME, maxStatedCalendar == null ? null : (new SimpleDateFormat(SEARCH_DATE_FORMAT, Locale.getDefault())).format(maxStatedCalendar.getTime()));
                    goToResultsActivity.putExtra(SEARCH_MIN_DATE_INTENT_NAME, minStatedCalendar == null ? null : (new SimpleDateFormat(SEARCH_DATE_FORMAT, Locale.getDefault())).format(minStatedCalendar.getTime()));
                    parentActivity.startActivity(goToResultsActivity);
                }
            }
        });

    }

    /**
     * This either shows or hides the clear date button
     */
    private void showOrHideClearDateButtons() {

        // Max Stated Calendar
        if (maxStatedCalendar == null) {
            clearMaxDateInputButton.setVisibility(View.GONE);
        } else {
            clearMaxDateInputButton.setVisibility(View.VISIBLE);
        }

        // Min Stated Calendar
        if (minStatedCalendar == null) {
            clearMinDateInputButton.setVisibility(View.GONE);
        } else {
            clearMinDateInputButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Returns true if everything is OK
     */
    private boolean validateInputsOrShowError() {
        boolean error = false;

        // If min price is greater than max price
        if (statedMaxPrice != null && statedMinPrice != null && statedMinPrice > statedMaxPrice) {
            maxPriceInputLayout.setErrorEnabled(true);
            maxPriceInputLayout.setError(getString(R.string.search_price_error_message));
            minPriceInputLayout.setErrorEnabled(true);
            error = true;
        } else {
            maxPriceInputLayout.setErrorEnabled(false);
            maxPriceInputLayout.setError(null);
            minPriceInputLayout.setErrorEnabled(false);
        }

        // If min date is greater than max date
        if (minStatedCalendar != null && maxStatedCalendar != null && minStatedCalendar.after(maxStatedCalendar)) {
            maxDateInputLayout.setErrorEnabled(true);
            maxDateInputLayout.setError(getString(R.string.search_date_error_message));
            minDateInputLayout.setErrorEnabled(true);
            error = true;
        } else {
            maxDateInputLayout.setErrorEnabled(false);
            maxDateInputLayout.setError(null);
            minDateInputLayout.setErrorEnabled(false);
        }

        // TODO can also check whether either of the calendars are later than the current date

        return !error;
    }

    /**
     * Retrieves all the other inputs
     * Does not touch DATE or CATEGORIES - these are set elsewhere!
     */
    private void getInputs() {

        // Keywords
        Editable keywordsInputText = keywordsInput.getText();
        statedKeywords = (keywordsInputText == null || keywordsInputText.toString().isEmpty()) ? null : keywordsInputText.toString();

        // Price
        Editable minPriceInputText = minPriceInput.getText();
        statedMinPrice = (minPriceInputText == null || minPriceInputText.toString().isEmpty()) ? null : Double.valueOf(minPriceInputText.toString());
        Editable maxPriceInputText = maxPriceInput.getText();
        statedMaxPrice = (maxPriceInputText == null || maxPriceInputText.toString().isEmpty()) ? null : Double.valueOf(maxPriceInputText.toString());

    }

    /**
     * Clears all state variables and resets inputs
     */
    private void resetForm() {
        minStatedCalendar = null;
        maxStatedCalendar = null;
        statedMinPrice = null;
        statedMaxPrice = null;
        statedKeywords = null;
        statedCategoryStrings = new ArrayList<>();

        keywordsInput.setText("");
        categoriesInput.setText(getString(R.string.search_categories_any_placeholder));
        minPriceInput.setText("");
        maxPriceInput.setText("");
        minDateInput.setText("");
        maxDateInput.setText("");

        // This resets any errors
        validateInputsOrShowError();
        // This resets clear buttons
        showOrHideClearDateButtons();

    }

    /**
     * Used in returning from PickCategory
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CATEGORY && data != null) {
            // This will always be passed back, but will be empty if nothing has been picked
            List<String> pickedCategories = data.getStringArrayListExtra(SELECTED_CATEGORY_INTENT_NAME);
            statedCategoryStrings = new ArrayList<>(pickedCategories);
            // Construct a string to show - this can eventually be extracted as its own method if we have multiple category selection elsewhere
            if (statedCategoryStrings.isEmpty()) {
                categoriesInput.setText(getString(R.string.search_categories_any_placeholder));
            } else {
                categoriesInput.setText(String.join(", ", statedCategoryStrings));
            }
        }
    }

    /**
     * Options menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_reset_menu, menu);

        // Set up reset listener
        MenuItem resetItem = menu.findItem(R.id.action_search_reset);
        resetItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                resetForm();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
