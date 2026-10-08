package com.example.cantinadigital.codigoCalculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

class CodigoCalculadora: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CantinaDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Calculadora()
                }
            }
        }
    }
}

@Composable
fun Calculadora(modifier: Modifier = Modifier){
    var equation by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var percent by remember { mutableStateOf("") }
    val lightBlue = Color( 0xffbbdefb)
    val lightBlueA100 = Color( 0xff80d8ff)
    val indigo100 = Color( 0xffc5cae9)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(1.4F)
        ) {
            Row {
                Column(modifier = Modifier.width(250.dp)) {
                    Text(
                        text = equation,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = result,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(75.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        equation = ""
                        result = "0"
                        percent = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = indigo100,
                        contentColor = Color.Black
                    ),
                    shape = RectangleShape,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = "C",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (equation != null) {
                            result = calculandoExpressao(equation, percent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightBlueA100
                    ),
                    shape = RectangleShape,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = "=",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoNumeros("1") {
                    equation += "1"
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoNumeros("2") {
                    equation += "2"
                }
                Spacer(modifier = Modifier.width(5.dp))

                BotaoNumeros("3") {
                    equation += "3"
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoSinais("÷") {
                    equation += "÷"
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoNumeros("4") {
                    equation += "4"
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoNumeros("5") {
                    equation += "5"
                }
                Spacer(modifier = Modifier.width(5.dp))

                BotaoNumeros("6") {
                    equation += "6"
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoSinais("x") {
                    equation += "x"
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BotaoNumeros("7") {
                    equation += "7"
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoNumeros("8") {
                    equation += "8"
                }
                Spacer(modifier = Modifier.width(5.dp))

                BotaoNumeros("9") {
                    equation += "9"
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoSinais("-") {
                    equation += "-"
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        equation += "%"
                        percent = "%"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightBlue
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = "%",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoNumeros("0") {
                    equation += "0"
                }
                Spacer(modifier = Modifier.width(5.dp))

                BotaoNumeros(".") {
                    equation += "."
                }
                Spacer(modifier = Modifier.width(4.dp))

                BotaoSinais("+") {
                    equation += "+"
                }
            }
        }
    }
}

private fun calculandoExpressao(expression: String, percent: String = ""): String{
    if(expression.isBlank()) {
        return "0"
    }
    val numeros = expression.split("+", "-", "x", "÷", "%").filter { it.isNotBlank() }

    val operadores = expression.filter {
        it == '+' ||
                it == '-' ||
                it == 'x' ||
                it == '÷'
    }
    if(numeros.isEmpty()){
        return "Error"
    }
    var result = numeros[0].toDoubleOrNull() ?: return "Error"

    var posicao = 0

    for(numeroTexto in numeros) {
        if (posicao == 0) {
            posicao++
        } else {
            val numero = numeroTexto.toDoubleOrNull() ?: return "Error"

            result = when (operadores[posicao - 1]){
                '+' -> {
                    if(percent == "%"){
                        result + (result * numero /100)
                    } else{
                        result + numero
                    }
                }
                '-' -> {
                    if(percent == "%"){
                        result - (result * numero /100)
                    } else{
                        result - numero
                    }
                }
                'x' -> {
                    if(percent == "%"){
                        result * (numero /100)
                    } else{
                        result * numero
                    }
                }
                '÷' -> {
                    if(percent == "%"){
                        result / (numero /100)
                    } else{
                        if(numero != 0.0) {
                            result / numero
                        } else{
                            return "Error"
                        }
                    }
                }
                else -> return "Error"
            }
            posicao++
        }
    }
    return result.toString()
    //return "%.2f".format(result).replace(",", ".")
}

@Composable
fun BotaoNumeros(num: String, onClick: () -> Unit){
    val lightBlue = Color( 0xffbbdefb)
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = lightBlue
        ),
        shape = CircleShape
    ) {
        Text(
            text = num,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun BotaoSinais(sinal: String, onClick: () -> Unit){
    val blue = Color( 0xff0d47a1)
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = blue
        ),
        shape = CircleShape
    ) {
        Text(
            text = sinal,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculadoraPreview(){
    CantinaDigitalTheme {
        Calculadora()
    }
}