package com.example.flashcard

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.estimateAnimationDurationMillis
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashcardTheme
import kotlinx.coroutines.delay
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(){
    var context = LocalContext.current
    var flashcards = loadFlashcards(context)
    var showAnswer = remember { mutableStateOf(false) }
    var rotation = remember { mutableStateOf(0f) }

    //ChatGPT teaches me how to do the rotation
    var animatedRotation = animateFloatAsState(
        targetValue = rotation.value,
        animationSpec = tween(durationMillis = 400))

    LaunchedEffect(Unit) {
        while (true){
            delay(15000)
            flashcards=flashcards.shuffled()
        }
    }

    LazyRow (
        modifier = Modifier
            .graphicsLayer {
                rotationX = animatedRotation.value   //In LazyRow, rotationX is vertical rotation
                cameraDistance = 12f * density    //by ChatGPT
            }
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){ 
        items(flashcards){flashcard ->
            var (question,answer) = flashcard
            Card (
                modifier = Modifier
                    .padding(8.dp)
                    .fillParentMaxWidth()
                    .clickable {
                        if (rotation.value==0f){
                            rotation.value=180f
                            showAnswer.value=true
                        }else{
                            rotation.value=0f
                            showAnswer.value=false
                        }
                    }
                    .height(200.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),

            ){
                Box(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentAlignment = Alignment.Center
                ){
                    if (showAnswer.value){
                        Text(
                            text = answer,
                            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.graphicsLayer { rotationX=180f })   //when the card is flipped, the text is also flipped. Set the rotationX of text to avoid that
                    }else{
                        Text(
                            text = question,
                            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
                    }

                }
            }
        }
    }
}

fun loadFlashcards(context: Context):List<Pair<String,String>>{
    val flashcards = mutableListOf<Pair<String, String>>()
    val parser = context.resources.getXml(R.xml.flashcards)

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType == XmlPullParser.START_TAG && parser.name == "card") {
            var question = ""
            var answer = ""

            parser.nextTag()
            if (parser.name == "question") question = parser.nextText()
            parser.nextTag()
            if (parser.name == "answer") answer = parser.nextText()
            flashcards.add(Pair(question, answer))
        }
    }
    parser.close()
    return flashcards
}

