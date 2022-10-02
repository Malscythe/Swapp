package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.flexbox.FlexboxLayout;

import Swapp.R;

public class PostItem_S2 extends AppCompatActivity {

    TextView showAdditionalSize, hideAdditionalSize, R1C2, R1C3, R1C4, R1C5;
    CheckBox sizeXS, sizeS, sizeM, sizeL, sizeXL, sizeXXL, sizeXXXL;
    RelativeLayout additionalSizeInterface;
    FlexboxLayout xsRow, sRow, mRow, lRow, xlRow, xxlRow, xxxlRow;
    AutoCompleteTextView m1, m2, m3, m4;
    TextView xs1, xs2, xs3, xs4, s1, s2, s3, s4, med1, med2, med3, med4, l1, l2, l3, l4, xl1, xl2, xl3, xl4, xxl1, xxl2, xxl3, xxl4, xxxl1, xxxl2, xxxl3, xxxl4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_s2);

        sizeXS = findViewById(R.id.size_XS);
        sizeS = findViewById(R.id.size_S);
        sizeM = findViewById(R.id.size_M);
        sizeL = findViewById(R.id.size_L);
        sizeXL = findViewById(R.id.size_XL);
        sizeXXL = findViewById(R.id.size_2XL);
        sizeXXXL = findViewById(R.id.size_3XL);

        xs1 = findViewById(R.id.R2C2);
        xs2 = findViewById(R.id.R2C3);
        xs3 = findViewById(R.id.R2C4);
        xs4 = findViewById(R.id.R2C5);
        s1 = findViewById(R.id.R3C2);
        s2 = findViewById(R.id.R3C3);
        s3 = findViewById(R.id.R3C4);
        s4 = findViewById(R.id.R3C5);
        med1 = findViewById(R.id.R4C2);
        med2 = findViewById(R.id.R4C3);
        med3 = findViewById(R.id.R4C4);
        med4 = findViewById(R.id.R4C5);
        l1 = findViewById(R.id.R5C2);
        l2 = findViewById(R.id.R5C3);
        l3 = findViewById(R.id.R5C4);
        l4 = findViewById(R.id.R5C5);
        xl1 = findViewById(R.id.R6C2);
        xl2 = findViewById(R.id.R6C3);
        xl3 = findViewById(R.id.R6C4);
        xl4 = findViewById(R.id.R6C5);
        xxl1 = findViewById(R.id.R7C2);
        xxl2 = findViewById(R.id.R7C3);
        xxl3 = findViewById(R.id.R7C4);
        xxl4 = findViewById(R.id.R7C5);
        xxxl1 = findViewById(R.id.R8C2);
        xxxl2 = findViewById(R.id.R8C3);
        xxxl3 = findViewById(R.id.R8C4);
        xxxl4 = findViewById(R.id.R8C5);

        R1C2 = findViewById(R.id.R1C2);
        R1C3 = findViewById(R.id.R1C3);
        R1C4 = findViewById(R.id.R1C4);
        R1C5 = findViewById(R.id.R1C5);

        m1 = findViewById(R.id.R1C2Header);
        m2 = findViewById(R.id.R1C3Header);
        m3 = findViewById(R.id.R1C4Header);
        m4 = findViewById(R.id.R1C5Header);

        showAdditionalSize = findViewById(R.id.showAdditionalSize);
        hideAdditionalSize = findViewById(R.id.hideAdditionalSize);

        additionalSizeInterface = findViewById(R.id.additionalSizeInterface);

        xsRow = findViewById(R.id.xsRow);
        sRow = findViewById(R.id.sRow);
        mRow = findViewById(R.id.mRow);
        lRow = findViewById(R.id.lRow);
        xlRow = findViewById(R.id.xlRow);
        xxlRow = findViewById(R.id.xxlRow);
        xxxlRow = findViewById(R.id.xxxlRow);

        String[] measurementsArr = getResources().getStringArray(R.array.measurements);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, measurementsArr);
        m1.setAdapter(arrayAdapter);
        m2.setAdapter(arrayAdapter);
        m3.setAdapter(arrayAdapter);
        m4.setAdapter(arrayAdapter);

        m1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (R1C2.getText().toString().equals(editable.toString()) || R1C3.getText().toString().equals(editable.toString()) || R1C4.getText().toString().equals(editable.toString()) || R1C5.getText().toString().equals(editable.toString())) {
                    Toast.makeText(PostItem_S2.this, "This item is already present in measurement header.", Toast.LENGTH_SHORT).show();

                } else {
                    R1C2.setText(editable);
                }
            }
        });

        m2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((R1C2.getText().toString().equals(editable.toString()) ||
                    R1C3.getText().toString().equals(editable.toString()) ||
                    R1C4.getText().toString().equals(editable.toString()) ||
                    R1C5.getText().toString().equals(editable.toString()))) {

                    Toast.makeText(PostItem_S2.this, "This item is already present in measurement header.", Toast.LENGTH_SHORT).show();

                } else {

                    R1C3.setText(editable);

                }
            }
        });

        m3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (R1C2.getText().toString().equals(editable.toString()) || R1C3.getText().toString().equals(editable.toString()) || R1C4.getText().toString().equals(editable.toString()) || R1C5.getText().toString().equals(editable.toString())) {
                    Toast.makeText(PostItem_S2.this, "This item is already present in measurement header.", Toast.LENGTH_SHORT).show();
                } else {
                    R1C4.setText(editable);
                }
            }
        });

        m4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (R1C2.getText().toString().equals(editable.toString()) || R1C3.getText().toString().equals(editable.toString()) || R1C4.getText().toString().equals(editable.toString()) || R1C5.getText().toString().equals(editable.toString())) {
                    Toast.makeText(PostItem_S2.this, "This item is already present in measurement header.", Toast.LENGTH_SHORT).show();
                } else {
                    R1C5.setText(editable);
                }
            }
        });

        sizeXS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    xsRow.setVisibility(View.VISIBLE);
                } else if (!sizeS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()){
                    showAdditionalSize.setVisibility(View.GONE);
                    xsRow.setVisibility(View.GONE);
                } else {
                    xsRow.setVisibility(View.GONE);
                }

            }
        });

        sizeS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    sRow.setVisibility(View.VISIBLE);
                } else if (!sizeXS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()) {
                    showAdditionalSize.setVisibility(View.GONE);
                    sRow.setVisibility(View.GONE);
                } else {
                    sRow.setVisibility(View.GONE);
                }

            }
        });

        sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    mRow.setVisibility(View.VISIBLE);
                } else  if (!sizeS.isChecked() && !sizeXS.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()){
                    showAdditionalSize.setVisibility(View.GONE);
                    mRow.setVisibility(View.GONE);
                } else {
                    mRow.setVisibility(View.GONE);
                }

            }
        });

        sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    lRow.setVisibility(View.VISIBLE);
                } else if (!sizeS.isChecked() && !sizeM.isChecked() && !sizeXS.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()) {
                    showAdditionalSize.setVisibility(View.GONE);
                    lRow.setVisibility(View.GONE);
                } else {
                    lRow.setVisibility(View.GONE);
                }

            }
        });

        sizeXL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                Log.d("TAG", "" + !sizeL.isChecked());

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    xlRow.setVisibility(View.VISIBLE);
                } else if (!sizeS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXS.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()) {
                    Log.d("TAG", "IN" );
                    showAdditionalSize.setVisibility(View.GONE);
                    xlRow.setVisibility(View.GONE);
                } else {
                    xlRow.setVisibility(View.GONE);
                }

            }
        });

        sizeXXL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    xxlRow.setVisibility(View.VISIBLE);
                } else if (!sizeS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXS.isChecked() && !sizeXXXL.isChecked()) {
                    showAdditionalSize.setVisibility(View.GONE);
                    xxlRow.setVisibility(View.GONE);
                } else {
                    xxlRow.setVisibility(View.GONE);
                }

            }
        });

        sizeXXXL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && hideAdditionalSize.getVisibility() == View.GONE) {
                    showAdditionalSize.setVisibility(View.VISIBLE);
                    xxxlRow.setVisibility(View.VISIBLE);
                } else if (!sizeS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXS.isChecked()) {
                    showAdditionalSize.setVisibility(View.GONE);
                    xxxlRow.setVisibility(View.GONE);
                } else {
                    xxxlRow.setVisibility(View.GONE);
                }

            }
        });

       showAdditionalSize.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               showAdditionalSize.setVisibility(View.GONE);
               hideAdditionalSize.setVisibility(View.VISIBLE);
               additionalSizeInterface.setVisibility(View.VISIBLE);

               sizeXS.setEnabled(false);
               sizeS.setEnabled(false);
               sizeM.setEnabled(false);
               sizeL.setEnabled(false);
               sizeXL.setEnabled(false);
               sizeXXL.setEnabled(false);
               sizeXXXL.setEnabled(false);
           }
       });

        hideAdditionalSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAdditionalSize.setVisibility(View.GONE);
                showAdditionalSize.setVisibility(View.VISIBLE);
                additionalSizeInterface.setVisibility(View.GONE);

                sizeXS.setEnabled(true);
                sizeS.setEnabled(true);
                sizeM.setEnabled(true);
                sizeL.setEnabled(true);
                sizeXL.setEnabled(true);
                sizeXXL.setEnabled(true);
                sizeXXXL.setEnabled(true);
            }
        });
    }
}