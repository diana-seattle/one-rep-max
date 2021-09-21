package org.indiv.dls.onerepmax.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.viewmodel.DataInputViewModel
import org.indiv.dls.onerepmax.viewmodel.InputData
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class DataInputDialog : AppCompatDialogFragment() {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    }
    private val dataInputViewModel: DataInputViewModel by viewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val composeView = ComposeView(requireContext())
        composeView.setContent {
            MaterialTheme {
                DataInput(dataInputViewModel, dateFormatter, parentFragmentManager)
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_data_title)
            .setPositiveButton(R.string.button_text_ok) { _, _ -> }
            .setNegativeButton(R.string.button_text_cancel) { _, _ -> }
            .setNeutralButton(R.string.button_text_reset) { _, _ -> }
            .setView(composeView)
            .create()
        dialog.setOnShowListener {
            // prevent closing when validation failure
            // https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dataInputViewModel.saveData()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                dataInputViewModel.resetToFile()
            }
        }

        dataInputViewModel.actionCompletionLiveData.observe(this) {
            dialog.dismiss()
        }

        return dialog
    }
}

@Composable
fun DataInput(
    dataInputViewModel: DataInputViewModel,
    dateFormatter: DateTimeFormatter,
    fragmentManager: FragmentManager
) {

    val inputData: InputData by dataInputViewModel.inputLiveData.observeAsState(InputData())
    val onDataChange: (InputData) -> Unit = { dataInputViewModel.onInputChange(it) }

    Column(Modifier.padding(all = dimensionResource(R.dimen.default_margin))) {
        inputData.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.default_margin))
            )
        }

        // todo: use Spinner with Other option paired with text field
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputData.exerciseName ?: "",
            onValueChange = { if (it.length <= 100) onDataChange(inputData.copy(exerciseName = it)) },
            label = { Text(stringResource(R.string.exercise_name_input_label)) }
        )

        DatePickerView(
            fragmentManager = fragmentManager,
            datePicked = inputData.dateOfWorkout?.format(dateFormatter),
            updatedDate = { date ->
                onDataChange(inputData.copy(dateOfWorkout = date?.let {
                    LocalDate.ofEpochDay(it / 86_400_000)
                }))
            }
        )

        Row(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.default_margin))
        ) {
            IntegerTextField(
                labelResId = R.string.sets_input_label,
                value = inputData.sets,
                onValueChange = { onDataChange(inputData.copy(sets = it)) },
            )
            IntegerTextField(
                labelResId = R.string.reps_input_label,
                value = inputData.reps,
                onValueChange = { onDataChange(inputData.copy(reps = it)) },
            )
            IntegerTextField(
                labelResId = R.string.weight_input_label,
                value = inputData.weight,
                onValueChange = { onDataChange(inputData.copy(weight = it)) },
            )
        }
    }
}

@Composable
fun IntegerTextField(
    labelResId: Int,
    value: UInt?,
    onValueChange: (UInt?) -> Unit,
    maxLength: Int = 3
) {
    OutlinedTextField(
        modifier = Modifier
            .width(80.dp)
            .padding(end = dimensionResource(R.dimen.quarter_margin)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = value?.toString() ?: "",
        onValueChange = {
            if (it.isEmpty()) {
                onValueChange(null)
            } else if (it.length <= maxLength && it.isDigitsOnly()) {
                onValueChange(it.toUInt())
            }
        },
        label = { Text(stringResource(labelResId)) }
    )
}

@Composable
fun DatePickerView(
    fragmentManager: FragmentManager,
    datePicked: String?,
    updatedDate: (date: Long?) -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .padding(top = dimensionResource(R.dimen.default_margin))
            .border(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
            .clickable {
                showDatePicker(fragmentManager, updatedDate)
            }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.default_margin))
        ) {
            val (label, iconView) = createRefs()
            Text(
                text= datePicked ?: stringResource(R.string.exercise_date_input_label),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(label) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(iconView.start)
                        width = Dimension.fillToConstraints
                    }
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .constrainAs(iconView) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}

private fun showDatePicker(
    fragmentManager: FragmentManager,
    updatedDate: (Long?) -> Unit)
{
    val picker = MaterialDatePicker.Builder.datePicker().build()
    picker.show(fragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener {
        updatedDate(it)
    }
}