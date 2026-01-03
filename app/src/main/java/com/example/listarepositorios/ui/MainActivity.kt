package com.example.listarepositorios.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listarepositorios.model.Repository
import com.example.listarepositorios.adapter.MyAdapter
import com.example.listarepositorios.api.GithubService
import com.example.listarepositorios.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var etUsername: TextView
    private lateinit var btnConfirm: Button
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        val api = setupRetrofit()
        handleClickButton(api)
    }

    override fun onResume() {
        super.onResume()
        getDataInSharedPreferences()
    }

    // Método que inicializa os componentes
    private fun initComponents(){
        etUsername = binding.etUsername
        btnConfirm = binding.btnConfirm
        recyclerView = binding.recyclerview
    }

    /*
       Método que ao clicar no botão chama 2 métodos responsáveis respectivamente por
       encontrar os repositórios pela web e salvar o nome do usuário em shared preferences
     */
    private fun handleClickButton(api: GithubService){
        btnConfirm.setOnClickListener {
            getAllRepositories(api)
            saveInSharedPreferences(etUsername.text.toString())
        }
    }

    // Método que configura o retrofit
    private fun setupRetrofit(): GithubService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubService::class.java)
    }

    /*
        Método que configura a recycler view e ao clicar no ícone de
        compartilhar, o app dá a opção de compartilhar o link do repositório
        do usuário com outros apps e ao clicar no card, o usuário é direcionado
        ao link do card selecionado
     */
    private fun setupRecyclerView(list: List<Repository>){
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = MyAdapter(list, this)
        recyclerView.adapter = adapter
        adapter.iconListener = { repository ->
            shareRepositoryLink(repository.html_url)
        }

        adapter.itemListener = { repository ->
            openBrowser(repository.html_url)
        }
    }

    // Método que encontra os repositórios
    private fun getAllRepositories(api: GithubService){
        val username = etUsername.text.toString()
        val response = api.getAllRepositoriesByUser(username)
        response.enqueue(object: Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        setupRecyclerView(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(applicationContext, "Algo deu errado", Toast.LENGTH_LONG).show()
            }

        })
    }

    // Método que compartilha o link com o app que o usuário selecionar
    private fun shareRepositoryLink(url: String){
        val sendIntent = Intent().apply{
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Método que direciona o usuário para a web
    private fun openBrowser(url: String){
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }

    // Método que salva o nome do usuário em shared preferences
    private fun saveInSharedPreferences(userName: String){
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", userName)
        editor.apply()
    }

    /* Método que busca o nome do usuário em shared preferences se existir
        caso não exista ele passa para o EditText uma string vazia
     */
    private fun getDataInSharedPreferences(){
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        Log.v("VERBOSE -> ", username.toString())
        if (username != null) {
            etUsername.text = username
        }
    }
}