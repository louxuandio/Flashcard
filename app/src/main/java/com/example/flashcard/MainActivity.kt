package com.example.flashcard

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashcardTheme
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
    LazyRow (
        modifier = Modifier.fillMaxSize(),
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
                    .height(200.dp)
            ){
                Box(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = question,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
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

