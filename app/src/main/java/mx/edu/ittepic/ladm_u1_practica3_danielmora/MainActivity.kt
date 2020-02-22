package mx.edu.ittepic.ladm_u1_practica3_danielmora

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    var vectorStatico = Array<Int>( 10, { 0 })



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat
                .checkSelfPermission(this,android
                    .Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            //Si entra entonces no se otorgaron los permisos
            ActivityCompat.requestPermissions(
                this,//en que proyecto
                arrayOf(       //Arreglo con todos los permisos
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0)//metodo para la comprobacion de solicitud
        }else{
            //si entra aqui,se otorgaron los permisos
            mensaje("Permisos ya otorgados ")
        }



        btnAsignar.setOnClickListener {
            var valorkt=valor.text.toString().toInt()
            var posikt= posicion.text.toString().toInt()

            if(valorkt.toString().isEmpty() || posikt.toString().isEmpty()){
                AlertDialog.Builder(this).setMessage("El valor y la posición no puede estar vacia").show()
                return@setOnClickListener
            }
            if( posikt < 0 || posikt > 9){
                AlertDialog.Builder(this).setMessage("La posición no puede  salir del rango [0-9]").show()
                return@setOnClickListener
            }
        vectorStatico.set(posikt,valorkt)
            valor.setText("")
            posicion.setText("")
        Toast.makeText(this,"se agregó correctamente!",Toast.LENGTH_LONG).show()

        }

        btnMostrar.setOnClickListener {
         var vector=""

            (0..9).forEach {dato->
                vector += "[ ${dato} ] : ${vectorStatico[dato].toString() } \n"
            }
        AlertDialog.Builder(this).setMessage(vector).show()
        }

        btnGuardar.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            }
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED) {
                if(GNombre.text.isEmpty()){
                    AlertDialog.Builder(this).setMessage("Ingresa el nombre del archivo para guardar").setPositiveButton("Ok") {d,i->}
                        .show()
                    return@setOnClickListener
                }
                guardarSD()
                GNombre.setText("")
            }else{
                Toast.makeText(this,"Faltan los permisos",Toast.LENGTH_LONG).show()
            }
            return@setOnClickListener
        }
        btnLeer.setOnClickListener {

            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0)
            }
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED) {
                if(ANombre.text.isEmpty()){
                    AlertDialog.Builder(this).setMessage("Ingresa el nombre del archivo ha abrir").setPositiveButton("Ok") {d,i->}
                        .show()
                    return@setOnClickListener
                }
                abrirSD()
                ANombre.setText("")
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Necesitas dar permisos")
                    .setPositiveButton("Ok") {d,i->}
                    .show()
            }
            return@setOnClickListener

        }
    }


    //////////////////////////////////////Funciones

    fun noSD():Boolean{
        var estado = Environment.getExternalStorageState()
        if(estado != Environment.MEDIA_MOUNTED){
            return true
        }
        return false
    }

    fun guardarSD() {
        if(noSD()){
            AlertDialog.Builder(this).setTitle("Error").setMessage("Inserta una memoria externa")
                .setPositiveButton("Ok") {d,i->}.show()
            return
        }
        var rutaSD = Environment.getExternalStorageDirectory()
        var datosArchivo = File(rutaSD.absolutePath, GNombre.text.toString())
        try {
            var flujoSalida = OutputStreamWriter(FileOutputStream(datosArchivo))
            var vec = ""
            (0..9).forEach {r->
                    if( r == 9){
                        vec +=  vectorStatico[r].toString()
                    }else{
                        vec +=  vectorStatico[r].toString() + "&"
                    }
            }
            flujoSalida.write(vec)
            flujoSalida.flush()
            flujoSalida.close()
            AlertDialog.Builder(this).setTitle("Exito").setMessage("Se ha guardo correctamente").setPositiveButton("Ok") {d,i->}
                .show()
        }catch ( error : IOException){
            AlertDialog.Builder(this).setTitle("Error").setMessage(error.message.toString()).setPositiveButton("Ok") {d,i->}
                .show()
        }
    }

    fun abrirSD() {
        if(noSD()){
            AlertDialog.Builder(this).setTitle("Error").setMessage("Inserta una memoria externa")
                .setPositiveButton("Ok") {d,i->}.show()
            return
        }
        var rutaSD = Environment.getExternalStorageDirectory()
        var datosArchivo = File(rutaSD.absolutePath, ANombre.text.toString())
        try {
            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(datosArchivo)))
            var data = flujoEntrada.readLine()
            var dataSplit = data.split("&")
            (0..9).forEach {
                vectorStatico[it] = dataSplit[it].toInt()
            }
            AlertDialog.Builder(this).setTitle("Exito").setMessage("Se ha cargado correctamente").setPositiveButton("Ok") {d,i->}
                .show()
            flujoEntrada.close()
        }catch ( error : IOException){
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.message.toString())
                .setPositiveButton("Ok") {d,i->}
                .show()
        }
    }
    fun mensaje( m :String){
        AlertDialog.Builder(this)
            .setTitle("Atencion")
            .setMessage(m)
            .setPositiveButton("OK"){d,i->}
            .show()

    }
}
