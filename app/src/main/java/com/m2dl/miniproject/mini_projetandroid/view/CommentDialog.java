package com.m2dl.miniproject.mini_projetandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.m2dl.miniproject.mini_projetandroid.R;
import com.m2dl.miniproject.mini_projetandroid.business.DataStorage;

/**
 * Created by kana on 15/01/15.
 */
public class CommentDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Activity activity;
    private Button yes, cancel;
    private String comment;
    private DataStorage sharePreference;

    public CommentDialog(Activity activity) {
        super(activity);

        comment = "";
        this.activity = activity;
        sharePreference = new DataStorage(activity, activity.getResources().getString(R.string.sharedPreferencesFile));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        yes = (Button) findViewById(R.id.comment_ok);
        cancel = (Button) findViewById(R.id.comment_cancel);
        yes.setOnClickListener(this);
        cancel.setOnClickListener(this);
        this.setTitle(R.string.comment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_ok:
                createComment();
                break;
            case R.id.comment_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public void createComment(){
        EditText commentEditText = (EditText) findViewById(R.id.comment_editText);
        comment = commentEditText.getText().toString();
        sharePreference.newSharedPreference("comment",comment);
    }

    public String getComment(){
        return comment;
    }
}
