package org.faudroids.werewolf.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.faudroids.werewolf.R;

public class NumberPickerView extends LinearLayout {

	private int value;
	private int minValue, maxValue;
	private OnValueChangeListener listener;

	private Button increaseButton, decreaseButton;
	private TextView numberText;

	public NumberPickerView(Context context) {
		super(context);
		setupLayout();
	}

	public NumberPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupLayout();
	}

	public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupLayout();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setupLayout();
	}

	private void setupLayout() {
		View rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_number_picker, this);
		increaseButton = (Button) rootView.findViewById(R.id.btn_increase);
		decreaseButton = (Button) rootView.findViewById(R.id.btn_decrease);
		numberText = (TextView) rootView.findViewById(R.id.txt_number);

		increaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setValue(value + 1);
			}
		});
		decreaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setValue(value - 1);
			}
		});

		minValue = 0;
		maxValue = 0;
		setValue(0);
	}

	public void setValue(int value) {
		if (value == this.value) return;
		if (value < minValue) value = minValue;
		if (value > maxValue) value = maxValue;

		final int oldValue = this.value;
		final int newValue = value;
		this.value = newValue;
		numberText.setText(String.valueOf(newValue));
		increaseButton.setEnabled(newValue != maxValue);
		decreaseButton.setEnabled(newValue != minValue);

		if (listener == null) return;
		post(new Runnable() {
			@Override
			public void run() {
				listener.onValueChange(NumberPickerView.this, oldValue, newValue);
			}
		});
	}

	public int getValue() {
		return value;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void setOnValueChangeListener(OnValueChangeListener listener) {
		this.listener = listener;
	}

	public void removeOnValueChangeListener() {
		this.listener = null;
	}

	public interface OnValueChangeListener {
		void onValueChange(NumberPickerView picker, int oldValue, int newValue);
	}

}
