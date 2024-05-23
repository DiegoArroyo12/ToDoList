package mx.todolist;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private ImageButton buttonAddTask;
    private LinearLayout taskContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        taskContainer = findViewById(R.id.taskContainer);

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskText = editTextTask.getText().toString().trim();
                if (!taskText.isEmpty()) {
                    addTask(taskText);
                    editTextTask.setText("");
                }
            }
        });
    }

    private void addTask(String taskText) {
        RelativeLayout taskLayout = new RelativeLayout(this);
        taskLayout.setPadding(8, 8, 8, 8);

        final TextView taskEditText = new TextView(this);
        taskEditText.setText(taskText);
        taskEditText.setTextSize(18);
        taskEditText.setBackgroundResource(android.R.drawable.edit_text);
        RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editTextParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        editTextParams.addRule(RelativeLayout.LEFT_OF, View.generateViewId());
        taskEditText.setLayoutParams(editTextParams);

        ImageButton editButton = new ImageButton(this);
        editButton.setImageResource(R.drawable.edit_button);
        editButton.setBackground(null);
        editButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams editButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        editButton.setLayoutParams(editButtonParams);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTask(taskEditText);
            }
        });

        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.delete_button);
        deleteButton.setBackground(null);
        RelativeLayout.LayoutParams deleteButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButtonParams.addRule(RelativeLayout.LEFT_OF, editButton.getId());
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(taskLayout);
            }
        });


        taskLayout.addView(taskEditText);
        taskLayout.addView(editButton);
        taskLayout.addView(deleteButton);

        taskContainer.addView(taskLayout);
    }
    private void editTask(final TextView taskTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(taskTextView.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> taskTextView.setText(input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteTask(final RelativeLayout taskTextView) {
        taskContainer.removeView(taskTextView);
    }
}