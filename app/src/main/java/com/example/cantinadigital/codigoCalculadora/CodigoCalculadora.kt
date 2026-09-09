package com.example.cantinadigital.codigoCalculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
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
    var operador by remember { mutableStateOf("") }
    val lightBlue = Color( 0xffbbdefb)
    val lightBlueA100 = Color( 0xff80d8ff)
    val indigo100 = Color( 0xffc5cae9)
    val blue = Color( 0xff0d47a1)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row{
            Column(modifier = Modifier.width(250.dp)){
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
                    if(equation != null){
                        result = calculandoExpressao(equation, operador)
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
            Button(
                onClick = {
                    equation += "1"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "1",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "2"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "2",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    equation += "3"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "3",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "÷"
                    operador = "÷"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "÷",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    equation += "4"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "4",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "5"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "5",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    equation += "6"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "6",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "x"
                    operador = "x"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "x",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    equation += "7"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "7",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "8"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "8",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    equation += "9"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "9",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "-"
                    operador = "-"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "-",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    equation += "%"
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
            Button(
                onClick = {
                    equation += "0"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "0",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    equation += "."
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightBlue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = ".",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    equation += "+"
                    operador = "+"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "+",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            calculandoExpressao(equation, operador)
        }
    }
}

private fun calculandoExpressao(expression: String, operador: String): String{
    if(expression.isBlank()) {
        return "0"
    }
    val numeros = expression.split("+", "-", "x", "÷").filter { it.isNotBlank() }

    var result = numeros[0].toDoubleOrNull() ?: return "Error"

    for(numeroTexto in numeros){
        val numero = numeroTexto.toDoubleOrNull() ?: return "Error"

        result = when(operador){
            "+" -> result + numero
            "-" -> result - numero
            "x" -> result * numero
            "÷" -> if(numero != 0.0){
                    result/numero
                } else{
                    return "Error"
                }
            else -> return "Error"
        }
    }
    return result.toString()

    /*val (num1, num2) = expression.split(operador, limit = 2).takeIf { it.size == 2 } ?: return "Error"

    val number1 = num1.toDoubleOrNull() ?: return "Error"
    val number2 = num2.toDoubleOrNull() ?: return "Error"

    val result = when(operador){
        "+" -> number1 + number2
        "-" -> number1 - number2
        "x" -> number1 * number2
        "÷" -> if(number2 != 0.0) number1 / number2 else return "Error"
        else -> return "Error"
    }
    return result.toString()
    */
}

@Preview(showBackground = true)
@Composable
fun CalculadoraPreview(){
    CantinaDigitalTheme {
        Calculadora()
    }
}