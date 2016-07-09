package org.faudroids.werewolf.ui;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.faudroids.werewolf.R;

public class InputDialog {

	private final Context context;
	private final View dialogView;
	private final View okBtn, cancelBtn;
	private final TextView errorView;
	private final EditText editText;
	@StringRes private final int title;

	private InputListener inputListener = null;

	public InputDialog(Context context, @StringRes int title, @StringRes int hint) {
		this.context = context;
		this.dialogView = LayoutInflater.from(context).inflate(R.layout.input_dialog, null);
		this.title = title;
		this.editText = (EditText) dialogView.findViewById(R.id.input);
		editText.setHint(hint);
		this.errorView = (TextView) dialogView.findViewById(R.id.error_txt);
		this.okBtn = dialogView.findViewById(R.id.btn_ok);
		this.cancelBtn = dialogView.findViewById(R.id.btn_cancel);
	}

	public void setInputListener(final InputListener inputListener) {
		this.inputListener = inputListener;
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void afterTextChanged(Editable s) {
				ValidationResult result = inputListener.isInputValid(editText.getText().toString());
				if (!result.isValid) errorView.setText(result.errorMsg);
				errorView.setVisibility(!result.isValid ? View.VISIBLE : View.GONE);
				okBtn.setEnabled(result.isValid);
			}
		});
	}

	public void setInitialInput(String initialInput) {
		this.editText.setText(initialInput);
	}

	public void show() {
		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(title)
				.setView(dialogView)
				.show();
		editText.requestFocus();

		final InputMethodManager inputMethodManager = (InputMethodManager) dialogView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (inputListener != null) inputListener.onConfirm(editText.getText().toString());
				inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				dialog.dismiss();
			}
		});

	}

	public interface InputListener {

		ValidationResult isInputValid(String input);
		void onConfirm(String input);

	}

	public static class ValidationResult {

		public final boolean isValid;
		@StringRes public final int errorMsg;

		public ValidationResult(boolean isValid, @StringRes int errorMsg) {
			this.isValid = isValid;
			this.errorMsg = errorMsg;
		}

	}

}
