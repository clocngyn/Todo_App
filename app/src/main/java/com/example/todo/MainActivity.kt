package com.example.todo

import android.R
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.todo.ui.theme.TodoTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material3.TextField
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("OnCreate is called")
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Screen(modifier = Modifier
                        .padding(innerPadding)
                    )


                }
            }
        }
    }


}

data class Task(val name: String, val isCompleted: Boolean = false)
class TaskViewModel : ViewModel() {
    val tasks = mutableStateListOf<Task>()      // container for our Task objects

    fun insertTask(task:Task) { // inserts a task object into tasks
        tasks.add(task)
    }

    fun deleteTask(task : Task) { // removes a task object from tasks
        tasks.remove(task)
    }

    fun updateTask(task : Task, index : Int, isChecked : Boolean) {  // copies a task object and changes
        tasks[index] = task.copy(isCompleted = isChecked )          //  the isCompleted attribute
    }
}
@Composable
fun Screen(modifier: Modifier) {
    val viewModel: TaskViewModel = viewModel() // create and pass viewmodel
    StatelessComp(modifier, viewModel)
}

@Composable
fun StatelessComp(modifier: Modifier, viewModel: TaskViewModel) {
    val tasks       = viewModel.tasks                                 // reference to container of Tasks
    var inputText   by remember { mutableStateOf("") }    // remember inputted text state

    Column(modifier = modifier) {

        Text("Todo List", fontSize = 32.sp)

        AddTask(viewModel, inputText, { inputText = it } , modifier)


        when {
            tasks.isEmpty() -> {
                Text("No items yet", fontSize = 26.sp)
            }
            tasks.any { !it.isCompleted } -> {          // at least 1 task is not completed
                Text("Items", fontSize = 32.sp)
            }
            // else: all tasks are completed, showing nothing
        }



        for (task in tasks) {
            CreateItem(viewModel, task, false)
        }

        if (tasks.any { it.isCompleted }) { // if there is at least 1 completed item, show text
            Text("Completed Items", fontSize = 32.sp)

            for (task in tasks) {
                CreateItem(viewModel, task, true)
            }
        }
    }

}

@Composable
fun AddTask( // handles input field, and adds a Task object to the array
    viewModel: TaskViewModel,
    text : String,
    onTextChange: (String) -> Unit,
    modifier: Modifier) {

    val context = LocalContext.current

    Row(modifier = Modifier) {

        TextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Enter task") },
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
                .padding(end = 5.dp)
        )
        Button(modifier = Modifier
            .padding(10.dp),

            onClick = {
            val trimmed = text.trim() // get rid of whitespace
            if (trimmed.isNotBlank()) { // check if the task is blank
                //println("success")
                viewModel.insertTask(Task(name = trimmed)) // add a new Task object to array, isCompleted attribute is false by default
                onTextChange("")

            } else { // if it is blank, make a toast
                Toast.makeText(context, "No input", Toast.LENGTH_SHORT).show()
            }


        })
        {
            Text("Add Task")
        }
    }
}

@Composable
fun CreateItem(             // Creates an Item on screen using a Task object
    viewModel: TaskViewModel,
    task: Task,
    completed : Boolean) {

    if (completed != task.isCompleted) return // if the task is out of place, return since we will use this function twice for completed and incompleted tasks
    val index = viewModel.tasks.indexOf(task)
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .border(1.dp, Color.DarkGray)
        .padding(20.dp)
        ,verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = task.name,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f), // fills up whitespace so check goes to the right
            fontSize = 26.sp
        )

        Checkbox(                   // changes isCompleted attribute of Task to isChecked
            checked = task.isCompleted,
            onCheckedChange = { isChecked ->
                viewModel.updateTask(task, index, isChecked)
            }
        )

        IconButton(onClick = { viewModel.deleteTask(task) }) { // removes Task object from our array
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",

            )
        }
    }
}


