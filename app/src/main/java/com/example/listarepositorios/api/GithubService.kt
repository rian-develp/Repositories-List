package com.example.listarepositorios.api

import com.example.listarepositorios.model.Repository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {

    @GET("users/{user}/repos")
    fun getAllRepositoriesByUser(@Path("user") user: String): Call<List<Repository>>
}