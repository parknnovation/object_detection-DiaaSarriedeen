package com.daniel.detection

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.daniel.detection.databinding.FragmentTranslationBinding
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_translation.*
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("StaticFieldLeak")
private lateinit var binding: FragmentTranslationBinding
@SuppressLint("StaticFieldLeak")
private lateinit var textTranslated: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var leftSpinner: Button
@SuppressLint("StaticFieldLeak")
private lateinit var rightSpinner: Button
@SuppressLint("StaticFieldLeak")
private lateinit var imageMic: Button
private var languagesArray:ArrayList<ModelLanguage> ?= null
private var sourceLanguageCode = "en"
private var sourceLanguageTitle="English"
private var targetLanguageCode = "en"
private var targetLanguageTitle="English"
@SuppressLint("StaticFieldLeak")
private lateinit var goBackBtn2 : Button
private lateinit var translatorOptions: TranslatorOptions
private lateinit var translator: Translator
private lateinit var progressDialog: ProgressDialog






class TranslationFragment  : Fragment() {
    private var languagesArray: ArrayList<ModelLanguage>? = null
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTranslationBinding.inflate(layoutInflater)
        textTranslated = binding.textView2
        leftSpinner = binding.Spinner
        rightSpinner = binding.Spinner2
        imageMic = binding.mic
        goBackBtn2=binding.goBackBtn
        var sourceLanguageCode="en"
        var sourceLanguageTitle="English"
        var targetLanguageCode="en"
        var targetLanguageTitle="English"

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait!")
        progressDialog.setCanceledOnTouchOutside(false)



        addLanguages()

        leftSpinner.setOnClickListener {
            sourceLanguages()
        }

        rightSpinner.setOnClickListener {
            targetLanguages()

        }

        textTranslated.setOnClickListener {
            validateData()
        }



       /* val adapter = context?.let {
            languagesArray?.let { it1 ->
                ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item,
                    it1.toArray()
                )
            }
        }



        adapter?.setDropDownViewResource(android.R.layout.simple_list_item_1)
            leftSpinner.adapter = adapter
            rightSpinner.adapter=adapter




       leftSpinner.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                leftSpinner.prompt=sourceLanguageTitle

                 Toast.makeText(context,"Language: "+ languagesArray?.get(position),Toast.LENGTH_LONG).show( )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }*/


        //now receive data sent from Home Fragment
        val data = fragment.arguments
        if (data != null) {
            textTranslated.text=data.get("output").toString()
        }

        imageMic.setOnClickListener {
             askSpeechInput()
        }




        goBackBtn2.setOnClickListener{
            findNavController().navigate(R.id.action_translationFragment_to_homeFragment2)
        }








        return binding.root

    }



    private fun addLanguages() {
        //first specify that languagesArray is empty, then add each language from TranslateLanguage into the array
        languagesArray = ArrayList()
        val languagesCodeArray = TranslateLanguage.getAllLanguages()
        for (languageCode in languagesCodeArray) {
            val languageTitle = Locale(languageCode).displayLanguage
            Log.d("TAG", "LoadAvailableLanguages: $languageCode")
            Log.d("TAG", "LoadAvailableLanguages: $languageTitle")
            val modelLanguage = ModelLanguage(languageCode, languageTitle)
            languagesArray!!.add(modelLanguage)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun askSpeechInput()
    {

             if(!context?.let { SpeechRecognizer.isRecognitionAvailable(it) }!!)
             {
                 Toast.makeText(context,"Unrecognized speech", Toast.LENGTH_SHORT ).show()
             }
            val intent= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            //Informs the recognizer which speech model to prefer when performing
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"SAY SOMETHING!")
            startActivityForResult(intent, 102)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==102 && resultCode== Activity.RESULT_OK)
        {
            val result= data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null) {
                textTranslated.text= result[0].toString()
            }
        }
    }

    private fun sourceLanguages()
    {
        val popUpMenu= PopupMenu(context, leftSpinner)
        for(index in languagesArray?.indices!!)
        {
            popUpMenu.menu.add(Menu.NONE, index,index,languagesArray!![index].languageTitle)
        }
        //after adding elements to spinner show it
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {menuItem ->
            val position = menuItem.itemId
            sourceLanguageCode= languagesArray!![position].languageCode
            sourceLanguageTitle= languagesArray!![position].languageTitle

            leftSpinner.text = sourceLanguageTitle

            Log.d("TAG", "The chosen language is: $sourceLanguageTitle")

            false

        }
    }

    private fun targetLanguages()
    {
        val popupMenu=PopupMenu(context, rightSpinner)
        for(index in languagesArray?.indices!!)
        {
            popupMenu.menu.add(Menu.NONE,index,index, languagesArray!![index].languageTitle)
        }
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {menuItem ->
            val position = menuItem.itemId
            targetLanguageCode= languagesArray!![position].languageCode
            targetLanguageTitle= languagesArray!![position].languageTitle

            rightSpinner.text= targetLanguageTitle
            Log.d("TAG", "The chosen language is: $targetLanguageTitle")


            false
        }
    }

    private fun validateData()
    {
        var sourceLanguageText = leftSpinner.text.toString().trim()
        if(sourceLanguageText.isEmpty())
        {
            Toast.makeText(context, "NO language chosen!", Toast.LENGTH_LONG).show()
        }
        else{
            progressDialog.setMessage("Processing Language...")
            progressDialog.show()

            translatorOptions = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build()

            translator=Translation.getClient(translatorOptions)
            textTranslated.text= translatorOptions.toString()

        }
    }


}




























































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































