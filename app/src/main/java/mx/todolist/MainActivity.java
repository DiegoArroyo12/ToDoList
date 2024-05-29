package mx.todolist;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private ImageButton buttonAddTask;
    private LinearLayout taskContainer;
    private Switch switchActivate;
    private TextView totalTextView;
    private boolean comprasActivado = false;
    private List<EditText> priceEditTexts = new ArrayList<>();
    private List<RelativeLayout> taskLayouts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        taskContainer = findViewById(R.id.taskContainer);
        switchActivate = findViewById(R.id.switch_activate);
        totalTextView = findViewById(R.id.totalTextView);

        // Agregar Tarea con Botón
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

        // Agregar Tarea con Enter
        editTextTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String taskText = editTextTask.getText().toString().trim();
                    if (!taskText.isEmpty()) {
                        addTask(taskText);
                        editTextTask.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        // Función para controlar eventos del Switch
        switchActivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            comprasActivado = isChecked;
            totalTextView.setVisibility(comprasActivado ? View.VISIBLE : View.GONE);
            for (EditText priceEditText : priceEditTexts) {
                priceEditText.setVisibility(comprasActivado ? View.VISIBLE : View.GONE);
            }
            calcularTotal();
        });
    }

    // Función para Crear las Tareas
    private void addTask(String taskText) {
        RelativeLayout taskLayout = new RelativeLayout(this);
        taskLayout.setPadding(8, 8, 8, 8);

        //Text view de Descipcion de tarea
        final TextView taskEditText = new TextView(this);
        taskEditText.setText(taskText);
        taskEditText.setTextSize(18);
        taskEditText.setBackgroundResource(android.R.drawable.edit_text);
        RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editTextParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        editTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
        taskEditText.setLayoutParams(editTextParams);

        // Botón para Editar Tarea
        ImageButton editButton = new ImageButton(this);
        editButton.setImageResource(R.drawable.edit_button);
        editButton.setBackground(null);
        editButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams editButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        editButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        editButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        editButton.setLayoutParams(editButtonParams);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTask(taskEditText);
            }
        });

        // EditText de precio
        final EditText precioEditText = new EditText(this);

        // Botón para Eliminar Tarea
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.delete_button);
        deleteButton.setBackground(null);
        deleteButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams deleteButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButtonParams.addRule(RelativeLayout.LEFT_OF, editButton.getId());
        deleteButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(taskLayout, precioEditText);
            }
        });

        precioEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        precioEditText.setHint("Precio:");
        precioEditText.setVisibility(comprasActivado ? View.VISIBLE : View.GONE);
        // Coloca el contenido del EditText en el Centro
        precioEditText.setGravity(Gravity.CENTER);
        precioEditText.setId(View.generateViewId());
        RelativeLayout.LayoutParams priceEditTextParams = new RelativeLayout.LayoutParams(
                200,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        priceEditTextParams.addRule(RelativeLayout.LEFT_OF,deleteButton.getId());
        priceEditTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
        precioEditText.setLayoutParams(priceEditTextParams);

        // Actualiza el Precio al perder el Foco de precioEditText
        precioEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calcularTotal();
            }
        });

        // Actualiza el Precio al dar Enter en precioEditText
        precioEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    calcularTotal();
                }
                return false;
            }
        });

        taskLayout.addView(taskEditText);
        taskLayout.addView(precioEditText);
        taskLayout.addView(editButton);
        taskLayout.addView(deleteButton);

        taskContainer.addView(taskLayout);
        priceEditTexts.add(precioEditText);
        taskLayouts.add(taskLayout);
    }

    // Función para Editar Tareas
    private void editTask(final TextView taskTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(taskTextView.getText().toString());
        builder.setView(input);
        builder.setTitle("Editar Tarea");
        builder.setPositiveButton("Confirmar", (dialog, which) -> taskTextView.setText(input.getText().toString()));
        builder.setNegativeButton("Regresar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Función para Eliminar Tareas
    private void deleteTask(final RelativeLayout  taskLayout, final EditText priceEditText) {
        taskContainer.removeView(taskLayout);
        priceEditTexts.remove(priceEditText);
        taskLayouts.remove(taskLayout);
        calcularTotal();
    }

    // Función para calcular el Total
    private void calcularTotal() {
        double total = 0.0;
        for (EditText priceEditText : priceEditTexts) {
            String priceText = priceEditText.getText().toString();
            if (!priceText.isEmpty()) {
                try {
                    total += Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        totalTextView.setText("Total: $ " + total);
    }
}