package com.eozsahin.otpinput

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eozsahin.otpinput.ui.theme.OTPInputTheme
import com.eozsahin.otpinput.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OTPInputTheme {
                // A surface container using the 'background' color from the theme
                PreviewView()
            }
        }
    }
}

data class OneTimePasscodeInputConfig(
    val defaultValue: String = "",
    val type: PasscodeType = PasscodeType.Numeric,
    val length: Int = 4,
) {
    enum class PasscodeType {
        Alphanumeric, Numeric
    }
}

//@Composable
//fun OtpCell(
//    modifier: Modifier,
//    value: String,
//    isCursorVisible: Boolean = false
//) {
//    val scope = rememberCoroutineScope()
//    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }
//
//    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
//        if (isCursorVisible) {
//            scope.launch {
//                delay(350)
//                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
//            }
//        }
//    }
//
//    Box(
//        modifier = modifier
//    ) {
//        Text(
//            text = if (isCursorVisible) cursorSymbol else value,
//            style = MaterialTheme.typography.body1,
//            modifier = Modifier.align(Alignment.Center)
//        )
//    }
//}
//
//@ExperimentalComposeUiApi
//@Composable
//fun PinInput(
//    modifier: Modifier = Modifier,
//    length: Int = 5,
//    value: String = "",
//    onValueChanged: (String) -> Unit
//) {
//    val focusRequester = remember { FocusRequester() }
//    val keyboard = LocalSoftwareKeyboardController.current
//    TextField(
//        value = value,
//        onValueChange = {
//            if (it.length <= length) {
//                if (it.all { c -> c in '0'..'9' }) {
//                    onValueChanged(it)
//                }
//                if (it.length >= length) {
//                    keyboard?.hide()
//                }
//            }
//        },
//        // Hide the text field
//        modifier = Modifier
//            .size(0.dp)
//            .focusRequester(focusRequester),
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.Number
//        )
//    )
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        repeat(length) {
//            OtpCell(
//                modifier = modifier
//                    .size(width = 45.dp, height = 60.dp)
//                    .clip(MaterialTheme.shapes.large)
//                    .background(MaterialTheme.colors.surface)
//                    .clickable {
//                        focusRequester.requestFocus()
//                        keyboard?.show()
//                    },
//                value = value.getOrNull(it)?.toString() ?: "",
//                isCursorVisible = value.length == it
//            )
//            Spacer(modifier = Modifier.size(8.dp))
//        }
//    }
//}

val textColor = Color(0xFF6A6A6A)
val bgColor = Color(0xFFECECEC)
val allDigitsRegex = "\\d*".toRegex()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OneTimePasscodeInput1(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    length: Int = 4,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current


    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Invisible text field to keep track of entered input
        TextField(
            value = value,
            onValueChange = {
                if (it.length <= length && it.matches(allDigitsRegex)) {
                    onValueChanged(it)
                }
                if (it.length == length) {
                    keyboard?.hide()
                }
            },
            // Hide the text field
            modifier = Modifier
                .size(0.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        repeat(length) { i ->
            OneTimePasscodeCell(
                modifier = Modifier
                    .clickable {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    },
                value = value.getOrNull(i)?.toString() ?: ""
            )
        }
    }

}

@Composable
private fun OneTimePasscodeCell(
    modifier: Modifier = Modifier,
    defaultText: String = "#",
    value: String = "",
) {
    Box(
        modifier = modifier
            .width(72.dp)
            .height(80.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp), true)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = value.ifEmpty { defaultText },
            style = LocalTextStyle.current.copy(fontSize = 48.sp, lineHeight = 60.sp,
                textAlign = TextAlign.Center),
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun PreviewView() {
    var value by remember { mutableStateOf("") }
//    PinInput(value = value) {
//        value = it
//    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        //OneTimePasscodeInput()
        OneTimePasscodeInput1(value = value, onValueChanged = {
            value = it
            Log.i("emre", "new value is $it")
        } )
    }
}


//@OptIn(ExperimentalComposeUiApi::class)
//@Preview
//@Composable
//fun OneTimePasscodeInput(
//    config: OneTimePasscodeInputConfig = OneTimePasscodeInputConfig()
//) {
//    val focusManager = LocalFocusManager.current
//    var focusedBox by remember { mutableStateOf(1) }
//
//    val keyboardManager = LocalSoftwareKeyboardController.current
//    val focusRequester = remember { FocusRequester() }
//
//    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//        repeat(config.length) { i ->
//            EditIntegerFieldBox(key = i, initialVal = config.defaultValue, focusRequester = focusRequester,
//                hasFocus = {
//                   focusedBox = i
//                    Log.i("emre", "box has focus: $i")
//                },
//                singleValueAdded = {
//                    if (focusedBox < config.length - 1) {
//                        Log.i("emre", "change focus right")
//                        focusManager.moveFocus(FocusDirection.Right)
//                        //focusedBox++
//                    } else {
//                        keyboardManager?.hide()
//                    }
//                    Log.i("emre", "added focusedBox: $focusedBox")
//                },
//                onValueRemoved = {
//                    if (focusedBox > 0) {
//                        Log.i("emre", "change focus left")
//                        focusManager.moveFocus(FocusDirection.Left)
//                        //focusedBox--
//                    }
//                    Log.i("emre", "removed focusedBox: $focusedBox")
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//private fun EditIntegerFieldBox(
//    key: Int,
//    modifier: Modifier = Modifier,
//    focusRequester: FocusRequester,
//    initialVal: String = "",
//    hasFocus: () -> Unit = {},
//    singleValueAdded: (String) -> Unit = {},
//    onValueRemoved: () -> Unit = {}
//) {
//    var isFocused by remember { mutableStateOf(false) }
//    var value by remember { mutableStateOf(TextFieldValue(initialVal)) }
//    var borderColor = animateColorAsState(targetValue = if (!isFocused) Color(0xFFECECEC) else Color(0xFF6A6A6A))
//
//    TextField(
//        value = value,
//        onValueChange = {
//            val earlier = value.text
//            value = TextFieldValue(it.text[0].toString())
//            Log.i("emre","Value added: ${it.text}")
//            if (value.text != earlier && value.text.isNotEmpty()) {
//                singleValueAdded(it.text)
//            }
//            if (earlier.isNotEmpty() && value.text.isEmpty()) {
//                onValueRemoved()
//            }
//        },
//        placeholder = {
//            //if (!isFocused) {
//                Text(text = "#",
//                    style = LocalTextStyle.current.copy(fontSize = 48.sp, lineHeight = 60.sp,
//                        textAlign = TextAlign.Center), modifier = Modifier.padding(start = 4.dp))
//            //}
//        },
//        modifier = Modifier
//            .width(72.dp)
//            .height(100.dp)
//            .focusRequester(focusRequester)
//            .onFocusChanged {
//                isFocused = it.hasFocus
//                if (isFocused) {
//                    hasFocus()
//                }
//            }
//            .onKeyEvent {
//                if (it.key == Key.Backspace) {
//                    Log.i("emre", "Removing value: ${value.text}")
//                    value = TextFieldValue("")
//                    onValueRemoved()
//                    return@onKeyEvent true
//                }
//                return@onKeyEvent false
//            }
//            .border(width = 2.dp, color = borderColor.value, shape = RoundedCornerShape(8.dp)),
//        singleLine = true,
//        maxLines = 1,
//        colors = TextFieldDefaults.textFieldColors(
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent,
//            textColor = Color(0xFF6A6A6A),
//            backgroundColor = Color(0xFFECECEC),
//            placeholderColor = Color(0xFF6A6A6A),
//            cursorColor = Color.Transparent
//        ),
//        shape = RoundedCornerShape(8.dp),
//        textStyle = LocalTextStyle.current.copy(fontSize = 48.sp, lineHeight = 60.sp, textAlign = TextAlign.Center),
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
//        keyboardActions = KeyboardActions()
//    )
//}



//LaunchedEffect("MFAverification") {
//    navHostController.getSmsVerificationResult { success ->
//        scope.launch {
//            //viewModel.onMFAResult(success)
//            Timber.i("MFA Verification result:  $success")
//            if (success) {
//                Timber.i("MFA verification success, navigating to next screen!!!")
//                delay(300)
//                //inviteFriendsTapped() // not doing anything??
//                navHostController.navigate(NavScreens.InviteFriends.route)
//            }
//        }
//    }
//}
//
//LaunchedEffect(viewModel.requiresMFA.value) {
//    Timber.i("MFA launchedeffect called")
//    if (viewModel.requiresMFA.value) {
//        viewModel.requiresMFA.value = false
//        Timber.i("MFA required navigating to SMS verification")
//        navHostController.navigate(NavScreens.EnterVerificationCode.route)
//    }
//}