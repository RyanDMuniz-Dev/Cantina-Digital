package com.example.cantinadigital.codigoTelaPerfil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.cantinadigital.ui.navigation.AppNavigation
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

class TelaPerfil: ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {

                CantinaDigitalTheme {

                    // o que executar

                }

            }
        }
}

@Composable
fun Tela(modifier: Modifier = Modifier){
    var typeAccept by remember { mutableStateOf(false) }
    Column{
        /*Row(){
            //barra de cima
        }*/

        Row() {
            Column() {
                //colocar a imagem
                //local pra editar nome do usuário
            }
        }
        //espaço/detalhe de uma linha

        Row(){
            Text( // provavelmente não é só text
                text = "Preferências"
            )
        }

        DarkMode(
            typeAccept = typeAccept,
            backgroundDark = {typeAccept = it},
            modifier = Modifier.padding(bottom = 20.dp)
            )
        /*Row(){
            Column(){
                //modo escuro
                //efeitos
            }
            Column(){
                //permitir notificações
                //permitir atualizações
            }
        }*/

        Spacer(modifier = Modifier.width(10.dp))

        Row(){
            Text(// provavelmente não é só text
                text = "Dados"
            )
        }

        Row(){
            Column(){
                //excluir conta da cantina
            }
            Column(){
                //alterar conta da cantina
            }
        }
        //espaço

        Row(){
            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ){
                Text(
                    text = "Créditos"
                )
            }
            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                )
            ){
                Text(
                    text = "Sair"
                )
            }
        }

        Row(){
            //barra da parte de baixo
        }
    }
}

@Composable
fun DarkMode(
    typeAccept: Boolean,
    backgroundDark: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth().size(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Modo escuro"
        )
        Switch(
            checked = typeAccept,
            onCheckedChange = backgroundDark,
            modifier = modifier.fillMaxWidth().wrapContentWidth(Alignment.Start)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    CantinaDigitalTheme{
        Tela()
    }
}